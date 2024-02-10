package com.lolmaxlevel.oneclone_backend.utils;

public class TemplateFinder {
    private static String TEMPLATES_PATH = "src/main/resources/document_templates/";

    public static String getTemplatePath(String templateName, String companyName) {
        return TEMPLATES_PATH + companyName + "/" + templateName;
    }
}
