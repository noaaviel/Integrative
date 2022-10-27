package iob.data;

import java.util.Date;

import javax.persistence.Id;

import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;


import iob.basics.InstanceId;
import iob.basics.Location;

@Document(collection = "Instances")
public class InstanceEntity {
	// Fields
	@Id
	private InstanceId _id;
	private String type;
	private String name;
	private Boolean active;
	private Date createdTimestamp;
	private String createdByDomain;
	private String createdByEmail;
	private String instanceAttributes;
	@GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
	private Point location;

	// Constructors
	public InstanceEntity() {
	}

	

	public InstanceEntity(InstanceId instanceId, String type, String name, Boolean active, Date createdTimestamp,
			String createdByDomain, String createdByEmail, Double locationLat, Double locationLng,
			String instanceAttributes) {
		super();

		setInstanceId(instanceId);
		setType(type);
		setName(name);
		setActive(active);
		setCreatedTimestamp(createdTimestamp);
		setCreatedByDomain(createdByDomain);
		setCreatedByEmail(createdByEmail);
		setInstanceAttributes(instanceAttributes);
		setLocation(locationLng,locationLat);
	}

	// Getters & Setters
	public InstanceId getInstanceId() {
		return _id;
	}

	public void setInstanceId(InstanceId instanceId) {
		this._id = instanceId;
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

	public String getCreatedByDomain() {
		return createdByDomain;
	}

	public void setCreatedByDomain(String createdByDomain) {
		this.createdByDomain = createdByDomain;
	}

	public String getCreatedByEmail() {
		return createdByEmail;
	}

	public void setCreatedByEmail(String createdByEmail) {
		this.createdByEmail = createdByEmail;
	}

	public Double getLocationLat() {
		return location.getY();
	}


	public Double getLocationLng() {
		return location.getX();
	}

	public String getInstanceAttributes() {
		return instanceAttributes;
	}

	public void setInstanceAttributes(String instanceAttributes) {
		this.instanceAttributes = instanceAttributes;
	}
	
	public Point getLocation() {
		return location;
	}

	public void setLocation(Double lng, Double lat) {
		location = new Point(lng, lat);
	}

	// Methods
	@Override
	public String toString() {
		return "InstanceEntity [instanceId=" + _id + ", type=" + type + ", name=" + name + ", active=" + active
				+ ", createdTimestamp=" + createdTimestamp + ", createdByDomain=" + createdByDomain
				+ ", createdByEmail=" + createdByEmail + ", locationLat=" + location.getY() + ", locationLng=" + location.getX()
				+ ", instanceAttributes=" + instanceAttributes + "]";
	}
}