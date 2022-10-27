package iob.JPAServices;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import iob.Boundaries.NewUserBoundary;
import iob.Boundaries.UserBoundary;
import iob.DAOs.UsersDAO;
import iob.Exceptions.BadRequestException;
import iob.Exceptions.NotFoundException;
import iob.basics.UserId;
import iob.data.UserEntity;
import iob.data.UserRole;
import iob.logic.EnhancedUsersService;

@Service
@Scope("singleton")
public class JPAUsersService implements EnhancedUsersService {
	// Fields
	private String appName;

	private UsersDAO usersDAO;
	private ObjectMapper jackson;
	private String defaultRoleString;

	// Constructors
	@Autowired
	public JPAUsersService(UsersDAO usersDAO) {
		setUsersDAO(usersDAO);
	}

	// Getters & Setters
	@Value("${spring.application.name:defaultName}")
	public void setSpringApplicatioName(String appName) {
		this.appName = appName;
	}

	public UsersDAO getUsersDAO() {
		return usersDAO;
	}

	public void setUsersDAO(UsersDAO usersDAO) {
		this.usersDAO = usersDAO;
	}

	@Value("${role.defaultrole.value:error}")
	public void setDefaultRoleString(String defaultRoleString) {
		this.defaultRoleString = defaultRoleString;
	}

	// Methods
	@PostConstruct
	public void init() {
		this.jackson = new ObjectMapper();
	}

	@Override
	public UserBoundary createUser(NewUserBoundary newUser) {
		UserBoundary user = new UserBoundary();

		if (newUser == null)
			throw new BadRequestException("createUser: No user data provided");

		// Validation of email
		if ((newUser.getEmail() == null) || (newUser.getEmail().isEmpty()))
			throw new BadRequestException("createUser: User's email cannot be null or empty");
		if (!Pattern.compile("^(.+)@(\\S+)$").matcher(newUser.getEmail()).matches())
			throw new BadRequestException("createUser: User's email is invalid");
		if (userExists(newUser))
			throw new BadRequestException("createUser: User already exists");
		user.setUserId(new UserId(appName, newUser.getEmail()));

		// Validation of username
		if ((newUser.getUsername() == null) || (newUser.getUsername().isEmpty()))
			throw new BadRequestException("createUser: User's name cannot be null or empty");
		user.setUsername(newUser.getUsername());

		// Validation of role
		if (newUser.getRole() == null)
			throw new BadRequestException("createUser: User's role cannot be null");
		if (UserRole.valueOf(newUser.getRole().toString().toUpperCase()) == null)
			throw new BadRequestException("createUser: User's role can only be ADMIN, MANAGER or PLAYER");
		user.setRole(newUser.getRole());

		// Validation of avatar
		if ((newUser.getAvatar() == null) || (newUser.getAvatar().isEmpty()))
			throw new BadRequestException("createUser: User's avatar cannot be null or empty");
		user.setAvatar(newUser.getAvatar());
		
		UserEntity entity = this.toEntity(user);
		entity = usersDAO.save(entity);

		return toBoundary(entity);
	}

	@Override
	public UserBoundary login(String userDomain, String userEmail) {
		Optional<UserEntity> entityOp = usersDAO.findById(new UserId(userDomain, userEmail));

		if (!entityOp.isPresent())
			throw new NotFoundException("login: There's no user with email: " + userEmail);

		return toBoundary(entityOp.get());
	}

	@Override
	public void updateUser(String userDomain, String userEmail, UserBoundary update) {
		Optional<UserEntity> optionalUser = usersDAO.findById(new UserId(userDomain, userEmail));
		UserEntity entity;

		if (!optionalUser.isPresent())
			throw new NotFoundException("updateUser: There's no user with email: " + userEmail);
		entity = optionalUser.get();

		// Validation of username
		if ((update.getUsername() == null) || (update.getUsername().isEmpty()))
			throw new BadRequestException("updateUser: User's email cannot be null or empty");
		entity.setUsername(update.getUsername());

		// Validation of role
		if (update.getRole() == null)
			throw new BadRequestException("updateUser: User's role cannot be null");
		if (UserRole.valueOf(update.getRole().toString().toUpperCase()) == null)
			throw new BadRequestException("updateUser: User's role can only be ADMIN, MANAGER or PLAYER");
		entity.setRole(update.getRole());

		// Validation of avatar
		if ((update.getAvatar() == null) || (update.getAvatar().isEmpty()))
			throw new BadRequestException("updateUser: User's avatar cannot be null or empty");
		entity.setAvatar(update.getAvatar());

		entity = usersDAO.save(entity);
	}

	@Override
	public List<UserBoundary> getAllUsers() {
		return StreamSupport.stream(usersDAO.findAll().spliterator(), false).map(this::toBoundary)
				.collect(Collectors.toList());
	}

	@Override
	public void deleteAllUsers() {
		usersDAO.deleteAll();
	}

	// Pagination
	@Override
	public List<UserBoundary> getAllUsers(int size, int page) {
		return usersDAO.findAll(PageRequest.of(page, size, Direction.DESC, "userId")).getContent().stream()
				.map(this::toBoundary).collect(Collectors.toList());
	}

	@Override
	public List<UserBoundary> getAllUsersByRole(UserRole role, int size, int page) {
		List<UserEntity> matchingUsers = usersDAO.getAllUsersByRole(role,
				PageRequest.of(page, size, Direction.DESC, "role"));

		return StreamSupport.stream(matchingUsers.spliterator(), false).map(this::toBoundary)
				.collect(Collectors.toList());
	}

	@Override
	public List<UserBoundary> getAllUsersByUsername(String username, int size, int page) {
		List<UserEntity> matchingUsers = usersDAO.getAllUsersByUsername(username,
				PageRequest.of(page, size, Direction.DESC, "username"));

		return StreamSupport.stream(matchingUsers.spliterator(), false).map(this::toBoundary)
				.collect(Collectors.toList());
	}

	@Override
	public List<UserBoundary> getAllUsersByAvatar(String avatar, int size, int page) {
		List<UserEntity> matchingUsers = usersDAO.getAllUsersByAvatar(avatar,
				PageRequest.of(page, size, Direction.DESC, "avatar"));

		return StreamSupport.stream(matchingUsers.spliterator(), false).map(this::toBoundary)
				.collect(Collectors.toList());
	}

	// Conversion
	public UserEntity toEntity(UserBoundary boundary) {
		UserEntity entity = new UserEntity();
		UserId userId;

		// Validation of userId
		if (boundary.getUserId() == null)
			throw new BadRequestException("Users.toEntity: User's Id cannot be null");
		userId = boundary.getUserId();
		entity.setUserId(userId);

		// Validation of domain
		if ((userId.getDomain() == null) || (userId.getDomain().isEmpty()))
			throw new BadRequestException("Users.toEntity: User's domain cannot be null or empty");
		entity.getUserId().setDomain(userId.getDomain());

		// Validation of email
		if ((userId.getEmail() == null) || (userId.getEmail().isEmpty()))
			throw new BadRequestException("Users.toEntity: User's email cannot be null or empty");
		entity.getUserId().setEmail(userId.getEmail());

		// Validation of role
		if (boundary.getRole() == null)
			throw new BadRequestException("Users.toEntity: User's role cannot be null");
		entity.setRole(boundary.getRole());

		// Validation of username
		if (boundary.getUsername() == null)
			throw new BadRequestException("Users.toEntity: User's username cannot be null or empty");
		entity.setUsername(boundary.getUsername());

		// Validation of avatar
		if (boundary.getAvatar() == null)
			throw new BadRequestException("Users.toEntity: User's avatar cannot be null or empty");
		entity.setAvatar(boundary.getAvatar());

		return entity;
	}

	public UserBoundary toBoundary(UserEntity entity) {
		UserBoundary boundary = new UserBoundary();
		UserId userId;

		// Validation of userId
		if (entity.getUserId() == null)
			throw new BadRequestException("Users.toBoundary: User's Id cannot be null");
		userId = entity.getUserId();
		boundary.setUserId(userId);

		// Validation of domain
		if ((userId.getDomain() == null) || (userId.getDomain().isEmpty()))
			throw new BadRequestException("Users.toBoundary: User's domain cannot be null or empty");
		boundary.getUserId().setDomain(userId.getDomain());

		// Validation of email
		if ((userId.getEmail() == null) || (userId.getEmail().isEmpty()))
			throw new BadRequestException("Users.toBoundary: User's email cannot be null or empty");
		boundary.getUserId().setEmail(userId.getEmail());

		// Validation of role
		if (entity.getRole() == null)
			throw new BadRequestException("Users.toBoundary: User's role cannot be null");
		boundary.setRole(entity.getRole());

		// Validation of username
		if (entity.getUsername() == null)
			throw new BadRequestException("Users.toBoundary: User's username cannot be null or empty");
		boundary.setUsername(entity.getUsername());

		// Validation of avatar
		if (entity.getAvatar() == null)
			throw new BadRequestException("Users.toBoundary: User's avatar cannot be null or empty");
		boundary.setAvatar(entity.getAvatar());

		return boundary;
	}

	public boolean userExists(NewUserBoundary nb) {
		List<UserBoundary> newList = getAllUsers();

		for (UserBoundary user : newList)
			if (user.getUserId().getEmail().equalsIgnoreCase(nb.getEmail()))
				return true;

		return false;
	}
}