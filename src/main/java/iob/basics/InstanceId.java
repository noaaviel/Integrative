package iob.basics;

import java.io.Serializable;

public class InstanceId implements Serializable {
	// Fields
	private static final long serialVersionUID = 4193430258869459458L;

	private String domain;
	private String id;

	// Constructors
	public InstanceId() {
	}

	public InstanceId(String domain, String id) {
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
		return "InstanceId [domain=" + domain + ", id=" + id + "]";
	}
}