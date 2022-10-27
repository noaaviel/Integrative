package iob.basics;

import java.io.Serializable;

public class InvokedBy implements Serializable {
	// Fields
	private static final long serialVersionUID = 5539736520054428143L;

	private UserId userId;

	// Constructors
	public InvokedBy() {
	}

	public InvokedBy(UserId userId) {
		super();

		setUserId(userId);
	}

	// Getters & Setters
	public UserId getUserId() {
		return userId;
	}

	public void setUserId(UserId userId) {
		this.userId = userId;
	}

	// Methods
	@Override
	public String toString() {
		return "InvokedBy [userID=" + userId + "]";
	}
}