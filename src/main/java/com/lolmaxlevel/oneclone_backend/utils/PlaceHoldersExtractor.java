package com.lolmaxlevel.oneclone_backend.utils;

import com.lolmaxlevel.oneclone_backend.model.Document;
import com.lolmaxlevel.oneclone_backend.model.Employee;
import com.lolmaxlevel.oneclone_backend.types.DocumentType;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PlaceHoldersExtractor {
    public static Map<String, String> getPlaceholdersFromEmployee(Employee employee) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{{ NAME }}", employee.getName());
        placeholders.put("{{ SURNAME }}", employee.getSurname());
        placeholders.put("{{ SECOND_NAME }}", employee.getSecondName());
        placeholders.put("{{ FULL_NAME }}", employee.getSurname() + " " + employee.getName() + " " + employee.getSecondName());
        placeholders.put("{{ DATE_OF_BIRTH }}", DateUtils.formatDate(employee.getDateOfBirth()));
        placeholders.put("{{ PASSPORT }}", employee.getDocumentSeries() + " " + employee.getDocumentNumber());
        placeholders.put("{{ ISSUED_BY }}", employee.getDocumentIssuedBy());
        placeholders.put("{{ ISSUE_DATE }}", DateUtils.formatDate(employee.getDocumentIssuedDate()));
        placeholders.put("{{ INN }}", employee.getInn());
        placeholders.put("{{ DOCUMENT_NUMBER }}", employee.getDocumentNumber());
        placeholders.put("{{ BANK_NUMBER }}", employee.getBankAccount());
        placeholders.put("{{ NATIONALITY }}", employee.getNationality().getGenitiveName());
        placeholders.put("{{ FIRST_LETTER }}", employee.getName().substring(0, 1));
        placeholders.put("{{ FIRST_LETTER_S }}", employee.getSecondName().substring(0, 1));
        placeholders.put("{{ CONTRACT_NUMBER }}", employee.getCompanySpecificId() + "/2024");
        placeholders.put("{{ DOCUMENT_TYPE }}", employee.getDocumentType().getName());
        placeholders.put("{{ DOCUMENT_SERIES }}", employee.getDocumentSeries());
        return placeholders;
    }

    public static Map<String, String> getPlaceholdersFromDocument(Document document) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{{ DATE_START }}", DateUtils.formatDate(document.getDateFrom()));
        placeholders.put("{{ DATE_END }}", DateUtils.formatDate(document.getDateTo()));
        placeholders.put("{{ COST }}", String.valueOf(document.getPrice().intValue()));
        placeholders.put("{{ DATE_START_FULL }}", document.getDateTo().toString());
        placeholders.put("{{ DOP_FIRST_DATE }}", DateUtils.formatDateFull(document.getDateFrom()));
        placeholders.put("{{ DOP_SECOND_DATE }}", DateUtils.formatDateFull(document.getDateTo()));
        return placeholders;
    }
}
