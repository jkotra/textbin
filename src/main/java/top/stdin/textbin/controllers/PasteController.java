package top.stdin.textbin.controllers;

import java.util.*;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import top.stdin.textbin.entities.Paste;
import top.stdin.textbin.repositories.PasteRepository;
import top.stdin.textbin.utils.VerifyCaptcha;

import javax.annotation.PostConstruct;

@Slf4j
@RestController
@RequestMapping(path = "/api")
@CrossOrigin
public class PasteController {

	@Value("${redis.timeout.latest}")
	private Integer REDIS_LATEST_CACHE_TIMEOUT = 60;

	@Value("${redis.timeout.paste}")
	private Integer REDIS_PASTE_CACHE_TIMEOUT = 60;

	PasteRepository pr;
	VerifyCaptcha vc;
	RedisTemplate<String, String> rt;
	ObjectMapper om = new ObjectMapper();

	@PostConstruct
	public void postConstruct() {
		log.info("REDIS_LATEST_CACHE_TIMEOUT = {}", REDIS_LATEST_CACHE_TIMEOUT);
		log.info("REDIS_PASTE_CACHE_TIMEOUT = {}", REDIS_PASTE_CACHE_TIMEOUT);
	}

	public PasteController(PasteRepository pr, VerifyCaptcha vc, RedisTemplate<String, String> rt) {
		this.pr = pr;
		this.vc = vc;
		this.rt = rt;
	}
	
	@GetMapping(path = "latest")
	public Iterable<Paste> getlatest() throws JsonProcessingException {

		Boolean isCached = rt.hasKey("latest");
		if (isCached != null && isCached) {
			log.info("cache hit");
			String data = rt.opsForValue().get("latest");
			return Arrays.asList(this.om.readValue(data, Paste[].class));
		}

		log.info("cache miss");
		List<Paste> latest = this.pr.findTop5ByOrderByIdDesc();
		rt.opsForValue().set("latest", this.om.writeValueAsString(latest));
		rt.expire("latest", REDIS_LATEST_CACHE_TIMEOUT, TimeUnit.MINUTES);

		return latest;
	}

	@GetMapping
	public Paste find(@RequestParam(name="uuid") String uuid) throws Exception {
		Boolean isCached = rt.hasKey(uuid);
		if (isCached != null && isCached){
			log.info("cache hit for {}", uuid);
			String val = rt.opsForValue().get(uuid);
			return om.readValue(val, Paste.class);
		}
		Paste r = this.pr.findByUuid(UUID.fromString(uuid)).get(0).remove_delete_key();
		rt.opsForValue().set(uuid, om.writeValueAsString(r));
		rt.expire(uuid, 60, TimeUnit.MINUTES);
		return r;
	}
	
	@GetMapping("delete/{uuid}")
	public void delete(@PathVariable(name="uuid") String uuid ,@RequestParam(name="key") String deletekey) {
		log.info("{}  {}", uuid, deletekey);
		Paste p = this.pr.findByUuid(UUID.fromString(uuid)).get(0);
		if (p.getDeletekey().equals(deletekey)){
			this.pr.delete(p);

			// delete `latest` and individual paste cache entry.
			this.rt.delete("latest");
			this.rt.delete(p.getUuid().toString());

		}
	}
	
	@PostMapping
	public Paste post(@RequestBody Paste paste) throws Exception {
		log.info("request: {}", paste.toString());
		if (this.vc.verify(paste.getCaptcha())) {
			this.rt.delete("latest");
			Paste r = this.pr.save(paste);
			rt.opsForValue().set(r.getUuid().toString(), om.writeValueAsString(r));
			rt.expire(r.getUuid().toString(), REDIS_PASTE_CACHE_TIMEOUT, TimeUnit.MINUTES);
			return r;
		}
		else {
			throw new Exception("Invalid Google Captcha");
		}
	}
	
}
