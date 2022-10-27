package iob.data;

import java.util.Date;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import iob.basics.ActivityId;

@Document(collection = "Activites")
public class ActivityEntity {
	// Fields
	@Id
	private ActivityId _id;
	private String type;
	private String instanceDomain;
	private String instanceId;
	private Date createdTimestamp;
	private String invokerDomain;
	private String invokerEmail;
	private String activityAttributes;

	// Constructors
	public ActivityEntity() {
	}

	public ActivityEntity(ActivityId activityId, String type, String instanceDomain, String instanceId,
			Date createdTimestamp, String invokerDomain, String invokerEmail, String activityAttributes) {
		super();

		setActivityId(activityId);
		setType(type);
		setInstanceDomain(instanceDomain);
		setInstanceId(instanceId);
		setCreatedTimestamp(createdTimestamp);
		setInvokerDomain(invokerDomain);
		setInvokerEmail(invokerEmail);
		setActivityAttributes(activityAttributes);
	}

	// Getters & Setters
	public ActivityId getActivityId() {
		return _id;
	}

	public void setActivityId(ActivityId activityId) {
		this._id = activityId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInstanceDomain() {
		return instanceDomain;
	}

	public void setInstanceDomain(String instanceDomain) {
		this.instanceDomain = instanceDomain;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public String getInvokerDomain() {
		return invokerDomain;
	}

	public void setInvokerDomain(String invokerDomain) {
		this.invokerDomain = invokerDomain;
	}

	public String getInvokerEmail() {
		return invokerEmail;
	}

	public void setInvokerEmail(String invokerEmail) {
		this.invokerEmail = invokerEmail;
	}

	public String getActivityAttributes() {
		return activityAttributes;
	}

	public void setActivityAttributes(String activityAttributes) {
		this.activityAttributes = activityAttributes;
	}

	// Methods
	@Override
	public String toString() {
		return "ActivityEntity [activityId=" + _id + ", type=" + type + ", instanceDomain=" + instanceDomain
				+ ", instanceId=" + instanceId + ", createdTimestamp=" + createdTimestamp + ", invokerDomain="
				+ invokerDomain + ", invokerEmail=" + invokerEmail + ", activityAttributes=" + activityAttributes + "]";
	}
}