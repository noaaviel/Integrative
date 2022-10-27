package iob.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import iob.Boundaries.InstanceBoundary;
import iob.Boundaries.UserBoundary;
import iob.Exceptions.ForbiddenRequestException;
import iob.basics.UserId;
import iob.data.UserRole;
import iob.logic.EnhancedInstancesService;
import iob.logic.EnhancedUsersService;
import iob.logic.InstancesService;

@RestController
public class InstanceController {
	// Fields
	protected EnhancedInstancesService instancesService;
	protected EnhancedUsersService usersService;

	// Constructors
	@Autowired
	public InstanceController(EnhancedInstancesService instancesService, EnhancedUsersService usersService) {
		this.instancesService = instancesService;
		this.usersService = usersService;
	}

	// Methods
	// createInstance
	@RequestMapping(path = "/iob/instances", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public InstanceBoundary createInstance(@RequestBody InstanceBoundary instance) {
		UserId user = instance.getCreatedBy().getUserId();

		// Role authentication
		if (!usersService.login(user.getDomain(), user.getEmail()).getRole().equals(UserRole.MANAGER))
			throw new ForbiddenRequestException(
					"InstanceController: Only users with role MANAGER can create instances");

		return instancesService.createInstance(instance, user.getEmail());
	}

	@Deprecated
	@RequestMapping(path = "iob/instances/{instanceDomain}/{instanceID}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateInstance(@RequestBody InstanceBoundary instance,
			@PathVariable("instanceDomain") String instanceDomain, @PathVariable("instanceID") String instanceId,
			@RequestParam(name = "userDomain", required = false, defaultValue = "") String userDomain,
			@RequestParam(name = "userEmail", required = false, defaultValue = "") String userEmail)
			throws JsonMappingException, JsonProcessingException {

		// Role authentication
		if (!userDomain.equals("") && !userEmail.equals("")) {
			if (!usersService.login(userDomain, userEmail).getRole().equals(UserRole.MANAGER))
				throw new ForbiddenRequestException(
						"InstanceController: Only users with role MANAGER can update instances");
		}
		instancesService.updateInstance(instanceDomain, instanceId, instance);
	}

	@Deprecated
	// getSpecificInstance
	@RequestMapping(path = "/iob/instances/{instanceDomain}/{instanceID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public InstanceBoundary getSpecificInstance(@PathVariable("instanceDomain") String instanceDomain,
			@PathVariable("instanceID") String instanceID,
			@RequestParam(name = "userDomain", required = false, defaultValue = "") String userDomain,
			@RequestParam(name = "userEmail", required = false, defaultValue = "") String userEmail) {
		UserBoundary user = usersService.login(userDomain, userEmail);
		InstanceBoundary instance = instancesService.getSpecificInstance(instanceDomain, instanceID);

		// Role authentication
		if (user.getRole().equals(UserRole.PLAYER)) {
			if (!instance.getActive())
				throw new ForbiddenRequestException(
						"InstanceController: Users with role PLAYER can only get active instances");
		} else if (!user.getRole().equals(UserRole.MANAGER))
			throw new ForbiddenRequestException(
					"InstanceController: Only users with role PLAYER/MANAGER can get instances");

		return instance;
	}

	@Deprecated
	// getAllInstances
	@RequestMapping(path = "/iob/instances", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public InstanceBoundary[] getAllInstances(
			@RequestParam(name = "userDomain", required = false, defaultValue = "") String userDomain,
			@RequestParam(name = "userEmail", required = false, defaultValue = "") String userEmail) {
		UserBoundary user = usersService.login(userDomain, userEmail);
		List<InstanceBoundary> allInstances = instancesService.getAllInstances();

		// Role authentication
		if (user.getRole().equals(UserRole.PLAYER))
			allInstances.removeIf(instance -> !instance.getActive());
		else if (!user.getRole().equals(UserRole.MANAGER))
			throw new ForbiddenRequestException(
					"InstanceController: Only users with role PLAYER/MANAGER can get all instances");

		return allInstances.toArray(new InstanceBoundary[0]);
	}

//	InstanceBoundary JSON:
//	{
//		"instanceId":
//		{
//			"domain":"2022b.noa.aviel",
//			"id":"950"
//		},
//		"type":"demo",
//		"name":"demo instance",
//		"active":true,
//		"createdTimeStamp":"2022",
//		"createdBy":
//		{
//			"userId":
//			{
//				"domain":"2022b.noa.aviel",
//				"email":"user@demo.com"
//			}
//		},
//		"location":
//		{
//        	"lat":"32.115",
//        	"lng":"34.817"
//		},
//		"instanceAttributes":
//		{
//			"key1":"value1"
//		}
//	}
}