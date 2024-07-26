package com.lolmaxlevel.oneclone_backend.utils;

import com.lolmaxlevel.oneclone_backend.model.Document;
import com.lolmaxlevel.oneclone_backend.model.Employee;

import java.util.HashMap;
import java.util.Map;

public class PlaceHoldersExtractor {
    public static Map<String, String> getPlaceholdersFromEmployee(Employee employee) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{{ NAME }}", employee.getName());
        placeholders.put("{{ SURNAME }}", employee.getSurname());
        placeholders.put("{{ SECOND_NAME }}", employee.getSecondName());
        placeholders.put("{{ FULL_NAME }}", employee.getSurname() + " " + employee.getName() + " " + employee.getSecondName());
        placeholders.put("{{ DATE_OF_BIRTH }}", employee.getDateOfBirth().toString());
        placeholders.put("{{ PASSPORT }}", employee.getDocumentSeries() + " " + employee.getDocumentNumber());
        placeholders.put("{{ ISSUED_BY }}", employee.getDocumentIssuedBy());
        placeholders.put("{{ ISSUE_DATE }}", employee.getDocumentIssuedDate().toString());
        placeholders.put("{{ INN }}", employee.getInn());
        placeholders.put("{{ COST }}", "1000");
        placeholders.put("{{ DOCUMENT_NUMBER }}", "123");
        placeholders.put("{{ BANK_NUMBER }}", employee.getBankAccount());
        placeholders.put("{{ DATE_START_FULL }}", "12.12.2021года");
        placeholders.put("{{ NATIONALITY }}", employee.getNationality().getGenitiveName());
        placeholders.put("{{ FIRST_LETTER }}", employee.getName().substring(0, 1));
        placeholders.put("{{ FIRST_LETTER_S }}", employee.getSecondName().substring(0, 1));
        return placeholders;
    }
    public static Map<String, String> getPlaceholdersFromDocument(Document document) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{{ DATE_START }}", document.getDateFrom().toString());
        placeholders.put("{{ DATE_END }}", document.getDateTo().toString());
        placeholders.put("{{ COST }}", document.getPrice().toString());
        placeholders.put("{{ DATE_START_FULL }}", document.getDateTo().toString());
        return placeholders;
    }
}
