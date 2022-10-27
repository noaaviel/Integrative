package iob.JPAServices;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import iob.Boundaries.InstanceBoundary;
import iob.Boundaries.UserBoundary;
import iob.DAOs.InstancesDAO;
import iob.Exceptions.BadRequestException;
import iob.Exceptions.ForbiddenRequestException;
import iob.Exceptions.NotFoundException;
import iob.basics.CreatedBy;
import iob.basics.InstanceId;
import iob.basics.Location;
import iob.basics.UserId;
import iob.data.InstanceEntity;
import iob.data.UserRole;
import iob.logic.EnhancedInstancesService;

@Service
public class JPAInstancesService implements EnhancedInstancesService {
	// Fields
	private String appName;

	private InstancesDAO instancesDAO;
	private ObjectMapper jackson;
	@Autowired
	private JPAUsersService usersService;

	// Constructors
	@Autowired
	public JPAInstancesService(InstancesDAO instancesDAO) {
		setInstancesDAO(instancesDAO);
	}

	// Getters & Setters
	@Value("${spring.application.name:defaultName}")
	public void setSpringApplicatioName(String appName) {
		this.appName = appName;
	}

	public InstancesDAO getInstancesDAO() {
		return instancesDAO;
	}

	public void setInstancesDAO(InstancesDAO instancesDAO) {
		this.instancesDAO = instancesDAO;
	}

	// Methods
	@PostConstruct
	public void init() {
		jackson = new ObjectMapper();
	}

	@Override
	public InstanceBoundary createInstance(InstanceBoundary instance, String email) {
		UserId userId;

		if (instance == null)
			throw new BadRequestException("createInstance: No instance data provided");
		instance.setInstanceId(new InstanceId(appName, UUID.randomUUID().toString()));

		// Validation of type
		if ((instance.getType() == null) || (instance.getType().isEmpty()))
			throw new BadRequestException("createInstance: Instance's type cannot be null or empty");
		email = instance.getCreatedBy().getUserId().getEmail();

		// Validation of name
		if ((instance.getName() == null) || (instance.getName().isEmpty()))
			throw new BadRequestException("createInstance: Instance's name cannot be null or empty");

		// Validation of active
		if (instance.getActive() == null)
			throw new BadRequestException("createInstance: Instance's active status cannot be null");

		instance.setCreatedTimestamp(new Date());
		userId = new UserId(appName, email);
		instance.setCreatedBy(new CreatedBy(userId));

		// Validation of location
		if ((instance.getLocation().getLat() == null) || (instance.getLocation().getLng() == null))
			throw new BadRequestException("createInstance: Instance's location cannot be null or empty");

		// Role authentication
		if (usersService.login(userId.getDomain(), userId.getEmail()).getRole() != UserRole.MANAGER)
			throw new ForbiddenRequestException("createInstance: Only users with role MANAGER can create instances");

		InstanceEntity entity = toEntity(instance);
		entity = instancesDAO.save(entity);

		return toBoundary(entity);
	}

	@Override
	public InstanceBoundary updateInstance(String instanceDomain, String instanceID, InstanceBoundary update) {
		InstanceId instanceId = new InstanceId(instanceDomain, instanceID);
		Optional<InstanceEntity> optionalInstance = instancesDAO.findById(instanceId);
		InstanceEntity entity;
		if (!optionalInstance.isPresent())
			throw new NotFoundException("updateInstance: There's no instance with Id:" + instanceId);

		entity = optionalInstance.get();

		// Validation of type
		if ((update.getType() == null) || (update.getType().isEmpty()))
			throw new BadRequestException("updateInstance: Instance's type cannot be null or empty");
		entity.setType(update.getType());

		// Validation of name
		if ((update.getName() == null) || (update.getName().isEmpty()))
			throw new BadRequestException("updateInstance: Instance's name cannot be null or empty");
		entity.setName(update.getName());

		// Validation of active
		if (update.getActive() == null)
			throw new BadRequestException("updateInstance: Instance's active status cannot be null");
		entity.setActive(update.getActive());

		// Validation of location
		if (update.getLocation() == null)
			throw new BadRequestException("updateInstance: Instance's location cannot be null");
		if ((update.getLocation().getLat() == null) || (update.getLocation().getLng() == null))
			throw new BadRequestException("updateInstance: Instance's location cannot be null");
		entity.setLocation(update.getLocation().getLng(), update.getLocation().getLat());
		// entity=string
		// update=map
		if (update.getInstanceAttributes() != null) {
			Map<String, Object> map = update.getInstanceAttributes();
			try {
				entity.setInstanceAttributes(jackson.writeValueAsString(map));
			} catch (Exception e) {
				entity.setInstanceAttributes("");
			}
		}

		entity = instancesDAO.save(entity);
		return toBoundary(entity);
	}

	@Override
	public InstanceBoundary getSpecificInstance(String instanceDomain, String instanceID) {
		Optional<InstanceEntity> entityOp = instancesDAO.findById(new InstanceId(instanceDomain, instanceID));
		InstanceEntity entity;
		UserBoundary user;

		if (!entityOp.isPresent())
			throw new NotFoundException("getSpecificInstance: No Such Instance With Id: " + instanceID);
		entity = entityOp.get();

		// Role authentication
		user = usersService.login(entity.getCreatedByDomain(), entity.getCreatedByEmail());
		if ((user.getRole() == UserRole.PLAYER) && (!entity.getActive()))
			throw new ForbiddenRequestException(
					"getSpecificInstance: Users with role PLAYER can only get active instances");
		else if (user.getRole() != UserRole.MANAGER)
			throw new ForbiddenRequestException(
					"getSpecificInstance: Only users with role PLAYER/MANAGER can get instances");

		return toBoundary(entity);
	}

	@Override
	public List<InstanceBoundary> getAllInstances() {
		return StreamSupport.stream(instancesDAO.findAll().spliterator(), false).map(this::toBoundary)
				.collect(Collectors.toList());
	}

	@Override
	public void deleteAllInstances() {
		instancesDAO.deleteAll();
	}

	// Pagination
	@Override
	public List<InstanceBoundary> getAllInstances(int size, int page) {
		return instancesDAO.findAll(PageRequest.of(page, size, Direction.DESC, "instanceId")).getContent().stream()
				.map(this::toBoundary).collect(Collectors.toList());
	}

	@Override
	public List<InstanceBoundary> getAllInstancesByName(String name, int size, int page) {
		return instancesDAO.getAllInstancesByName(name, PageRequest.of(page, size, Direction.DESC, "name")).stream()
				.map(this::toBoundary).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<InstanceBoundary> getAllInstancesByType(String type, int size, int page) {
		return instancesDAO.getAllInstancesByType(type, PageRequest.of(page, size, Direction.DESC, "type")).stream()
				.map(this::toBoundary).collect(Collectors.toList());
	}

	// Conversion
	
	public InstanceEntity toEntity(InstanceBoundary boundary) {
		InstanceEntity entity = new InstanceEntity();
		InstanceId instanceId;
		UserId userId;

		// Validation of instanceId
		if (boundary.getInstanceId() == null)
			throw new BadRequestException("Instances.toEntity: Instance's Id cannot be null");
		instanceId = boundary.getInstanceId();
		entity.setInstanceId(instanceId);

		// Validation of domain
		if (instanceId.getDomain() == null)
			throw new BadRequestException("Instances.toEntity: Instance's domain cannot be null");
		entity.getInstanceId().setDomain(instanceId.getDomain());

		// Validation of Id
		if (instanceId.getId() == null)
			throw new BadRequestException("Instances.toEntity: Instance's Id cannot be null");
		entity.getInstanceId().setId(instanceId.getId());

		// Validation of type
		if (boundary.getType() == null)
			throw new BadRequestException("Instances.toEntity: Instance's type cannot be null");
		entity.setType(boundary.getType());

		// Validation of name
		if (boundary.getName() == null)
			throw new BadRequestException("Instances.toEntity: Instance's name cannot be null");
		entity.setName(boundary.getName());

		// Validation of active
		if (boundary.getActive() == null)
			throw new BadRequestException("Instances.toEntity: Instance's active status cannot be null");
		entity.setActive(boundary.getActive());

		// Validation of createdTimestamp
		if (boundary.getCreatedTimestamp() == null)
			throw new BadRequestException("Instances.toEntity: Instance's creation timestamp cannot be null");
		entity.setCreatedTimestamp(boundary.getCreatedTimestamp());

		// Validation of createdBy
		if (boundary.getCreatedBy() == null)
			throw new BadRequestException("Instances.toEntity: Instance's creator cannot be null");
		userId = boundary.getCreatedBy().getUserId();
		if (userId == null)
			throw new BadRequestException("Instances.toEntity: Instance's creator cannot be null");
		if ((userId.getDomain() == null) || (userId.getDomain().isEmpty()))
			throw new BadRequestException("Instances.toEntity: Instance's creator's domain cannot be null or empty");
		entity.setCreatedByDomain(boundary.getCreatedBy().getUserId().getDomain());
		if ((userId.getEmail() == null) || (userId.getEmail().isEmpty()))
			throw new BadRequestException("Instances.toEntity: Instance's creator's email cannot be null or empty");
		entity.setCreatedByEmail(boundary.getCreatedBy().getUserId().getEmail());

		// Validation of location
		if (boundary.getLocation() == null)
			throw new BadRequestException("Instances.toEntity: Instance's location cannot be null");
		if ((boundary.getLocation().getLat() == null) || (boundary.getLocation().getLng() == null))
			throw new BadRequestException("Instances.toEntity: Instance's location cannot be null");
		entity.setLocation(boundary.getLocation().getLng(), boundary.getLocation().getLat());

		if (boundary.getInstanceAttributes() != null) {
			Map<String, Object> map = boundary.getInstanceAttributes();
			try {
				String json = jackson.writeValueAsString(map);
				entity.setInstanceAttributes(json);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			entity.setInstanceAttributes("");
		}

		return entity;
	}

	public InstanceBoundary toBoundary(InstanceEntity entity) {
		InstanceBoundary boundary = new InstanceBoundary();
		InstanceId instanceId;
		CreatedBy createdBy;
		Location location;

		// Validation of instanceId
		if (entity.getInstanceId() == null)
			throw new BadRequestException("Instances.toBoundary: Instance's Id 1 cannot be null");
		instanceId = entity.getInstanceId();
		boundary.setInstanceId(instanceId);

		// Validation of domain
		if (instanceId.getDomain() == null)
			throw new BadRequestException("Instances.toBoundary: Instance's domain cannot be null");
		boundary.getInstanceId().setDomain(instanceId.getDomain());

		// Validation of Id
		if (instanceId.getId() == null)
			throw new BadRequestException("Instances.toBoundary: Instance's Id 2 cannot be null");
		boundary.getInstanceId().setId(instanceId.getId());

		// Validation of type
		if (entity.getType() == null)
			throw new BadRequestException("Instances.toBoundary: Instance's type cannot be null");
		boundary.setType(entity.getType());

		// Validation of name
		if (entity.getName() == null)
			throw new BadRequestException("Instances.toBoundary: Instance's name cannot be null");
		boundary.setName(entity.getName());

		// Validation of active
		if (entity.getActive() == null)
			throw new BadRequestException("Instances.toBoundary: Instance's active status cannot be null");
		boundary.setActive(entity.getActive());

		// Validation of createdTimestamp
		if (entity.getCreatedTimestamp() == null)
			throw new BadRequestException("Instances.toBoundary: Instance's creation timestamp cannot be null");
		boundary.setCreatedTimestamp(entity.getCreatedTimestamp());

		// Validation of createdBy
		if ((entity.getCreatedByDomain() == null) || (entity.getCreatedByDomain().isEmpty()))
			throw new BadRequestException("Instances.toBoundary: Instance's creator's domain cannot be null or empty");
		if ((entity.getCreatedByEmail() == null) || (entity.getCreatedByEmail().isEmpty()))
			throw new BadRequestException("Instances.toBoundary: Instance's creator's email cannot be null or empty");
		createdBy = new CreatedBy(new UserId(entity.getCreatedByDomain(), entity.getCreatedByEmail()));
		boundary.setCreatedBy(createdBy);

		// Validation of location
		if ((entity.getLocationLat() == null) || (entity.getLocationLng() == null))
			throw new BadRequestException("Instances.toBoundary: Instance's location cannot be null");
		location = new Location(entity.getLocationLat(), entity.getLocationLng());
		boundary.setLocation(location);

		if (entity.getInstanceAttributes() != null) {
			try {
				@SuppressWarnings("unchecked")
				Map<String, Object> boundaryDetails = jackson.readValue(entity.getInstanceAttributes(), Map.class);
				boundary.setInstanceAttributes(boundaryDetails);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			boundary.setInstanceAttributes(new HashMap<String, Object>());
		}

		return boundary;
	}
	
	public List<InstanceBoundary> getAllByCreatedByDomainAndCreatedByEmail(String domain,String email, int size, int page) {
		return instancesDAO.getAllByCreatedByDomainAndCreatedByEmail(domain,email, PageRequest.of(page, size, Direction.DESC, "name")).stream()
				.map(this::toBoundary).collect(Collectors.toList());
	}
	
	public List<InstanceBoundary> getAllInstancesNear(Point location, Distance distance){
		return instancesDAO.getAllByLocationNear(location,distance).stream()
				.map(this::toBoundary).collect(Collectors.toList());
	}

	public List<InstanceBoundary> getAllByInstanceAttributesContainingAndNear(String str, Point point, Distance radius) {
		return instancesDAO.getAllByInstanceAttributesContainingAndLocationNear(str,point,radius).stream()
				.map(this::toBoundary).collect(Collectors.toList());
	}

	public List<InstanceBoundary> getAllByInstanceAttributesContainingAndNearAndNameContaining(
			String str, Point point, Distance distance, String name) {
		return instancesDAO.getAllByInstanceAttributesContainingAndLocationNearAndNameContaining(str,point,distance,name).stream()
				.map(this::toBoundary).collect(Collectors.toList());
	}

}