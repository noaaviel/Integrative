package Tests;

import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import iob.Boundaries.UserBoundary;
import iob.data.UserRole;
import iob.Application;
import iob.Boundaries.NewUserBoundary;
//@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class ApplicationTests {
	private RestTemplate restTemplate;
	private String url;
	private int port;
	private String appName;

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + this.port;
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
	public void addUser() {
		this.restTemplate = new RestTemplate();
		url = this.url + "/iob/users";
		NewUserBoundary newUser = setNewUserForTesting("barm471@gmail.com", "PLAYER", "yay", "yosi");
		UserBoundary user = this.restTemplate.postForObject(url, newUser, UserBoundary.class);
		assertThat(user.getUserId().getDomain().equals(appName));
		assertThat(newUser.getEmail().equals(user.getUserId().getEmail()));
		assertThat(newUser.getAvatar().equals(user.getAvatar()));
		assertThat(newUser.getRole() == user.getRole());
		assertThat(newUser.getUsername().equals(user.getUsername()));
		assertThat("x".equals("x"));
		
	}

	@Test
	public void testLoginUser() {
		// Create user
		NewUserBoundary newUser = setNewUserForTesting("barm471@gmail.com", "PLAYER", "yay", "yosi");
		String addUrl = this.url + "/iob/users";
		// Add user
		//this.restTemplate.postForObject(addUrl, newUser, UserBoundary.class);
		// Login
		String loginUrl = this.url + "/iob/users/login/" + appName + "/barm471@gmail.com";
		UserBoundary userLoggedIn = this.restTemplate.getForObject(loginUrl, UserBoundary.class);
		// Tests
		assertThat(newUser.getEmail().equals(userLoggedIn.getUserId().getEmail()));
		assertThat(userLoggedIn.getUserId().getDomain().equals(appName));
		assertThat(newUser.getAvatar().equals(userLoggedIn.getAvatar()));
		assertThat(newUser.getRole() == userLoggedIn.getRole());
		assertThat(newUser.getUsername().equals(userLoggedIn.getUsername()));
	}

	@Test
	public void contextLoads() throws Exception {
		String st = "x";
		assertThat(st).isEqualTo("x");
	}

	public NewUserBoundary setNewUserForTesting(String email, String role, String avatar, String username) {
		NewUserBoundary newUser = new NewUserBoundary();
		newUser.setEmail(email);
		newUser.setRole(UserRole.valueOf(role));
		newUser.setAvatar(avatar);
		newUser.setUsername(username);
		return newUser;
	}

	@Test
	public void updateUser() {
		// Create user
		NewUserBoundary newUser = setNewUserForTesting("barm4711@gmail.com", "PLAYER", "yay", "yosi1");
		String addUrl = this.url + "/iob/users";
		//UserBoundary userAdded = this.restTemplate.postForObject(addUrl, newUser, UserBoundary.class);
		this.restTemplate.postForObject(addUrl, newUser, UserBoundary.class);
		// Change values
		newUser.setRole(UserRole.valueOf("ADMIN"));
		newUser.setAvatar("bar");
		newUser.setUsername("barmiz");
		// update
		String updateUrl = this.url + "/iob/users/" + appName + "/barm471@gmail.com";
		this.restTemplate.put(updateUrl, newUser);
		// login
		String loginUrl = this.url + "/iob/users/login/" + appName + "/barm471@gmail.com";
		UserBoundary UpdatedUser = this.restTemplate.getForObject(loginUrl, UserBoundary.class); // Re login with new
		// tests
		assertThat(newUser.getEmail().equals(UpdatedUser.getUserId().getEmail()));
		assertThat(UpdatedUser.getUserId().getDomain().equals(appName));
		assertThat(UpdatedUser.getAvatar().equals(newUser.getAvatar()));
		assertThat(UpdatedUser.getRole() == newUser.getRole());
		assertThat(UpdatedUser.getUsername().equals(newUser.getUsername()));
	}
	
	@Test
	@DisplayName("when db contains a user that we try to insert")
	public void testWhenDBContainsASingleMessageWithTheProperYSubAttrASearchOfTheFirstPageWithSizeZeroByYRetrievesError() throws Exception{
		assertThrows(Exception.class, ()->
		this.restTemplate.postForObject(url, setNewUserForTesting("barm471@gmail.com", "PLAYER", "yay", "yosi"), UserBoundary.class));
	}
}
