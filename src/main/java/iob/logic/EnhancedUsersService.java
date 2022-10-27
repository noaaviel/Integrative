package iob.logic;

import java.util.List;

import iob.Boundaries.UserBoundary;
import iob.data.UserRole;

public interface EnhancedUsersService extends UsersService {
	public List<UserBoundary> getAllUsers(int size, int page);

	public List<UserBoundary> getAllUsersByRole(UserRole role, int size, int page);

	public List<UserBoundary> getAllUsersByUsername(String username, int size, int page);

	public List<UserBoundary> getAllUsersByAvatar(String avatar, int size, int page);

	//public List<UserBoundary> getAllUsersByEmail(String email, int size, int page);
}