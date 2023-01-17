package top.stdin.textbin;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import top.stdin.textbin.entities.Paste;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
@SpringBootTest
public class ApiTests {

    @Autowired
    WebApplicationContext context;

    protected MockMvc mockmvc;

    private ObjectMapper om = new ObjectMapper();

    @BeforeEach
    public void setup() {
        this.mockmvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void getPastes() throws Exception {
        MvcResult result = this.mockmvc.perform(
                get("/api/latest").accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andExpect(jsonPath("$").isArray()).andReturn();
    }

    @Test
    public void newPaste() throws Exception {
        Paste p = new Paste();
        p.setTitle("test");
        p.setText("test");
        p.setCaptcha("bypass");

        String ser = om.writeValueAsString(p);

        MvcResult result = mockmvc.perform(
                post("/api").contentType(MediaType.APPLICATION_JSON).content(ser)
        ).andExpect(status().isOk()).andReturn();

        String resp = result.getResponse().getContentAsString();
        log.info("resp = {}", resp);

        Paste inserted = om.readValue(resp, Paste.class);
        log.info("object = {}", inserted);

        // delete paste
        mockmvc.perform(
                get(String.format("/api/delete/%s?key=%s", inserted.getUuid(), inserted.getDeletekey()))
        ).andExpect(status().isOk());

    }

}
