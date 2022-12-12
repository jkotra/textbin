package top.stdin.textbin.utils;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VerifyCaptcha {
	
	RestTemplate rt;
	
	@Value("${grc.secret}")
	String GRCSecret;
	
	@Data
	@NoArgsConstructor
	private static class GRCResponse {
		Boolean success;
	}
	
	VerifyCaptcha(RestTemplate rt){
		this.rt = rt;
	};
	
	public Boolean verify(String response) {
		HashMap<String, String> request = new HashMap<>();
		request.put("secret", this.GRCSecret);
		request.put("response", response);
		
		log.info("secret: {}", this.GRCSecret);
		
		GRCResponse grcresponse = this.rt.postForObject(
				"https://www.google.com/recaptcha/api/siteverify?secret={secret}&response={response}",
				null,
				GRCResponse.class,
				request);
		
		System.out.println(grcresponse.getSuccess());
		
		return grcresponse.getSuccess();
	}

}
