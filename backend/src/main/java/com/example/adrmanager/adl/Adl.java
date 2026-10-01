package com.example.adrmanager.adl;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "adls")
public class Adl {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "business_identifier", nullable = false, unique = true, updatable = false)
	private String businessIdentifier;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String context;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String problem;

	@Column(name = "created_by_user_id", nullable = false, updatable = false)
	private String createdByUserId;

	@Column(name = "created_by_display_name", nullable = false, updatable = false)
	private String createdByDisplayName;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "updated_by_user_id")
	private String updatedByUserId;

	@Column(name = "updated_by_display_name")
	private String updatedByDisplayName;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@Column(name = "archived_at")
	private Instant archivedAt;

	@Version
	private long version;

	@ElementCollection
	@CollectionTable(name = "adl_tags", joinColumns = @JoinColumn(name = "adl_id"))
	@Column(name = "tag", nullable = false)
	private Set<String> tags = new LinkedHashSet<>();

	protected Adl() {
	}

	public Adl(String businessIdentifier, String title, String context, String problem, String createdByUserId,
			String createdByDisplayName, Instant now) {
		this.businessIdentifier = businessIdentifier;
		this.title = title;
		this.context = context;
		this.problem = problem;
		this.createdByUserId = createdByUserId;
		this.createdByDisplayName = createdByDisplayName;
		this.createdAt = now;
		this.updatedAt = now;
	}

	public Long getId() {
		return id;
	}

	public String getBusinessIdentifier() {
		return businessIdentifier;
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

	public String getCreatedByUserId() {
		return createdByUserId;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public long getVersion() {
		return version;
	}

	public Set<String> getTags() {
		return Set.copyOf(tags);
	}

	public void setTags(Set<String> tags) {
		this.tags = new LinkedHashSet<>(tags);
	}

	public Instant getArchivedAt() {
		return archivedAt;
	}

	public boolean isArchived() {
		return archivedAt != null;
	}

	void update(String title, String context, String problem, Set<String> tags, String updatedByUserId,
			String updatedByDisplayName, Instant now) {
		this.title = title;
		this.context = context;
		this.problem = problem;
		this.tags = new LinkedHashSet<>(tags);
		this.updatedByUserId = updatedByUserId;
		this.updatedByDisplayName = updatedByDisplayName;
		this.updatedAt = now;
	}

	public void markArchived(Instant now) {
		this.archivedAt = now;
		this.updatedAt = now;
	}

}
