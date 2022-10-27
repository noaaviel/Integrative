package iob.basics;

import java.io.Serializable;

public class Location implements Serializable{
	// Fields
	private static final long serialVersionUID = -4904804501234577863L;
	
	private Double lat;
	private Double lng;

	// Constructors
	public Location() {
	}

	public Location(Double lat, Double lng) {
		super();
		
		setLat(lat);
		setLng(lng);
	}

	// Getters & Setters
	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	// Methods
	@Override
	public String toString() {
		return "Location [lat=" + lat + ", lng=" + lng + "]";
	}
}