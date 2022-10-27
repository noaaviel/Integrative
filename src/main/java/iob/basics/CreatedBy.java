package iob.basics;

import java.io.Serializable;

public class CreatedBy implements Serializable {
	// Fields
	private static final long serialVersionUID = -4139608977119781602L;

	private UserId userId;

	// Constructors
	public CreatedBy() {

	}

	public CreatedBy(UserId userID) {
		super();

		setUserId(userID);
	}

	// Getters & Setters
	public UserId getUserId() {
		return userId;
	}

	public void setUserId(UserId userID) {
		this.userId = userID;
	}

	// Methods
	@Override
	public String toString() {
		return "CreatedBy [userId=" + userId + "]";
	}
}