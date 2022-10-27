package iob.basics;

import java.io.Serializable;

public class ActivityId implements Serializable {
	// Fields
	private static final long serialVersionUID = -1970419593440385011L;

	private String domain;
	private String id;

	// Constructors
	public ActivityId() {

	}

	public ActivityId(String domain, String id) {
		super();

		setDomain(domain);
		setId(id);
	}

	// Getters & Setters
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	// Methods
	@Override
	public String toString() {
		return "ActivityId [domain=" + domain + ", id=" + id + "]";
	}
}