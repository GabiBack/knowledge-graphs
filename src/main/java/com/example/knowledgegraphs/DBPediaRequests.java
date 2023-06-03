package com.example.knowledgegraphs;

import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public class DBPediaRequests {

    public DBPediaRequests() {
    }

    public ResponseEntity<String> getResource(String pageTitle) {
        String language = "en";
        String urlString = Defaults.DBPEDIA_API_URL + language + "/" + pageTitle;
        RestTemplate restTemplate = new RestTemplate();

        try {
            return restTemplate.getForEntity(urlString, String.class);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(urlString, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<String>  postSubject(String subject){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(subject, headers);
        ResponseEntity<String> response = restTemplate.exchange(Defaults.DBPEDIA_API_URL_ARTICLES, HttpMethod.POST, entity, String.class);

        return response;
    }
}
