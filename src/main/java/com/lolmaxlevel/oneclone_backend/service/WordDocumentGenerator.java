package com.lolmaxlevel.oneclone_backend.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WordDocumentGenerator {

    private static final String TEMPLATE_DIRECTORY_ROOT = "TEMPLATES_DIRECTORY/";

    public byte[] generateFromTemplate(String templateName, Map<String, String> placeholders) {

        try (FileInputStream fis = new FileInputStream(templateName);
             XWPFDocument document = new XWPFDocument(fis);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            // Handle placeholders in paragraphs
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                handlePlaceholdersInRuns(paragraph.getRuns(), placeholders);
            }

            // Handle placeholders in tables
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            handlePlaceholdersInRuns(paragraph.getRuns(), placeholders);
                        }
                    }
                }
            }

            // Save the document to a byte array
            document.write(baos);

            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Error while generating document from template", e);
            return null;
        }
    }

    private void handlePlaceholdersInRuns(List<XWPFRun> runs, Map<String, String> placeholders) {
        int start = 0;
        int end = 0;
        StringBuilder sb = new StringBuilder();
        boolean isPlaceholder = false;
        for (int i = 0; i < runs.size(); i++) {
            XWPFRun run = runs.get(i);
            String text = run.getText(0);
            if (text != null) {
                if (text.contains("{")) {
                    isPlaceholder = true;
                }
                if (isPlaceholder) {
                    sb.append(text);
                    run.setText("", 0);
                }
                if (text.contains("}")) {
                    isPlaceholder = false;

                    for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                        String placeholder = entry.getKey();
                        if (sb.toString().contains(placeholder)) {
                            sb = new StringBuilder(sb.toString().replace(placeholder, entry.getValue()));
                        }
                    }
                    run.setText(sb.toString(), 0);
                    sb = new StringBuilder();
                }
            }
        }
    }
}