package tn.IIT.mentorat_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.context.annotation.ComponentScan(basePackages = "tn.IIT.mentorat_platform")
public class MentoratPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(MentoratPlatformApplication.class, args);
	}

}
