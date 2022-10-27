package iob.Boundaries;

import java.util.Date;
import java.util.Map;

import iob.basics.ActivityId;
import iob.basics.Instance;
import iob.basics.InvokedBy;

public class ActivityBoundary {
	// Fields
	private ActivityId activityId;
	private String type;
	private Instance instance;
	private Date createdTimestamp;
	private InvokedBy invokedBy;
	private Map<String, Object> activityAttributes;

	// Constructors
	public ActivityBoundary() {
	}

	public ActivityBoundary(ActivityId activityId, String type, Instance instance, Date createdTimestamp,
			InvokedBy invokedBy, Map<String, Object> activityAttributes) {
		super();

		setActivityId(activityId);
		setType(type);
		setInstance(instance);
		setCreatedTimestamp(createdTimestamp);
		setInvokedBy(invokedBy);
		setActivityAttributes(activityAttributes);
	}

	// Getters & Setters
	public ActivityId getActivityId() {
		return activityId;
	}

	public void setActivityId(ActivityId activityId) {
		this.activityId = activityId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Instance getInstance() {
		return instance;
	}

	public void setInstance(Instance instance) {
		this.instance = instance;
	}

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public InvokedBy getInvokedBy() {
		return invokedBy;
	}

	public void setInvokedBy(InvokedBy invokedBy) {
		this.invokedBy = invokedBy;
	}

	public Map<String, Object> getActivityAttributes() {
		return activityAttributes;
	}

	public void setActivityAttributes(Map<String, Object> activityAttributes) {
		this.activityAttributes = activityAttributes;
	}

	// Methods
	@Override
	public String toString() {
		return "ActivityBoundary [ActivityId=" + activityId + ", type=" + type + ", instance=" + instance
				+ ", createdTimestamp=" + createdTimestamp + ", invokedBy=" + invokedBy + ", activityAttributes="
				+ activityAttributes + "]";
	}
}