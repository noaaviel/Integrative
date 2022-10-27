package iob.basics;

import java.io.Serializable;

public class UserId implements Serializable{
	// Fields
	private static final long serialVersionUID = 1273823492910819694L;
	
	private String domain;
	private String email;

	// Constructors
	public UserId() {
	}

	public UserId(String domain, String email) {
		super();
		
		setDomain(domain);
		setEmail(email);
	}

	// Getters & Setters
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	// Methods
	@Override
	public String toString() {
		return "UserId [domain=" + domain + ", email=" + email + "]";
	}
}