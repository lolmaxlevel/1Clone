package com.lolmaxlevel.oneclone_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("excel")
public class ExcelController {

    private static final Logger log = LoggerFactory.getLogger(ExcelController.class);

    @PostMapping("/parse")
    public ResponseEntity<String> parseExcelFileToJson(@RequestParam("file") MultipartFile file) {
        log.info("Parsing Excel file");
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Map<String, String>> rowsData = new ArrayList<>();
            Row headerRow = sheet.getRow(0);

            // Получение заголовков
            List<String> headers = new ArrayList<>();
            headerRow.forEach(cell -> headers.add(cell.getStringCellValue()));

            // Парсинг данных
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);

                Map<String, String> rowData = new HashMap<>();
                for (int cellNum = 0; cellNum < headers.size(); cellNum++) {
                    if (cellNum < row.getLastCellNum()) { // Check if the cell exists in this row
                        Cell cell = row.getCell(cellNum);
                        rowData.put(headers.get(cellNum), getCellValueAsString(cell));
                    } else {
                        // Handle missing cell case, e.g., by putting an empty string or a default value
                        rowData.put(headers.get(cellNum), "");
                    }
                }
                rowsData.add(rowData);
            }

            // Преобразование в JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(rowsData);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            log.warn("Error parsing Excel file", e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}