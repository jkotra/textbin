package top.stdin.textbin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class TextbinApplication {

	public static void main(String[] args) {
		SpringApplication.run(TextbinApplication.class, args);
	}
	
	@Bean
	public RestTemplate resttemplate() {
		return new RestTemplate();
	}

}
