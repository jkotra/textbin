package top.stdin.textbin.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import top.stdin.textbin.entities.Paste;
import top.stdin.textbin.repositories.PasteRepository;

@Controller
@CrossOrigin
public class GraphQLController {

    PasteRepository pr;
    PasteController pc;

    GraphQLController(PasteRepository pr, PasteController pc) {
        this.pr = pr;
        this.pc = pc;
    }

    @QueryMapping
    Iterable<Paste> getLatestPastes() throws JsonProcessingException {
        return this.pc.getlatest();
    };




}
