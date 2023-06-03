package com.example.knowledgegraphs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.HttpClientErrorException;

@Controller
public class DBPediaController {

    @Autowired
    DBPediaService dbPediaService;

    @GetMapping(path = "/pageTitle/{pageTitle}")
    public String fetchResources(@PathVariable String pageTitle) throws NoSuchFieldException {
        return this.dbPediaService.generateWikiChain(pageTitle).equals(Defaults.OPERATION_SUCCESS) ? "Success" : "Fail";
    }

}
