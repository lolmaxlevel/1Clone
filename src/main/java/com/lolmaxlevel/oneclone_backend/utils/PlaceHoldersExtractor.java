package com.lolmaxlevel.oneclone_backend.utils;

import com.lolmaxlevel.oneclone_backend.model.Employee;

import java.util.HashMap;
import java.util.Map;

public class PlaceHoldersExtractor {
    public static Map<String, String> getPlaceholders(Employee employee) {
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
        placeholders.put("{{ DATE_START }}", "12.12.2021");
        placeholders.put("{{ DATE_END }}", "12.12.2021");
        placeholders.put("{{ DOCUMENT_NUMBER }}", "123");
        placeholders.put("{{ BANK_NUMBER }}", employee.getBankAccount());
        placeholders.put("{{ DATE_START_FULL }}", "12.12.2021года");
        placeholders.put("{{ NATIONALITY }}", employee.getNationality());
        placeholders.put("{{ FIRST_LETTER }}", employee.getName().substring(0, 1));
        placeholders.put("{{ FIRST_LETTER_S }}", employee.getSecondName().substring(0, 1));

        return placeholders;
    }
}
