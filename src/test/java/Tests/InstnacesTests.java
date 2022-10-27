package Tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;
import iob.Boundaries.UserBoundary;
import iob.data.UserRole;
import iob.Application;
import iob.basics.CreatedBy;
import iob.basics.UserId;
import iob.Boundaries.NewUserBoundary;
import iob.basics.Location;
import iob.Boundaries.InstanceBoundary;

@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class InstnacesTests {

	private RestTemplate restTemplate;
	private String url;
	private int port;
	private String appName;
	private UserBoundary userPlayer;
	private UserBoundary userAdmin;
	private UserBoundary userManager;
	private String email;
	private String domain;

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + this.port;
		// All 3 types of users for testing
		//createUserForTestingManager();
		//createUserForTestingPLAYER();
		//createUserForTestingAdmin();
	}

	public void createUserForTestingPLAYER() {
		NewUserBoundary newUser = new NewUserBoundary();
		newUser.setEmail("yosiPlayer@gmail.com");
		newUser.setRole(UserRole.valueOf("PLAYER"));
		newUser.setAvatar("yay");
		newUser.setUsername("yosiPlayer");
		String createUserUrl = this.url + "/iob/users";
		this.userPlayer = this.restTemplate.postForObject(createUserUrl, newUser, UserBoundary.class);
	}

	public void createUserForTestingAdmin() {
		NewUserBoundary newUser = new NewUserBoundary();
		newUser.setEmail("yosiAdmin@gmail.com");
		newUser.setRole(UserRole.valueOf("ADMIN"));
		newUser.setAvatar("yay");
		newUser.setUsername("yosiAdmin");
		String createUserUrl = this.url + "/iob/users";
		this.userAdmin = this.restTemplate.postForObject(createUserUrl, newUser, UserBoundary.class);
	}

	public void createUserForTestingManager(String name) {
		NewUserBoundary newUser = new NewUserBoundary();
		newUser.setEmail(name+"@gmail.com");
		newUser.setRole(UserRole.valueOf("MANAGER"));
		newUser.setAvatar("bla");
		newUser.setUsername(name);
		String createUserUrl = this.url + "/iob/users";
		this.userManager = this.restTemplate.postForObject(createUserUrl, newUser, UserBoundary.class);
	}

	public InstanceBoundary getInsatnceBoundaryForTesting() {
		InstanceBoundary newInstance = new InstanceBoundary();
		newInstance.setType("fast food");
		newInstance.setName("new Deli");
		newInstance.setActive(true);
		newInstance.setCreatedBy(new CreatedBy(
				new UserId(domain, email)));
		newInstance.setLocation(new Location(16.317192752062, 76.0879815910421));
		Map<String, Object> map = new HashMap<String, Object>();
		String tags = "tags";
		String[] arrS = {"open 24/7","No disabled access"};
		map.put(tags, arrS);
		map.put("Rank", 5);
		map.put("City", "Givatayim");
		map.put("Description", "Sandwiches");
		map.put( "Opening Times", "11:00");
		map.put("Owner", new UserId("2022b.noa.aviel","barmiz@gmail.com"));
		newInstance.setInstanceAttributes(map);		
		return newInstance;
	}

	@Value("${spring.application.name:defaultName}")
	public void setSpringApplicatioName(String appName) {
		this.appName = appName;
	}

	@AfterEach
	public void teardown() {
//		 this.restTemplate.delete(this.url);
	}

	@Test
	public void addInstanceTest() {
		// Create the Player user
		createUserForTestingManager("yosiManager");
		// Create the Instance
		this.domain = this.userManager.getUserId().getDomain();
		this.email = this.userManager.getUserId().getEmail();
		InstanceBoundary newInstance = getInsatnceBoundaryForTesting();
		// Create the post url
		String postUrl = this.url + "/iob/instances";
				//+ "/" + appName + "/" + this.userPlayer.getUserId().getEmail();
		// Post the Instance	
		InstanceBoundary createdInstance = this.restTemplate.postForObject(postUrl, newInstance,
				InstanceBoundary.class);
		// Check the post values
		assertNotNull(createdInstance);
		assertThat(createdInstance.getInstanceId().getDomain().equals(appName));
		assertThat(createdInstance.getType().equals(newInstance.getType()));
		assertThat(createdInstance.getName().equals(newInstance.getName()));
		assertThat(createdInstance.getActive() == createdInstance.getActive());
		assertThat(createdInstance.getCreatedBy().getUserId().getDomain().equals(appName));
		assertThat(
				createdInstance.getCreatedBy().getUserId().getEmail().equals(this.userManager.getUserId().getEmail()));
	}

	@Test
	public void GetInsatnceActivaFalse() {
		// Create the Instance
		createUserForTestingAdmin();
		createUserForTestingPLAYER();
		createUserForTestingManager("yosiManager1");
		this.domain = this.userPlayer.getUserId().getDomain();
		this.email = this.userPlayer.getUserId().getEmail();
		InstanceBoundary newInstance = getInsatnceBoundaryForTesting();
		newInstance.setActive(false);
		String postUrl = this.url + "/iob/instances";
		// Create Player user and instance not active
		assertThrows(Exception.class, ()->
			this.restTemplate.postForObject(postUrl, newInstance, InstanceBoundary.class));
		// Manager get Instace URL
		this.domain = this.userManager.getUserId().getDomain();
		this.email = this.userManager.getUserId().getEmail();
		InstanceBoundary workInstance = getInsatnceBoundaryForTesting();
		InstanceBoundary postedInstance = this.restTemplate.postForObject(postUrl, workInstance, InstanceBoundary.class);
		String getInsatncePlyaerURL = this.url + "/iob/instances/" + this.appName + "/"
				+ this.userManager.getUserId().getEmail() + "/" + this.userManager.getUserId().getDomain() + "/"
				+ postedInstance.getInstanceId().getId();
		assertThrows(RuntimeException.class, () -> {
			// Try to get the Instance, expected exeption
			InstanceBoundary instance = this.restTemplate.getForObject(getInsatncePlyaerURL, InstanceBoundary.class);
		});

		//assertThat(workInstance.getInstanceId().equals(newInstance.getInstanceId()));
		assertThat(workInstance.getActive() == false);

		// For Admin
		// Manager get Instance URL
		this.domain = this.userAdmin.getUserId().getDomain();
		this.email = this.userAdmin.getUserId().getEmail();
		assertThrows(RuntimeException.class, () -> {
			url = this.url + "/iob/instances/" + this.appName + "/" + this.userAdmin.getUserId().getEmail() + "/"
					+ this.userAdmin.getUserId().getDomain() + "/" + postedInstance.getInstanceId().getId();
			InstanceBoundary createdInstanceAdmin = this.restTemplate.getForObject(url, InstanceBoundary.class);
		});		
	}
}
