package iob.Controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import iob.Boundaries.ActivityBoundary;
import iob.Boundaries.UserBoundary;
import iob.Exceptions.ForbiddenRequestException;
import iob.data.UserRole;
import iob.logic.EnhancedActivitiesService;
import iob.logic.EnhancedInstancesService;
import iob.logic.EnhancedUsersService;

public class EnhancedAdminController extends AdminController {
	// Fields

	// Constructors
	public EnhancedAdminController(EnhancedActivitiesService activitiesService, EnhancedUsersService userService,
			EnhancedInstancesService instanceService) {
		super(activitiesService, userService, instanceService);
	}

	// Methods
	// deleteAllUsers
	@RequestMapping(path = "/iob/admin/users?userDomain={domain}&userEmail={email}",
			method = RequestMethod.DELETE)
	public void deleteAllUsers(
			@RequestParam(name = "userDomain", required = false, defaultValue = "") String userDomain,
			@RequestParam(name = "userEmail", required = false, defaultValue = "") String userEmail) {
		// Role authentication
		if (!usersService.login(userDomain, userEmail).getRole().equals(UserRole.ADMIN))
			throw new ForbiddenRequestException("EnhancedAdminController: Only users with role ADMIN can delete all users");

		super.deleteAllUsers(userDomain, userEmail);
	}

	// deleteAllInstances
	@RequestMapping(path = "/iob/admin/instances?userDomain={domain}&userEmail={email}",
			method = RequestMethod.DELETE)
	public void deleteAllInstances(
			@RequestParam(name = "userDomain", required = false, defaultValue = "") String userDomain,
			@RequestParam(name = "userEmail", required = false, defaultValue = "") String userEmail) {
		// Role authentication
		if (!usersService.login(userDomain, userEmail).getRole().equals(UserRole.ADMIN))
			throw new ForbiddenRequestException("EnhancedAdminController: Only users with role ADMIN can delete all instances");

		super.deleteAllInstances(userDomain, userEmail);
	}

	// deleteAllActivities
	@RequestMapping(path = "/iob/admin/activities?userDomain={domain}&userEmail={email}",
			method = RequestMethod.DELETE)
	public void deleteAllActivities(
			@RequestParam(name = "userDomain", required = false, defaultValue = "") String userDomain,
			@RequestParam(name = "userEmail", required = false, defaultValue = "") String userEmail) {
		// Role authentication
		if (!usersService.login(userDomain, userEmail).getRole().equals(UserRole.ADMIN))
			throw new ForbiddenRequestException("EnhancedAdminController: Only users with role ADMIN can delete all activites");

		super.deleteAllActivities(userDomain, userEmail);
	}

	// getAllUsers
	@RequestMapping(path = "/iob/admin/users?userDomain={domain}&userEmail={email}&size={size}&page={page}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary[] getAllUsers(
			@RequestParam(name = "userDomain", required = false, defaultValue = "") String userDomain,
			@RequestParam(name = "userEmail", required = false, defaultValue = "") String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		// Role authentication
		if (!usersService.login(userDomain, userEmail).getRole().equals(UserRole.ADMIN))
			throw new ForbiddenRequestException("EnhancedAdminController: Only users with role ADMIN can get all users");

		return usersService.getAllUsers(size, page).toArray(new UserBoundary[0]);
	}

	// getAllActivities
	@RequestMapping(path = "/iob/admin/activities?userDomain={domain}&userEmail={email}&size={size}&page={page}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ActivityBoundary[] getAllActivities(
			@RequestParam(name = "userDomain", required = false, defaultValue = "") String userDomain,
			@RequestParam(name = "userEmail", required = false, defaultValue = "") String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		// Role authentication
		if (!usersService.login(userDomain, userEmail).getRole().equals(UserRole.ADMIN))
			throw new ForbiddenRequestException("EnhancedAdminController: Only users with role ADMIN can get all activities");

		return activitiesService.getAllActivities(size, page).toArray(new ActivityBoundary[0]);
	}
}