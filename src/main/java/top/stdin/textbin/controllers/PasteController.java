package top.stdin.textbin.controllers;

import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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

@Slf4j
@RestController
@RequestMapping(path = "/api")
@CrossOrigin
public class PasteController {
	
	PasteRepository pr;
	VerifyCaptcha vc;
	
	public PasteController(PasteRepository pr, VerifyCaptcha vc) {
		this.pr = pr;
		this.vc = vc;
	}
	
	@GetMapping(path = "latest")
	public Iterable<Paste> getlatest() {
		return this.pr.findTop5ByOrderByIdDesc();
	}

	@GetMapping
	public Paste find(@RequestParam(name="uuid") String uuid) {
		return this.pr.findByUuid(UUID.fromString(uuid)).get(0).remove_delete_key();
	}
	
	@GetMapping("delete/{uuid}")
	public void delete(@PathVariable(name="uuid") String uuid ,@RequestParam(name="key") String deletekey) {
		log.info("{}  {}", uuid, deletekey);
		Paste p = this.pr.findByUuid(UUID.fromString(uuid)).get(0);
		if (p.getDeletekey().equals(deletekey)){
			this.pr.delete(p);
		}
	}
	
	@PostMapping
	public Paste post(@RequestBody Paste paste) throws Exception {
		log.info("request: {}", paste.toString());
		if (this.vc.verify(paste.getCaptcha())) {
			return this.pr.save(paste);
		}
		else {
			throw new Exception("Invalid Google Captcha");
		}
	}
	
}
