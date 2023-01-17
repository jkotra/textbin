package top.stdin.textbin;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.stdin.textbin.entities.Paste;
import top.stdin.textbin.repositories.PasteRepository;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Slf4j
class TextbinApplicationTests {

	@Autowired
	PasteRepository pr;

	private Paste MyPaste;

	@Test
	void contextLoads() {
	}

	@BeforeEach
	public void initMyPaste(){
		this.MyPaste = new Paste();
		this.MyPaste.setTitle("Test");
		this.MyPaste.setText("Something");
		this.MyPaste.setDate(new Date().toInstant().toEpochMilli());
		this.MyPaste.setCaptcha("BYPASS");

		this.MyPaste = this.pr.save(MyPaste);
	};

	@Test
	public void newPaste() {
		assertFalse(pr.findByUuid(
				MyPaste.getUuid()
		).isEmpty());
	}

	@Test
	public void deletePaste() {
		this.pr.delete(MyPaste);
		assertTrue(this.pr.findByUuid(MyPaste.getUuid()).isEmpty());
	}

	@AfterEach
	public void cleanup() {

		if (!this.pr.findByUuid(MyPaste.getUuid()).isEmpty()){
			this.pr.delete(MyPaste);
		}

	}

}
