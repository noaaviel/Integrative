package iob.Boundaries;

import iob.data.UserRole;

public class NewUserBoundary {
	// Fields
	private String email;
	private UserRole role;
	private String username;
	private String avatar;

	// Constructors
	public NewUserBoundary() {
	}

	public NewUserBoundary(String email, UserRole role, String username, String avatar) {
		super();

		setEmail(email);
		setRole(role);
		setUsername(username);
		setAvatar(avatar);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	@Override
	public String toString() {
		return "NewUserBoundary [email=" + email + ", role=" + role + ", username=" + username + ", avatar=" + avatar
				+ "]";
	}
}