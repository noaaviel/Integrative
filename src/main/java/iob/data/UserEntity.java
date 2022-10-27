package iob.data;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import iob.basics.UserId;

@Document(collection = "Users")
public class UserEntity {
	// Fields
	@Id
	private UserId _id;
	private UserRole role;
	private String username;
	private String avatar;

	// Constructors
	public UserEntity() {
	}

	public UserEntity(UserId userId, UserRole role, String username, String avatar) {
		super();

		setUserId(userId);
		setRole(role);
		setUsername(username);
		setAvatar(avatar);
	}

	// Getters & Setters
	public UserId getUserId() {
		return _id;
	}

	public void setUserId(UserId userId) {
		this._id = userId;
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
		return "UserBoundary [Id=" + _id + ", role=" + role + ", username=" + username + ", avatar=" + avatar + "]";
	}
}