package iob.Boundaries;

import java.util.Date;
import java.util.Map;

import iob.basics.CreatedBy;
import iob.basics.InstanceId;
import iob.basics.Location;

public class InstanceBoundary {
	// Fields
	private InstanceId instanceId;
	private String type;
	private String name;
	private Boolean active;
	private Date createdTimestamp;
	private CreatedBy createdBy;
	private Location location;
	private Map<String, Object> instanceAttributes;

	// Constructors
	public InstanceBoundary() {
	}

	public InstanceBoundary(InstanceId instanceId, String type, String name, Boolean active, Date createdTimestamp,
			CreatedBy createdBy, Location location, Map<String, Object> instanceAttributes) {
		super();

		setInstanceId(instanceId);
		setType(type);
		setName(name);
		setActive(active);
		setCreatedTimestamp(createdTimestamp);
		setCreatedBy(createdBy);
		setLocation(location);
		setInstanceAttributes(instanceAttributes);
	}

	// Getters & Setters
	public InstanceId getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(InstanceId instanceId) {
		this.instanceId = instanceId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public CreatedBy getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(CreatedBy createdBy) {
		this.createdBy = createdBy;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Map<String, Object> getInstanceAttributes() {
		return instanceAttributes;
	}

	public void setInstanceAttributes(Map<String, Object> instanceAttributes) {
		this.instanceAttributes = instanceAttributes;
	}

	// Methods
	@Override
	public String toString() {
		return "InstanceBoundary [InstanceId=" + instanceId + ", type=" + type + ", name=" + name + ", active=" + active
				+ ", createdTimestamp=" + createdTimestamp + ", createdBy=" + createdBy + ", location=" + location
				+ ", instanceAttributes=" + instanceAttributes + "]";
	}
}