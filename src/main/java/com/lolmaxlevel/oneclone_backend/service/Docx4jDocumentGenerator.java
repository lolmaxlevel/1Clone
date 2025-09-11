//package com.lolmaxlevel.oneclone_backend.service;
//
//import lombok.extern.slf4j.Slf4j;
//import org.docx4j.XmlUtils;
//import org.docx4j.openpackaging.exceptions.Docx4JException;
//import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
//import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
//import org.springframework.stereotype.Service;
//
//import java.io.ByteArrayOutputStream;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.Map;
//
//@Service
//@Slf4j
//public class Docx4jDocumentGenerator {
//
//    public byte[] generateFromTemplate(String templatePath, Map<String, String> placeholders) {
//        try {
//            WordprocessingMLPackage wordPackage = WordprocessingMLPackage.load(new FileInputStream(templatePath));
//
//            // Заменяем плейсхолдеры в документе при помощи Docx4J
//            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
//                String placeholder = entry.getKey();
//                String value = entry.getValue();
//                wordPackage.getMainDocumentPart().variableReplace(Map.of(placeholder, value));
//            }
//
//            // Сохраняем в байты
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            wordPackage.save(outputStream);
//
//            log.info("Document generated successfully, size: {} bytes", outputStream.size());
//            return outputStream.toByteArray();
//
//        } catch (Docx4JException e) {
//            log.error("Docx4J error while generating document: {}", e.getMessage(), e);
//            return null;
//        } catch (IOException e) {
//            log.error("IO error while generating document: {}", e.getMessage(), e);
//            return null;
//        } catch (Exception e) {
//            log.error("Unexpected error while generating document: {}", e.getMessage(), e);
//            return null;
//        }
//    }
//}