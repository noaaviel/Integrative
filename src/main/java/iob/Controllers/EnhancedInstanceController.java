package iob.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
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
import iob.data.UserRole;
import iob.logic.EnhancedInstancesService;
import iob.logic.EnhancedUsersService;

@RestController
public class EnhancedInstanceController {
	// Fields
	private EnhancedInstancesService instancesService;
	private EnhancedUsersService usersService;

	// Constructors
	@Autowired
	public EnhancedInstanceController(EnhancedInstancesService instancesService, EnhancedUsersService usersService) {
		super();
		this.instancesService = instancesService;
		this.usersService = usersService;
	}

	// Methods
	// searchInstancesByName
	@RequestMapping(path = "/iob/instances/search/byName/{name}?userDomain={domain}&userEmail={email}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public InstanceBoundary[] searchInstancesByName(@PathVariable(name = "name") String name,
			@RequestParam(name = "userDomain", required = true, defaultValue = "") String userDomain,
			@RequestParam(name = "userEmail", required = true, defaultValue = "") String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		UserBoundary user = usersService.login(userDomain, userEmail);
		List<InstanceBoundary> matchingInstances = instancesService.getAllInstancesByName(name, size, page);

		// Role authentication
		if (user.getRole().equals(UserRole.PLAYER))
			matchingInstances.removeIf(instance -> !instance.getActive());
		else if (!user.getRole().equals(UserRole.MANAGER))
			throw new ForbiddenRequestException(
					"EnhancedInstanceController: Only users with role MANAGER/PLAYER can search instances by name");

		return matchingInstances.toArray(new InstanceBoundary[0]);
	}

	// searchInstancesByType
	@RequestMapping(path = "/iob/instances/search/byType/{type}?userDomain={domain}&userEmail={email}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public InstanceBoundary[] searchInstancesByType(@PathVariable(name = "type") String type,
			@RequestParam(name = "userDomain", required = true, defaultValue = "") String userDomain,
			@RequestParam(name = "userEmail", required = true, defaultValue = "") String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		UserBoundary user = usersService.login(userDomain, userEmail);
		List<InstanceBoundary> matchingInstances = instancesService.getAllInstancesByType(type, size, page);

		// Role authentication
		if (user.getRole().equals(UserRole.PLAYER))
			matchingInstances.removeIf(instance -> !instance.getActive());
		else if (!user.getRole().equals(UserRole.MANAGER))
			throw new ForbiddenRequestException(
					"EnhancedInstanceController: Only users with role MANAGER/PLAYER can search instances by type");

		return matchingInstances.toArray(new InstanceBoundary[0]);
	}

	// searchInstancesByLocation
	@RequestMapping(path = "/iob/instances/search/near/{lat}/{lng}/{distance}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public InstanceBoundary[] searchInstancesByLocation(@PathVariable(name = "lat") Double lat,
			// @PathVariable(name = "lng") Double lng, @PathVariable(name = "distance")
			// Double distance,
			@PathVariable(name = "lng") Double lng, @PathVariable(name = "distance") int distance,
			@RequestParam(name = "userDomain", required = false, defaultValue = "") String userDomain,
			@RequestParam(name = "userEmail", required = false, defaultValue = "") String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {

		List<InstanceBoundary> matchingInstances = this.instancesService.getAllInstancesNear(new Point(lng, lat),
				new Distance(distance, Metrics.KILOMETERS));

//		Role authentication
//		if (user.getRole().equals(UserRole.PLAYER))
//			matchingInstances.removeIf(instance -> !instance.getActive());
//		else if (!user.getRole().equals(UserRole.MANAGER))
//			throw new ForbiddenRequestException(
//					"EnhancedInstanceController: Only users with role MANAGER/PLAYER can search instances by location");

		return matchingInstances.toArray(new InstanceBoundary[0]);
	}

	// updateInstance
	@RequestMapping(path = "/iob/instances/{instanceDomain}/{instanceID}?userDomain={domain}&userEmail={email}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateInstance(@RequestBody InstanceBoundary instance,
			@PathVariable("instanceDomain") String instanceDomain, @PathVariable("instanceID") String instanceId,
			@RequestParam(name = "userDomain", required = false, defaultValue = "") String userDomain,
			@RequestParam(name = "userEmail", required = false, defaultValue = "") String userEmail)
			throws JsonMappingException, JsonProcessingException {
		// Role authentication
		if (!userDomain.equals("") && !userEmail.equals("")) {
			if (!usersService.login(userDomain, userEmail).getRole().equals(UserRole.MANAGER))
				throw new ForbiddenRequestException(
						"EnhancedInstanceController: Only users with role MANAGER can update instances");
		}

		this.instancesService.updateInstance(instanceDomain, instanceId, instance);
	}

	// getSpecificInstance
	@RequestMapping(path = "/iob/instances/{instanceDomain}/{instanceID}?userDomain={domain}&userEmail={email}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public InstanceBoundary getSpecificInstance(@PathVariable("instanceDomain") String instanceDomain,
			@PathVariable("instanceID") String instanceID,
			@RequestParam(name = "userDomain", required = true, defaultValue = "") String userDomain,
			@RequestParam(name = "userEmail", required = true, defaultValue = "") String userEmail) {
		UserBoundary user = usersService.login(userDomain, userEmail);
		InstanceBoundary instance = instancesService.getSpecificInstance(instanceDomain, instanceID);

		// Role authentication
		if ((user.getRole().equals(UserRole.PLAYER)) && (!instance.getActive()))
			throw new ForbiddenRequestException(
					"EnhancedInstanceController: Users with role PLAYER can only get active instances");
		else if (!user.getRole().equals(UserRole.MANAGER))
			throw new ForbiddenRequestException(
					"EnhancedInstanceController: Only users with role PLAYER/MANAGER can get instances");

		return this.instancesService.getSpecificInstance(instanceDomain, instanceID);
	}

	// getAllInstances
	@RequestMapping(path = "/iob/instances?userDomain={domain}&userEmail={email}&size={size}&page={page}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public InstanceBoundary[] getAllInstances(
			@RequestParam(name = "userDomain", required = true, defaultValue = "") String userDomain,
			@RequestParam(name = "userEmail", required = true, defaultValue = "") String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		UserBoundary user = usersService.login(userDomain, userEmail);
		List<InstanceBoundary> allInstances = instancesService.getAllInstances(size, page);
		// Role authentication
		if (user.getRole().equals(UserRole.PLAYER))
			allInstances.removeIf(instance -> !instance.getActive());
		else if (!user.getRole().equals(UserRole.MANAGER))
			throw new ForbiddenRequestException(
					"EnhancedInstanceController: Only users with role PLAYER/MANAGER can get all instances");

		return allInstances.toArray(new InstanceBoundary[0]);
	}

}