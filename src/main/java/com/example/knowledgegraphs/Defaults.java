package com.example.knowledgegraphs;

public class Defaults {

    public static final String DBPEDIA_API_URL = "http://api.live.dbpedia.org/resource/";
    public static final String DBPEDIA_API_URL_ARTICLES = "http://api.live.dbpedia.org/sync/articles";

    public static final String ENTITY_DELIMITER = " .";
    public static final String ENTITIES_ELEMENTS_DELIMITER = "> ";
    public static final String URL_ELEMENTS_DELIMITER = "/";

    public static final String OPERATION_SUCCESS = "\"Successfully performed " + Defaults.NUMBER_OF_RESOURCES + " operations\"";
    public static final String NOT_ENOUGH_PREDICATES = "There is not enough predicates to continue";

    public static final int NUMBER_OF_RESOURCES = 10;
}
