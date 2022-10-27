package iob.logic;

import java.util.List;

import iob.Boundaries.NewUserBoundary;
import iob.Boundaries.UserBoundary;

public interface UsersService {
	public UserBoundary createUser(NewUserBoundary user);

	public UserBoundary login(String userDomain, String userEmail);

	public void updateUser(String userDomain, String userEmail, UserBoundary update);

	public List<UserBoundary> getAllUsers();

	public void deleteAllUsers();
}