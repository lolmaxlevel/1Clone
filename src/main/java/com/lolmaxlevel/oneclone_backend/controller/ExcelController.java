package com.lolmaxlevel.oneclone_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolmaxlevel.oneclone_backend.model.Employee;
import com.lolmaxlevel.oneclone_backend.repository.EmployeeRepository;
import com.lolmaxlevel.oneclone_backend.types.CompanyType;
import com.lolmaxlevel.oneclone_backend.types.CountryType;
import com.lolmaxlevel.oneclone_backend.types.DocumentType;
import com.lolmaxlevel.oneclone_backend.types.WorkPositionType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("excel")
public class ExcelController {

    private static final Logger log = LoggerFactory.getLogger(ExcelController.class);

    private final EmployeeRepository employeeRepository;

    public ExcelController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

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
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    LocalDate date = cell.getLocalDateTimeCellValue().toLocalDate();
                    yield date.toString();
                } else {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> switch (cell.getCachedFormulaResultType()) {
                case STRING -> cell.getStringCellValue();
                case NUMERIC -> String.valueOf(cell.getNumericCellValue());
                case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                default -> "";
            };
            default -> "";
        };
    }

    @PostMapping("/upload-employees")
    public ResponseEntity<String> uploadEmployeesFromExcel(@RequestBody MultipartFile file) {
        log.info("Uploading employees from Excel file");
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Employee> employees = new ArrayList<>();
            Row headerRow = sheet.getRow(0);

            // Получение заголовков
            List<String> headers = new ArrayList<>();
            headerRow.forEach(cell -> headers.add(cell.getStringCellValue()));

            // Парсинг данных
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                Employee employee = new Employee();
                boolean hasMissingData = false;
                List<String> missingData = new ArrayList<>();

                for (int cellNum = 0; cellNum < headers.size(); cellNum++) {
                    if (cellNum < row.getLastCellNum()) {
                        Cell cell = row.getCell(cellNum);
                        String cellValue = getCellValueAsString(cell);
                        if (cellValue == null || cellValue.isEmpty()) {
                            if ("Отчество".equals(headers.get(cellNum)) || "ОМВД".equals(headers.get(cellNum))) {
                                continue; // Пропускаем установку отчества и ОМВД, если они пустые
                            }
                            hasMissingData = true;
                            missingData.add(headers.get(cellNum));
                            break;
                        }
                        switch (headers.get(cellNum)) {
                            case "Фамилия" -> employee.setSurname(cellValue);
                            case "Имя" -> employee.setName(cellValue);
                            case "Отчество" -> employee.setSecondName(cellValue);
                            case "Номер счета" -> employee.setBankAccount(cellValue);
                            case "ИНН" -> employee.setInn(cellValue);
                            case "Дата рождения" -> employee.setDateOfBirth(LocalDate.parse(cellValue));
                            case "вид документа" ->
                                    employee.setDocumentType(findEnumByNameIgnoreCase(DocumentType.class, cellValue));
                            case "серия" -> employee.setDocumentSeries(cellValue);
                            case "номер" -> employee.setDocumentNumber(cellValue);
                            case "Кем выдан" -> employee.setDocumentIssuedBy(cellValue);
                            case "Когда выдан" -> employee.setDocumentIssuedDate(LocalDate.parse(cellValue));
                            case "Гражданство" ->
                                    employee.setNationality(findEnumByOfficialNameIgnoreCase(CountryType.class, cellValue));
                            case "Место Рождения" ->
                                    employee.setBirthPlace(findEnumByOfficialNameIgnoreCase(CountryType.class, cellValue));
                            case "Объект" -> employee.setWorkObject(cellValue);
                            case "Адрес объекта" -> employee.setWorkAddress(cellValue);
                            case "Должность" ->
                                    employee.setWorkPosition(findEnumByRussianNameIgnoreCase(WorkPositionType.class, cellValue));
                            case "ОМВД" -> employee.setOmvd(cellValue);
                        }
                        employee.setCompanyType(CompanyType.AM);
                    } else {
                        hasMissingData = true;
                        missingData.add(headers.get(cellNum));
                        break;
                    }
                }

                if (hasMissingData) {
                    log.warn("Skipping row {} due to missing data: {}", rowNum, String.join(", ", missingData));
                    continue;
                }

                log.info("Parsed employee: {}", employee);
                employees.add(employee);
            }

            // Сохранение сотрудников в базу данных
            employeeRepository.saveAll(employees);
            return ResponseEntity.ok("Employees uploaded successfully");
        } catch (Exception e) {
            log.warn("Error uploading employees from Excel file", e);
            return ResponseEntity.badRequest().body("Error uploading employees");
        }
    }


    private <T extends Enum<T>> T findEnumByNameIgnoreCase(Class<T> enumClass, String value) {
        for (T enumConstant : enumClass.getEnumConstants()) {
            try {
                // Use the getter method to access the name field
                String name = (String) enumClass.getMethod("getName").invoke(enumConstant);
                if (name.equalsIgnoreCase(value)) {
                    return enumConstant;
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Error accessing name field of enum " + enumClass.getCanonicalName(), e);
            }
        }
        throw new IllegalArgumentException("No enum constant with name " + value + " in " + enumClass.getCanonicalName());
    }

    private <T extends Enum<T>> T findEnumByOfficialNameIgnoreCase(Class<T> enumClass, String value) {
        for (T enumConstant : enumClass.getEnumConstants()) {
            try {
                // Use the getter method to access the officialName field
                String officialName = (String) enumClass.getMethod("getOfficialName").invoke(enumConstant);
                if (officialName.equalsIgnoreCase(value)) {
                    return enumConstant;
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Error accessing officialName field of enum " + enumClass.getCanonicalName(), e);
            }
        }
        throw new IllegalArgumentException("No enum constant with officialName " + value + " in " + enumClass.getCanonicalName());
    }

    private <T extends Enum<T>> T findEnumByRussianNameIgnoreCase(Class<T> enumClass, String value) {
        for (T enumConstant : enumClass.getEnumConstants()) {
            try {
                // Use the getter method to access the russianName field
                String russianName = (String) enumClass.getMethod("getRussianName").invoke(enumConstant);
                if (russianName.equalsIgnoreCase(value)) {
                    return enumConstant;
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Error accessing russianName field of enum " + enumClass.getCanonicalName(), e);
            }
        }
        throw new IllegalArgumentException("No enum constant with russianName " + value + " in " + enumClass.getCanonicalName());
    }
}