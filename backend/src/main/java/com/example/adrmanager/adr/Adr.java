package com.example.adrmanager.adr;

import com.example.adrmanager.adl.Adl;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;

@Entity
@Table(name = "adrs")
public class Adr {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "business_identifier", nullable = false, unique = true, updatable = false)
	private String businessIdentifier;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "adl_id", foreignKey = @ForeignKey(name = "fk_adrs_adl"))
	private Adl adl;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String context;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String problem;

	@Column(name = "options_considered", columnDefinition = "TEXT")
	private String optionsConsidered;

	@Column(columnDefinition = "TEXT")
	private String decision;

	@Column(columnDefinition = "TEXT")
	private String consequences;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AdrStatus status = AdrStatus.DRAFT;

	@Column(name = "author_user_id", nullable = false, updatable = false)
	private String authorUserId;

	@Column(name = "author_display_name", nullable = false, updatable = false)
	private String authorDisplayName;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "updated_by_user_id")
	private String updatedByUserId;

	@Column(name = "updated_by_display_name")
	private String updatedByDisplayName;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@Column(name = "submitted_by_user_id")
	private String submittedByUserId;

	@Column(name = "submitted_at")
	private Instant submittedAt;

	@Column(name = "decided_by_user_id")
	private String decidedByUserId;

	@Column(name = "decided_by_display_name")
	private String decidedByDisplayName;

	@Column(name = "decided_at")
	private Instant decidedAt;

	@Column(name = "decision_comment", columnDefinition = "TEXT")
	private String decisionComment;

	@Column(name = "rejection_justification", columnDefinition = "TEXT")
	private String rejectionJustification;

	@Version
	private long version;

	protected Adr() {
	}

	public Adr(String businessIdentifier, Adl adl, String title, String context, String problem, String authorUserId,
			String authorDisplayName, Instant now) {
		this.businessIdentifier = businessIdentifier;
		this.adl = adl;
		this.title = title;
		this.context = context;
		this.problem = problem;
		this.authorUserId = authorUserId;
		this.authorDisplayName = authorDisplayName;
		this.createdAt = now;
		this.updatedAt = now;
	}

	public Long getId() {
		return id;
	}

	public String getBusinessIdentifier() {
		return businessIdentifier;
	}

	public Adl getAdl() {
		return adl;
	}

	public String getTitle() {
		return title;
	}

	public String getContext() {
		return context;
	}

	public String getProblem() {
		return problem;
	}

	public String getOptionsConsidered() {
		return optionsConsidered;
	}

	public String getDecision() {
		return decision;
	}

	public String getConsequences() {
		return consequences;
	}

	public String getAuthorUserId() {
		return authorUserId;
	}

	public String getAuthorDisplayName() {
		return authorDisplayName;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public AdrStatus getStatus() {
		return status;
	}

	public String getSubmittedByUserId() {
		return submittedByUserId;
	}

	public Instant getSubmittedAt() {
		return submittedAt;
	}

	public String getDecidedByUserId() {
		return decidedByUserId;
	}

	public String getDecidedByDisplayName() {
		return decidedByDisplayName;
	}

	public Instant getDecidedAt() {
		return decidedAt;
	}

	public String getDecisionComment() {
		return decisionComment;
	}

	public String getRejectionJustification() {
		return rejectionJustification;
	}

	public long getVersion() {
		return version;
	}

	public void setDecisionContent(String optionsConsidered, String decision, String consequences) {
		this.optionsConsidered = optionsConsidered;
		this.decision = decision;
		this.consequences = consequences;
	}

	void update(String title, String context, String problem, String optionsConsidered, String decision,
			String consequences, String updatedByUserId, String updatedByDisplayName, Instant now) {
		this.title = title;
		this.context = context;
		this.problem = problem;
		this.optionsConsidered = optionsConsidered;
		this.decision = decision;
		this.consequences = consequences;
		this.updatedByUserId = updatedByUserId;
		this.updatedByDisplayName = updatedByDisplayName;
		this.updatedAt = now;
	}

	void submit(String submittedByUserId, Instant now) {
		this.status = AdrStatus.UNDER_REVIEW;
		this.submittedByUserId = submittedByUserId;
		this.submittedAt = now;
		this.updatedAt = now;
	}

	void cancelReview(Instant now) {
		this.status = AdrStatus.DRAFT;
		this.updatedAt = now;
	}

	public void markApproved(String approverUserId, String approverDisplayName, Instant decidedAt) {
		this.status = AdrStatus.APPROVED;
		this.decidedByUserId = approverUserId;
		this.decidedByDisplayName = approverDisplayName;
		this.decidedAt = decidedAt;
		this.updatedAt = decidedAt;
	}

	void approve(String approverUserId, String approverDisplayName, String comment, Instant now) {
		markApproved(approverUserId, approverDisplayName, now);
		this.decisionComment = comment;
	}

	void reject(String approverUserId, String approverDisplayName, String comment, String justification, Instant now) {
		this.status = AdrStatus.REJECTED;
		this.decidedByUserId = approverUserId;
		this.decidedByDisplayName = approverDisplayName;
		this.decidedAt = now;
		this.decisionComment = comment;
		this.rejectionJustification = justification;
		this.updatedAt = now;
	}

}
