package iob.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import iob.Boundaries.ActivityBoundary;
import iob.Boundaries.InstanceBoundary;
import iob.Exceptions.ForbiddenRequestException;
import iob.Exceptions.NotFoundException;
import iob.basics.InstanceId;
import iob.basics.UserId;
import iob.data.UserRole;
import iob.logic.EnhancedActivitiesService;
import iob.logic.EnhancedInstancesService;
import iob.logic.EnhancedUsersService;

@RestController
public class ActivityController {
	// Fields
	private EnhancedActivitiesService activitiesService;
	private EnhancedUsersService usersService;
	private EnhancedInstancesService instancesService;

	// Constructors
	public ActivityController() {
	}

	@Autowired
	public ActivityController(EnhancedActivitiesService activitiesService, EnhancedUsersService usersService, EnhancedInstancesService instancesService) {
		this.activitiesService = activitiesService;
		this.usersService = usersService;
		this.instancesService = instancesService;
	}

	// Methods
	// invokeActivity
	@RequestMapping(path = "/iob/activities",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Object invokeActivity(@RequestBody ActivityBoundary activity) {
		//InstanceId instanceId = activity.getInstance().getInstanceId();
		//InstanceBoundary instance = instancesService.getSpecificInstance(instanceId.getDomain(), instanceId.getId());
		UserId user = activity.getInvokedBy().getUserId();

		
		// Role authentication
//		if (!usersService.login(user.getDomain(), user.getEmail()).getRole().equals(UserRole.PLAYER))
//			throw new ForbiddenRequestException("ActivityController: Only users with role PLAYER can invoke activities");

		return activitiesService.invokeActivity(activity, user.getEmail());
	}

//	ActivityBoundary JSON:
//	{
//		"activityId":
//		{
//			"domain":"2022b.noa.aviel",
//			"id":"950"
//		},
//		"type":"demo",
//		"instance":
//		{
//			"instanceId":
//			{
//				"domain":"2022b.noa.aviel",
//				"id":"42"
//			}
//		},
//		"createdTimeStamp":"2022",
//		"invokedBy":
//		{
//    		"userId":
//			{
//        		"domain":"2022b.noa.aviel",
//        		"email":"user@demo.com"
//    		}
//		},
//		"activityAttributes":
//		{
//			"key1":"value1"
//		}
//	}
}