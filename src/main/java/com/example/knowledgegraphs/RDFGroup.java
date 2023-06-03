package com.example.knowledgegraphs;

public class RDFGroup {

    private String subject;
    private String predicate;
    private String predicateTheme;
    private String value;

    public RDFGroup(String predicate, String predicateTheme, String value) {
        this.predicate = predicate;
        this.predicateTheme = predicateTheme;
        this.value = value;
    }

    public RDFGroup() {
    }


    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getPredicateTheme() {
        return predicateTheme;
    }

    public void setPredicateTheme(String predicateTheme) {
        this.predicateTheme = predicateTheme;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
