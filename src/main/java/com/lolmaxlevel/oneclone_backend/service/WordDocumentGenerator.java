package com.lolmaxlevel.oneclone_backend.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WordDocumentGenerator {

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
        if (runs.isEmpty()) {
            return;
        }

        // Создаем список сегментов с информацией о форматировании
        List<TextSegment> segments = new ArrayList<>();

        for (XWPFRun run : runs) {
            String text = run.getText(0);
            if (text != null && !text.isEmpty()) {
                segments.add(new TextSegment(text, run.isBold(), run.isItalic(),
                        run.getUnderline(), run.getFontSize(), run.getFontFamily()));
            }
        }

        // Собираем весь текст
        StringBuilder fullText = new StringBuilder();
        for (TextSegment segment : segments) {
            fullText.append(segment.text);
        }

        String originalText = fullText.toString();
        String processedText = originalText;

        // Заменяем плейсхолдеры
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String placeholder = entry.getKey();
            String value = entry.getValue();
            if (value != null && processedText.contains(placeholder)) {
                processedText = processedText.replace(placeholder, value);
            }
        }

        // Если текст изменился, обновляем runs с сохранением основного форматирования
        if (!originalText.equals(processedText)) {
            // Очищаем все runs
            for (XWPFRun run : runs) {
                run.setText("", 0);
            }

            // Находим run с наименьшим форматированием для размещения текста
            XWPFRun bestRun = runs.get(0);
            for (XWPFRun run : runs) {
                if (!run.isBold() && !run.isItalic() && run.getUnderline() == UnderlinePatterns.NONE) {
                    bestRun = run;
                    break;
                }
            }

            // Устанавливаем обработанный текст в лучший run
            bestRun.setText(processedText, 0);
        }
    }

    // Вспомогательный класс для хранения информации о сегменте текста
    private static class TextSegment {
        final String text;
        final boolean bold;
        final boolean italic;
        final UnderlinePatterns underline;
        final int fontSize;
        final String fontFamily;

        TextSegment(String text, boolean bold, boolean italic, UnderlinePatterns underline,
                    int fontSize, String fontFamily) {
            this.text = text;
            this.bold = bold;
            this.italic = italic;
            this.underline = underline;
            this.fontSize = fontSize;
            this.fontFamily = fontFamily;
        }
    }
}