package com.lolmaxlevel.oneclone_backend.service;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.config.ConfigureBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class PoiTlDocumentGenerator {

    public byte[] generateFromTemplate(String templatePath, Map<String, String> placeholders) {
        try {
            log.info("Loading template from: {}", templatePath);
            log.info("Placeholders to replace: {}", placeholders);

            // Загружаем шаблон
            XWPFTemplate template = XWPFTemplate.compile(new FileInputStream(templatePath));

            // Рендерим документ с данными
            XWPFTemplate renderedTemplate = template.render(placeholders);

            // Сохраняем в байты
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            renderedTemplate.write(outputStream);
            renderedTemplate.close();
            template.close();

            log.info("Document generated successfully, size: {} bytes", outputStream.size());
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("IO error while generating document: {}", e.getMessage(), e);
            return null;
        } catch (Exception e) {
            log.error("Unexpected error while generating document: {}", e.getMessage(), e);
            return null;
        }
    }
}
