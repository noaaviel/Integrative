package iob;

import java.io.File;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		try {
			Process process = Runtime.getRuntime().exec("setup.exe");
		} catch (IOException e) {
			System.out.println("Cant run client exe!" +e.getMessage());
		}
		SpringApplication.run(Application.class, args);
	}
}