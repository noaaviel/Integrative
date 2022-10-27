package iob.Boundaries;

import iob.basics.UserId;
import iob.data.UserRole;

public class UserBoundary {
	// Fields
	private UserId userId;
	private UserRole role;
	private String username;
	private String avatar;

	// Constructors
	public UserBoundary() {
	}
	
	public UserBoundary(NewUserBoundary newUser) {
		this(new UserId("demo", newUser.getEmail()), newUser.getRole(), newUser.getUsername(), newUser.getAvatar());
	}

	public UserBoundary(UserId userId, UserRole role, String username, String avatar) {
		super();

		setUserId(userId);
		setRole(role);
		setUsername(username);
		setAvatar(avatar);
	}

	// Getters & Setters
	public UserId getUserId() {
		return userId;
	}

	public void setUserId(UserId userId) {
		this.userId = userId;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	// Methods
	@Override
	public String toString() {
		return "UserBoundary [Id=" + userId + ", role=" + role + ", username=" + username + ", avatar=" + avatar + "]";
	}
}