package iob.JPAServices;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import iob.Boundaries.ActivityBoundary;
import iob.Boundaries.InstanceBoundary;
import iob.Boundaries.UserBoundary;
import iob.DAOs.ActivitiesDAO;
import iob.Exceptions.BadRequestException;
import iob.Exceptions.ForbiddenRequestException;
import iob.Exceptions.NotFoundException;
import iob.basics.ActivityId;
import iob.basics.Instance;
import iob.basics.InstanceId;
import iob.basics.InvokedBy;
import iob.basics.UserId;
import iob.data.ActivityEntity;
import iob.data.UserRole;
import iob.logic.EnhancedActivitiesService;

@Service
public class JPAActivitiesService implements EnhancedActivitiesService {
	// Fields
	private String appName;

	private ActivitiesDAO activitiesDAO;
	private ObjectMapper jackson;
	@Autowired
	private JPAUsersService usersService;
	@Autowired
	private JPAInstancesService instancesService;

	// Constructors
	@Autowired
	public JPAActivitiesService(ActivitiesDAO activitiesDAO) {
		setActivitiesDAO(activitiesDAO);
	}

	// Getters & Setters
	@Value("${spring.application.name:defaultName}")
	public void setSpringApplicatioName(String appName) {
		this.appName = appName;
	}

	public ActivitiesDAO getActivitiesDAO() {
		return activitiesDAO;
	}

	public void setActivitiesDAO(ActivitiesDAO activitiesDAO) {
		this.activitiesDAO = activitiesDAO;
	}

	// Methods
	@PostConstruct
	public void init() {
		jackson = new ObjectMapper();
	}

	@Override
	public Object invokeActivity(ActivityBoundary activity, String email) {
		Instance instance;
		InvokedBy invokedBy;
		UserBoundary invoker;

		// Validation of email
		if ((email == null) || (email.isEmpty()))
			throw new BadRequestException("InvokeActivity: User's email cannot be null or empty");

		if (activity == null)
			throw new BadRequestException("InvokeActivity: No activity provided");
		activity.setActivityId(new ActivityId(appName, UUID.randomUUID().toString()));

		// Validation of type
		if ((activity.getType() == null) || (activity.getType().isEmpty()))
			throw new BadRequestException("InvokeActivity: Activity's type cannot be null or empty");

		// Validation of instance
		if (activity.getInstance() == null)
			throw new BadRequestException("InvokeActivity: Activity's instance cannot be null");
		instance = activity.getInstance();
		if (instance.getInstanceId() == null)
			throw new BadRequestException("InvokeActivity: Activity's instance cannot be null");
		if ((instance.getInstanceId().getDomain() == null) || (instance.getInstanceId().getDomain().isEmpty()))
			throw new BadRequestException("Activity's instance's domain cannot be null");
		if ((instance.getInstanceId().getId() == null) || (instance.getInstanceId().getId().isEmpty()))
			throw new BadRequestException("Activity's instance's Id cannot be null");
		if (instancesService.getInstancesDAO().findById(instance.getInstanceId()) != null)
			activity.setInstance(activity.getInstance());
		else
			activity.setInstance(new Instance(new InstanceId(appName, UUID.randomUUID().toString())));

		activity.setCreatedTimestamp(new Date());

		// Validation of invoker
		if (activity.getInvokedBy() == null)
			throw new BadRequestException("InvokeActivity: Activity's invoker cannot be null");
		invokedBy = activity.getInvokedBy();
		if (invokedBy.getUserId() == null)
			throw new BadRequestException("InvokeActivity: Activity's invoker's Id cannot be null");
		if ((invokedBy.getUserId().getDomain() == null) || (invokedBy.getUserId().getDomain().isEmpty()))
			throw new BadRequestException("InvokeActivity: Activity's invoker's domain cannot be null or empty");
		if ((invokedBy.getUserId().getEmail() == null) || (invokedBy.getUserId().getEmail().isEmpty()))
			throw new BadRequestException("InvokeActivity: Activity's invoker's email cannot be null or empty");
		if (usersService.getUsersDAO().findById(activity.getInvokedBy().getUserId()) != null) {
			activity.setInvokedBy(activity.getInvokedBy());
		} else
			activity.setInvokedBy(new InvokedBy(new UserId(appName, email)));

		// Role authentication
		invoker = usersService.login(activity.getInvokedBy().getUserId().getDomain(), email);
		if (invoker.getRole() != UserRole.PLAYER)
			throw new ForbiddenRequestException("InvokeActivity: Only users with role PLAYER can invoke activities");

		if (activity.getType().equalsIgnoreCase("Search")) {
			ActivityEntity entity = toEntity(activity);
			entity = activitiesDAO.save(entity);
			return parseSearchRequest(activity);
		}
		ActivityEntity entity = toEntity(activity);
		entity = activitiesDAO.save(entity);

		return toBoundary(entity);
	}

	private Object parseSearchRequest(ActivityBoundary activity) {
		Object result = null;
		Map<String, Object> attributes = activity.getActivityAttributes();
		try {
			switch (attributes.get("SearchType").toString()) {
			case "ManagaerGetAllBusiness":
				result = instancesService.getAllByCreatedByDomainAndCreatedByEmail(
						activity.getInvokedBy().getUserId().getDomain(), activity.getInvokedBy().getUserId().getEmail(),
						(int) attributes.get("PageSize"), (int) attributes.get("PageNumber"));
				break;

			case "SearchQuery":
				String str = (String) attributes.get("BusinessType");
				Double lng = (Double) attributes.get("Lng");
				Double lat = (Double) attributes.get("Lat");
				int radius = (int) attributes.get("Proximity");
				Distance distance = new Distance(radius, Metrics.KILOMETERS);
				if (attributes.get("BusinessName") == "") { // only search by type and location // type and location
					result = instancesService.getAllByInstanceAttributesContainingAndNear(str, new Point(lng, lat),
							distance);
				} else { // search also by name
					String name = (String) attributes.get("BusinessName");
					result = instancesService.getAllByInstanceAttributesContainingAndNearAndNameContaining(str,
							new Point(lng, lat), distance, name);
					break;
				}
			}
		} catch (Exception e) {
			throw new BadRequestException(e);
		}
		return result;

	}

	@Override
	public List<ActivityBoundary> getAllActivities() {
		return StreamSupport.stream(activitiesDAO.findAll().spliterator(), false).map(this::toBoundary)
				.collect(Collectors.toList());
	}

	@Override
	public void deleteAllActivities() {
		activitiesDAO.deleteAll();
	}

	// Pagination
	@Override
	public List<ActivityBoundary> getAllActivities(int size, int page) {
		return activitiesDAO.findAll(PageRequest.of(page, size, Direction.DESC, "activityId")).getContent().stream()
				.map(this::toBoundary).collect(Collectors.toList());
	}

	@Override
	public List<ActivityBoundary> getAllActivitiesByType(String type, int size, int page) {
		List<ActivityEntity> matchingActivities = activitiesDAO.getAllActivitiesByType(type,
				PageRequest.of(page, size, Direction.DESC, "type"));

		return StreamSupport.stream(matchingActivities.spliterator(), false).map(this::toBoundary)
				.collect(Collectors.toList());
	}

	@Override
	public List<ActivityBoundary> getAllActivitiesByCreatedTimestampBefore(Date date, int size, int page) {
		List<ActivityEntity> matchingActivities = activitiesDAO.getAllActivitiesByCreatedTimestampBefore(date,
				PageRequest.of(page, size, Direction.DESC, "createdTimestamp"));

		return StreamSupport.stream(matchingActivities.spliterator(), false).map(this::toBoundary)
				.collect(Collectors.toList());
	}

	@Override
	public List<ActivityBoundary> getAllIActivitiesByCreatedTimestampAfter(Date date, int size, int page) {
		List<ActivityEntity> matchingActivities = activitiesDAO.getAllIActivitiesByCreatedTimestampAfter(date,
				PageRequest.of(page, size, Direction.DESC, "createdTimestamp"));

		return StreamSupport.stream(matchingActivities.spliterator(), false).map(this::toBoundary)
				.collect(Collectors.toList());
	}

	// Conversions
	public ActivityEntity toEntity(ActivityBoundary boundary) {
		ActivityEntity entity = new ActivityEntity();
		ActivityId activityId;
		Instance instance;
		InvokedBy invokedBy;

		// Validation of activityId
		if (boundary.getActivityId() == null)
			throw new BadRequestException("Activities.toEntity: Activity's Id cannot be null");
		activityId = boundary.getActivityId();
		entity.setActivityId(activityId);

		// Validation of domain
		if ((activityId.getDomain() == null) || (activityId.getDomain().isEmpty()))
			throw new BadRequestException("Activities.toEntity: Activity's domain cannot be null or empty");
		entity.getActivityId().setDomain(activityId.getDomain());

		// Validation of Id
		if ((activityId.getId() == null) || (activityId.getId().isEmpty()))
			throw new BadRequestException("Activities.toEntity: Activity's Id cannot be null or empty");
		entity.getActivityId().setId(activityId.getId());

		// Validation of type
		if ((boundary.getType() == null) || (boundary.getType().isEmpty()))
			throw new BadRequestException("Activities.toEntity: Activity's type cannot be null or empty");
		entity.setType(boundary.getType());

		// Validation of instance
		if (boundary.getInstance() == null)
			throw new BadRequestException("Activities.toEntity: Activity's instance cannot be null");
		instance = boundary.getInstance();
		if (instance.getInstanceId() == null)
			throw new BadRequestException("Activities.toEntity: Activity's instance's Id cannot be null");
		if ((instance.getInstanceId().getDomain() == null) || (instance.getInstanceId().getDomain().isEmpty()))
			throw new BadRequestException("Activities.toEntity: Activity's instance's domain cannot be null");
		entity.setInstanceDomain(boundary.getInstance().getInstanceId().getDomain());
		if ((instance.getInstanceId().getId() == null) || (instance.getInstanceId().getId().isEmpty()))
			throw new BadRequestException("Activity's instance's Id cannot be null");
		entity.setInstanceId(boundary.getInstance().getInstanceId().getId());

		// Validation of createdTimestamp
		if (boundary.getCreatedTimestamp() == null)
			throw new BadRequestException("Activities.toEntity: Activity's creation timestamp cannot be null");
		entity.setCreatedTimestamp(boundary.getCreatedTimestamp());

		// validation of invoker
		if (boundary.getInvokedBy() == null)
			throw new BadRequestException("Activities.toEntity: Activity's invoker cannot be null");
		invokedBy = boundary.getInvokedBy();
		if (invokedBy.getUserId() == null)
			throw new BadRequestException("Activities.toEntity: Activity's invoker's Id cannot be null");
		if ((invokedBy.getUserId().getDomain() == null) || (invokedBy.getUserId().getDomain().isEmpty()))
			throw new BadRequestException("Activities.toEntity Activity's invoker's domain cannot be null or empty");
		entity.setInvokerDomain(invokedBy.getUserId().getDomain());
		if ((invokedBy.getUserId().getEmail() == null) || (invokedBy.getUserId().getEmail().isEmpty()))
			throw new BadRequestException("Activities.toEntity Activity's invoker's email cannot be null or empty");
		entity.setInvokerEmail(invokedBy.getUserId().getEmail());

		if (boundary.getActivityAttributes() != null) {
			Map<String, Object> map = boundary.getActivityAttributes();
			try {
				String json = jackson.writeValueAsString(map);
				entity.setActivityAttributes(json);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			entity.setActivityAttributes("");
		}

		return entity;
	}

	public ActivityBoundary toBoundary(ActivityEntity entity) {
		ActivityBoundary boundary = new ActivityBoundary();
		ActivityId activityId;
		Instance instance;
		InvokedBy invokedBy;

		// Validation of activityId
		if (entity.getActivityId() == null)
			throw new BadRequestException("Activities.toBoundary: Activity's Id 1 cannot be null");
		activityId = entity.getActivityId();
		boundary.setActivityId(activityId);

		// Validation of domain
		if ((activityId.getDomain() == null) || (activityId.getDomain().isEmpty()))
			throw new BadRequestException("Activities.toBoundary: Activity's domain cannot be null or empty");
		boundary.getActivityId().setDomain(activityId.getDomain());

		// Validation of Id
		if ((activityId.getId() == null) || (activityId.getId().isEmpty()))
			throw new BadRequestException("Activities.toBoundary: Activity's Id 2 be null or empty");
		boundary.getActivityId().setId(activityId.getId());

		// Validation of type
		if ((entity.getType() == null) || (entity.getType().isEmpty()))
			throw new BadRequestException("Activities.toBoundary: Activity's type cannot be null or empty");
		boundary.setType(entity.getType());

		// Validation of instance
		if ((entity.getInstanceDomain() == null) || (entity.getInstanceDomain().isEmpty()))
			throw new BadRequestException(
					"Activities.toBoundary: Activity's instance's domain cannot be null or empty");
		if ((entity.getInstanceId() == null) || (entity.getInstanceId().isEmpty()))
			throw new BadRequestException("Activities.toBoundary: Activity's instance's Id cannot be null or empty");
		instance = new Instance(new InstanceId(entity.getInstanceDomain(), entity.getInstanceId()));
		boundary.setInstance(instance);

		// Validation of createdTimestamp
		if (entity.getCreatedTimestamp() == null)
			throw new BadRequestException("Activities.toBoundary: Activity's creation timestamp cannot be null");
		boundary.setCreatedTimestamp(entity.getCreatedTimestamp());

		// Validation of invoker
		if ((entity.getInvokerDomain() == null) || (entity.getInvokerDomain().isEmpty()))
			throw new BadRequestException("Activities.toBoundary: Activity's invoker's domain cannot be null or empty");
		if ((entity.getInvokerEmail() == null) || (entity.getInvokerEmail().isEmpty()))
			throw new BadRequestException("Activities.toBoundary: Activity's invoker's email cannot be null or empty");
		invokedBy = new InvokedBy(new UserId(entity.getInvokerDomain(), entity.getInvokerEmail()));
		boundary.setInvokedBy(invokedBy);

		if (entity.getActivityAttributes() != null) {
			try {
				@SuppressWarnings("unchecked")
				Map<String, Object> boundaryDetails = jackson.readValue(entity.getActivityAttributes(), Map.class);
				boundary.setActivityAttributes(boundaryDetails);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			boundary.setActivityAttributes(new HashMap<String, Object>());
		}

		return boundary;
	}
}