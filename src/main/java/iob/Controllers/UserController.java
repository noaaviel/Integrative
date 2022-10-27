package iob.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import iob.Boundaries.NewUserBoundary;
import iob.Boundaries.UserBoundary;
import iob.logic.UsersService;

@RestController
public class UserController {
	// Fields
	private UsersService userService;

	// Constructors
	@Autowired
	public UserController(UsersService userService) {
		this.userService = userService;
	}

	// Methods
	// createUser
	@RequestMapping(path = "/iob/users",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary createUser(@RequestBody NewUserBoundary user) {
		return userService.createUser(user);
	}

	// login
	@RequestMapping(path = "/iob/users/login/{userDomain}/{userEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary login(@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail) {
		return userService.login(userDomain, userEmail);
	}

	// updateUser
	@RequestMapping(path = "/iob/users/{userDomain}/{userEmail}",
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateUser(@RequestBody UserBoundary user, @PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail) {
		userService.updateUser(userDomain, userEmail, user);
	}

//	UserBoundary JSON:
//	{
//		"userId":
//		{
//			"domain":"2022b.noa.aviel",
//			"email":"user@demo.com"
//		},
//		"role":"MANAGER",
//		"username":"Demo User",
//		"avatar":"J"
//	}
}