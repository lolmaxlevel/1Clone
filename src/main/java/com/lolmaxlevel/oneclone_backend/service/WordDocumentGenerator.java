package com.lolmaxlevel.oneclone_backend.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Map;

@Slf4j
@Service
public class WordDocumentGenerator {

    public byte[] generateFromTemplate(String templatePath, Map<String, String> placeholders) {
        try {
            // Read the template
            File template = new File(templatePath);
            if (!template.exists()) {
                log.error("Template not found: {}", templatePath);
                return null;
            }

            // Create a document from the template
            XWPFDocument document = new XWPFDocument(new FileInputStream(template));

            // Replace placeholders with actual values
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    String text = run.getText(0);
                    if (text != null) {
                        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                            if (text.contains(entry.getKey())) {
                                text = text.replace(entry.getKey(), entry.getValue());
                                run.setText(text, 0);
                            }
                        }
                    }
                }
            }
            // Convert the document to a byte array
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.write(out);
            byte[] documentBytes = out.toByteArray();

            // Close resources
            out.close();
            document.close();

            return documentBytes;
        } catch (IOException e) {
            log.error("Error while generating word document: {}", e.getMessage());
            return null;
        }
    }
}