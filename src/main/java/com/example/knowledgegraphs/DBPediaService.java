package com.example.knowledgegraphs;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class DBPediaService {

    DBPediaRequests dbPediaRequests = new DBPediaRequests();
    DataExtractionService dataExtractionService = new DataExtractionService();
    DataExtractionService dataExtractionServiceForSubject = new DataExtractionService();
    int iterationNumber = 0;

    Logger LOGGER = Logger.getLogger(String.valueOf(DBPediaService.class));

    //@PostConstruct
    public void start() throws NoSuchFieldException {
        this.generateWikiChain("Robert_Kubica");
    }

    public String generateWikiChain(String name) throws NoSuchFieldException {
        this.setDefaults();
        return this.resourcesManagement(name);
    }


    public String resourcesManagement(String currentResource) throws NoSuchFieldException {
        if (this.iterationNumber == Defaults.NUMBER_OF_RESOURCES) {
            return Defaults.OPERATION_SUCCESS;
        } else {
            String nextResource = "";
            nextResource = this.fetchResource(currentResource);
            return resourcesManagement(nextResource);
        }
    }

    public String fetchResource(String pageTitle) throws NoSuchFieldException {
        ResponseEntity<String> response = dbPediaRequests.getResource(pageTitle);

        List<RDFGroup> rdfGroups = dataExtractionService.extractDataForOutcome(response);
        List<RDFGroup> rdfGroupsFromSubjects = dataExtractionServiceForSubject.extractDataForPredicateSubject(rdfGroups);

        this.exportInitialDataToTxt(response, pageTitle);
        this.exportToExcel(pageTitle, rdfGroups, rdfGroupsFromSubjects);
        this.iterationNumber++;

        return this.getNextResource(rdfGroupsFromSubjects);
    }

    private String getNextResource(List<RDFGroup> rdfGroupsFromSubjects) {
        String subject = rdfGroupsFromSubjects.get(0).getSubject();
        List<String> subjectSplit = Arrays.asList(subject.split(Defaults.URL_ELEMENTS_DELIMITER));
        int subjectSplitSize = subjectSplit.size();

        return subjectSplit.get(subjectSplitSize-1);
    }

    private void exportInitialDataToTxt(ResponseEntity<String> response, String pageTitle){
        try (PrintWriter out = new PrintWriter(pageTitle)) {
            out.println(response);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void exportToExcel(String pageTitle, List<RDFGroup> rdfGroups, List<RDFGroup> rdfGroupsFromSubject) {
        ExcelFileExporter excelFileExporter = new ExcelFileExporter();
        try {
            excelFileExporter.writeExcel(rdfGroups, iterationNumber +"_"+ pageTitle + ".xlsx", rdfGroupsFromSubject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDefaults(){
        this.iterationNumber=0;
        this.dbPediaRequests = new DBPediaRequests();
        this.dataExtractionService = new DataExtractionService();
        this.dataExtractionServiceForSubject = new DataExtractionService();
    }

}


