package iob.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import iob.Boundaries.ActivityBoundary;
import iob.Boundaries.UserBoundary;
import iob.Exceptions.ForbiddenRequestException;
import iob.data.UserRole;
import iob.logic.EnhancedActivitiesService;
import iob.logic.EnhancedInstancesService;
import iob.logic.EnhancedUsersService;

@RestController
public class AdminController {
	// Fields
	protected EnhancedActivitiesService activitiesService;
	protected EnhancedUsersService usersService;
	protected EnhancedInstancesService instancesService;

	// Constructors
	@Autowired
	public AdminController(EnhancedActivitiesService activitiesService, EnhancedUsersService usersService,
			EnhancedInstancesService instancesService) {
		this.activitiesService = activitiesService;
		this.usersService = usersService;
		this.instancesService = instancesService;
	}

	// Methods
	// deleteAllUsers
	@RequestMapping(path = "/iob/admin/users",
			method = RequestMethod.DELETE)
	public void deleteAllUsers(
			@RequestParam(name = "userDomain", required = false, defaultValue = "") String userDomain,
			@RequestParam(name = "userEmail", required = false, defaultValue = "") String userEmail) {
		// Role authentication
		if (!usersService.login(userDomain, userEmail).getRole().equals(UserRole.ADMIN))
			throw new ForbiddenRequestException("AdminController: Only users with role ADMIN can delete all users");
		
		usersService.deleteAllUsers();
	}

	// deleteAllInstances
	@RequestMapping(path = "/iob/admin/instances",
			method = RequestMethod.DELETE)
	public void deleteAllInstances(
			@RequestParam(name = "userDomain", required = false, defaultValue = "") String userDomain,
			@RequestParam(name = "userEmail", required = false, defaultValue = "") String userEmail) {
		// Role authentication
		if (!usersService.login(userDomain, userEmail).getRole().equals(UserRole.ADMIN))
			throw new ForbiddenRequestException("AdminController: Only users with role ADMIN can delete all instances");
//		
		instancesService.deleteAllInstances();
	}

	// deleteAllActivities
	@RequestMapping(path = "/iob/admin/activities",
			method = RequestMethod.DELETE)
	public void deleteAllActivities(
			@RequestParam(name = "userDomain", required = false, defaultValue = "") String userDomain,
			@RequestParam(name = "userEmail", required = false, defaultValue = "") String userEmail) {
		// Role authentication
		if (!usersService.login(userDomain, userEmail).getRole().equals(UserRole.ADMIN))
			throw new ForbiddenRequestException("AdminController: Only users with role ADMIN can delete all activites");
		
		activitiesService.deleteAllActivities();
	}

	// getAllUsers
	@RequestMapping(path = "/iob/admin/users",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary[] getAllUsers(
			@RequestParam(name = "userDomain", required = false, defaultValue = "") String userDomain,
			@RequestParam(name = "userEmail", required = false, defaultValue = "") String userEmail) {
		// Role authentication
		if (!usersService.login(userDomain, userEmail).getRole().equals(UserRole.ADMIN))
			throw new ForbiddenRequestException("AdminController: Only users with role ADMIN can get all users");
		
		return usersService.getAllUsers().toArray(new UserBoundary[0]);
	}

	// getAllActivities
	@RequestMapping(path = "/iob/admin/activities",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ActivityBoundary[] getAllActivities(
			@RequestParam(name = "userDomain", required = false, defaultValue = "") String userDomain,
			@RequestParam(name = "userEmail", required = false, defaultValue = "") String userEmail) {
		// Role authentication
		if (!usersService.login(userDomain, userEmail).getRole().equals(UserRole.ADMIN))
			throw new ForbiddenRequestException("AdminController: Only users with role ADMIN can get all activities");

		return activitiesService.getAllActivities().toArray(new ActivityBoundary[0]);
	}
}