package com.example.knowledgegraphs;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@Service
public class DataExtractionService {

    private String subject = "";
    private List<String> predicates = new ArrayList<>();
    int count = 0;
    List<RDFGroup> rdfGroups = new ArrayList<>();
    DBPediaRequests dbPediaRequests = new DBPediaRequests();

    Logger LOGGER = Logger.getLogger(String.valueOf(DataExtractionService.class));

    public DataExtractionService() {
    }

    public void clearRdfGroupList(){
        this.rdfGroups.clear();
        this.predicates.clear();
        this.count=0;
        this.subject="";
    }

    public void clearCount(){
        this.count=0;
        this.subject="";
    }

    public List<RDFGroup> extractDataForOutcome(ResponseEntity<String> response){
        this.clearCount();
        return this.extractData(response);
    }

    public List<RDFGroup> extractData(ResponseEntity<String> response){
        List<String> entities = this.separateEntities(response);
        List<List<String>> separatedEntityElements = this.separateEntityElements(entities);
        Map<String, List<String>> predicateGroupsWithValues = this.getPredicateGroupsWithValues(separatedEntityElements);
//        predicateGroupsWithValues.forEach((predicate, values) -> separatePredicates(predicate));
//        predicateGroupsWithValues.forEach(this::presentInfoForThePredicateGroup);
        return this.separatePredicatesAndPresentInfoForThePredicateGroup(predicateGroupsWithValues);
    }

    private List<String> separateEntities(ResponseEntity<String> response){
        return Arrays.asList(response.toString().split(Pattern.quote(Defaults.ENTITY_DELIMITER)));
    }

    private List<List<String>> separateEntityElements(List<String> entities) {
        List<List<String>> separatedEntitiesElements = new ArrayList<>();
        for(String entity : entities) {
            separatedEntitiesElements.add(Arrays.asList(entity.split(Pattern.quote(Defaults.ENTITIES_ELEMENTS_DELIMITER))));
        }

        return separatedEntitiesElements;
    }

    private Map<String, List<String>> getPredicateGroupsWithValues(List<List<String>> separatedEntityElements) {
        Map<String, List<String>> entitiesValues = new HashMap<>();

        for(List<String> separatedEntity : separatedEntityElements) {
            try {
                if (this.subject.isEmpty()) {
                    this.subject = separatedEntity.get(0).substring(1);
                }
                String predicate = separatedEntity.get(1);
                if (!predicate.isEmpty()) {
                    predicate = predicate.substring(1);
                }
                String value = separatedEntity.get(2);
                this.putPredicatesWithValuesIntoMap(predicate, value, entitiesValues);
            } catch (ArrayIndexOutOfBoundsException n) {
                if (separatedEntityElements.size() != 3) {
                    LOGGER.log(Level.INFO, "Not complete entity: " + separatedEntity.toString());
                }
            }
        }

        return entitiesValues;
    }

    private void putPredicatesWithValuesIntoMap(String predicate, String value, Map<String, List<String>> entitiesValues){
        List<String> valueForCurrentKey = entitiesValues.get(predicate);
        if (valueForCurrentKey != null) {
            valueForCurrentKey.add(value);
        } else {
            entitiesValues.put(predicate, new ArrayList<>(List.of(value)));
        }
    }

    private List<RDFGroup> separatePredicatesAndPresentInfoForThePredicateGroup(Map<String, List<String>> predicateGroupsWithValues){
        List<String> predicates = new ArrayList<>();
        List<RDFGroup> rdfGroups = new ArrayList<>();

        predicateGroupsWithValues.forEach((predicate, values) -> separatePredicates(predicate, predicates));
        predicateGroupsWithValues.forEach(
                (predicate, values) ->
                        presentInfoForThePredicateGroup(predicate, values, rdfGroups,predicates));

        return rdfGroups;

    }

    private void separatePredicates(String predicateHttp, List<String> predicates){
        List<String> separatedPredicateHttpElements = Arrays.asList(predicateHttp.split(Pattern.quote(Defaults.URL_ELEMENTS_DELIMITER)));
        String separatedPredicateElement = separatedPredicateHttpElements.get(separatedPredicateHttpElements.size()-1);

        predicates.add(separatedPredicateElement);
    }


    private void presentInfoForThePredicateGroup(String predicate,
                                                 List<String> values,
                                                 List<RDFGroup> rdfGroups,
                                                 List<String> predicates){
        RDFGroup rdfGroup = new RDFGroup();
        StringBuilder valuesForThePredicateGroup = new StringBuilder();
        values.forEach(s -> valuesForThePredicateGroup.append(s).append(", "));

        rdfGroup.setSubject(this.subject);
        rdfGroup.setPredicate(predicate);
        rdfGroup.setValue(valuesForThePredicateGroup.toString());
        rdfGroup.setPredicateTheme(predicates.get(count));
        rdfGroups.add(rdfGroup);

        count++;
    }

    public List<RDFGroup> extractDataForPredicateSubject(List<RDFGroup> rdfGroups) throws NoSuchFieldException {
        List<String> listOfUrlsForPredicateSubject = this.extractListOfUrlsForPredicateSubject(rdfGroups);
        ResponseEntity<String>  responseForRandomSubject = this.getResponseForRandomSubjectAndGetResource(listOfUrlsForPredicateSubject);
        //this.clearRdfGroupList();
        this.clearCount();

        return this.extractData(responseForRandomSubject);
    }


    private List<String> extractListOfUrlsForPredicateSubject(List<RDFGroup> rdfGroups) throws NoSuchFieldException {
        String bodyUrls = "";
        for (RDFGroup rdfGroup : rdfGroups) {
            if (rdfGroup.getPredicateTheme().equals("subject")) {
                bodyUrls = rdfGroup.getValue();
            }
        }
        if (bodyUrls.equals("")) {
            throw new NoSuchFieldException("There is no \"subject\" category in the output");
        }

        return Arrays.asList(bodyUrls.split(","));
    }

    private ResponseEntity<String> getResponseForRandomSubjectAndGetResource(List<String> bodyUrls){
        String subjectUrl = "";

        do {
            subjectUrl = getRandomSubject(bodyUrls);
        } while(subjectUrl.isBlank() || (subjectUrl.toCharArray()[0] != 'h'));

        return this.dbPediaRequests.postSubject(subjectUrl);
    }

    private String getRandomSubject(List<String> bodyUrls){
        String randomUrl = this.getUrlByRandom(bodyUrls);

        return randomUrl.substring(1, randomUrl.length() - 1);
    }

    private String getUrlByRandom(List<String> bodyUrls){
        Random rand = new Random();
        String randomUrl;

        do {
            int randomNum = rand.nextInt(bodyUrls.size());
            randomUrl = bodyUrls.get(randomNum).trim();
        } while(randomUrl.isBlank());

        return randomUrl;
    }
}
