package com.lolmaxlevel.oneclone_backend.controller;

import com.lolmaxlevel.oneclone_backend.model.Document;
import com.lolmaxlevel.oneclone_backend.model.Employee;
import com.lolmaxlevel.oneclone_backend.repository.DocumentRepository;
import com.lolmaxlevel.oneclone_backend.repository.EmployeeRepository;
import com.lolmaxlevel.oneclone_backend.service.WordDocumentGenerator;
import com.lolmaxlevel.oneclone_backend.specification.GenericSpecification;
import com.lolmaxlevel.oneclone_backend.types.*;
import com.lolmaxlevel.oneclone_backend.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.lolmaxlevel.oneclone_backend.utils.PlaceHoldersExtractor.getPlaceholders;


@Slf4j // lombok annotation for logging
@RestController
@RequestMapping("/api/employee")
@CrossOrigin(origins = "http://localhost:5173")
public class EmployeeController {

    private final String TEMPLATE_DIRECTORY_ROOT = "src/main/resources/document_templates/";


    private final EmployeeRepository employeeRepository;
    private final WordDocumentGenerator wordDocumentGenerator;
    private final DocumentRepository documentRepository;


    public EmployeeController(EmployeeRepository employeeRepository,
                              WordDocumentGenerator wordDocumentGenerator,
                              DocumentRepository documentRepository) {
        this.employeeRepository = employeeRepository;
        this.wordDocumentGenerator = wordDocumentGenerator;
        this.documentRepository = documentRepository;
    }


    // Get all employees with pagination and filtering
    // example: http://localhost:5173/api/employee/all?page=0&size=10&sort=id,desc&surname=Ivanov&name=Ivan&workPosition=maid
    @GetMapping("/all")
    public Page<Employee> getAllEmployees(Pageable pageable, @RequestParam MultiValueMap<String, String> allParams) {
        log.info("Get all employees request" + allParams);
        Specification<Employee> spec = Specification.where(null);

        for (Map.Entry<String, List<String>> entry : allParams.entrySet()) {
            if (!entry.getKey().equals("page") && !entry.getKey().equals("size") && !entry.getKey().equals("sort")) {
                Specification<Employee> specForKey;
                specForKey = new GenericSpecification<>(entry.getKey(), entry.getValue());
                spec = spec.and(specForKey);
            }
        }

        return employeeRepository.findAll(spec, pageable);
    }

    @PostMapping("/add")
    public Employee addEmployee(Employee employee) {
        log.info("Add employee request" + employee);
        return employeeRepository.save(employee);
    }

    @PostMapping("/add-random")
    public List<Employee> addRandomEmployees() {
        log.info("Add random employees request");
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Employee employee = new Employee();
            employee.setSurname("Surname" + i);
            employee.setName("Name" + i);
            employee.setSecondName("SecondName" + i);
            employee.setBankAccount("BankAccount" + i);
            employee.setInn("Inn" + i);
            employee.setDateOfBirth(LocalDate.of(ThreadLocalRandom.current().nextInt(1950, 2003), ThreadLocalRandom.current().nextInt(1, 13), ThreadLocalRandom.current().nextInt(1, 28)));
            employee.setDocumentType(DocumentType.values()[ThreadLocalRandom.current().nextInt(DocumentType.values().length)]);
            employee.setDocumentSeries("Series" + i);
            employee.setDocumentNumber("Number" + i);
            employee.setDocumentIssuedBy("IssuedBy" + i);
            employee.setDocumentIssuedDate(LocalDate.of(ThreadLocalRandom.current().nextInt(2000, 2023), ThreadLocalRandom.current().nextInt(1, 13), ThreadLocalRandom.current().nextInt(1, 28)));
            employee.setNationality(CountryType.values()[ThreadLocalRandom.current().nextInt(CountryType.values().length)]);
            employee.setBirthPlace(CountryType.values()[ThreadLocalRandom.current().nextInt(CountryType.values().length)]);
            employee.setWorkObject("WorkObject" + i);
            employee.setWorkAddress("WorkAddress" + i);
            employee.setWorkPosition(WorkPositionType.values()[ThreadLocalRandom.current().nextInt(WorkPositionType.values().length)]);
            employee.setOmvd("Omvd" + i);
            employee.setCompanyType(CompanyType.values()[ThreadLocalRandom.current().nextInt(CompanyType.values().length)]);
            employees.add(employee);
        }
        return employeeRepository.saveAll(employees);
    }

    @PostMapping("/delete")
    public String deleteEmployee(@RequestParam Long id) {
        log.info("Delete employee request " + id);
        employeeRepository.deleteById(id);
        return "Deleted";
    }

    @GetMapping("/get-document")
    public ResponseEntity<byte[]> generateDocument(@RequestParam Long employeeId,
                                                   @RequestParam ActType documentType,
                                                   @RequestParam(required = false) LocalDate dateFrom,
                                                   @RequestParam(required = false) LocalDate dateTo,
                                                   @RequestParam(required = false) Double price){
        log.info("Generate document request id: {} type: {}", employeeId, documentType);

        // Fetch the employee
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Employee not found"));
        Map<String, String> placeholders = getPlaceholders(employee);

        Document existingDocument;
        // If dates and price are provided, check if a document with these parameters already exists
        if (dateFrom != null && dateTo != null && price != null) {
            existingDocument = documentRepository.findByOwnerAndTypeAndDateFromAndDateToAndPrice(employee, documentType, dateFrom, dateTo, price);
            if (existingDocument == null) {
                // If the document does not exist, create a new one
                Document document = new Document();
                document.setName(employeeId + "_" + documentType );
                document.setType(documentType);
                document.setPrice(price);
                document.setDateFrom(dateFrom);
                document.setDateTo(dateTo);
                document.setOwner(employee);
                documentRepository.save(document);
                existingDocument = document;
            }
        } else {
            // If any of the non-required parameters are empty, return the latest document of the specified type and employee
            existingDocument = documentRepository.findTopByOwnerAndTypeOrderByDateToDesc(employee, documentType);
            if (existingDocument == null) {
                // If no such document exists, return an error
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }

        // If the document exists, use its data to generate the document
        placeholders.put("{{ DATE_START_FULL }}", existingDocument.getDateFrom().toString());
        placeholders.put("{{ DATE_END }}", existingDocument.getDateTo().toString());
        placeholders.put("{{ COST }}", existingDocument.getPrice().toString());

        String templatePath = TEMPLATE_DIRECTORY_ROOT + "templ_" + documentType + "_" + employee.getCompanyType() + ".docx";
        // Generate the document
        byte[] document = wordDocumentGenerator.generateFromTemplate(templatePath, placeholders);
        try {
            // Return the document
            HttpHeaders headers = new HttpHeaders();
            String filename = employee.getSurname() + "_" + employee.getName() + "_" + documentType + ".docx";
            filename = StringUtils.convertCyrilic(filename.toLowerCase());
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            //magic header for docx files
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(document);
        } catch (Exception e) {
            log.error("Error while generating document", e);
            return null;
        }
    }
    @GetMapping("/get-exist-document")
    public ResponseEntity<byte[]> getExistDocument(@RequestParam Long documentId){
        log.info("Get exist document request id: {}", documentId);
        Document document = documentRepository.findById(documentId).orElseThrow(() -> new RuntimeException("Document not found"));
        Employee employee = document.getOwner();
        Map<String, String> placeholders = getPlaceholders(employee);
        placeholders.put("{{ DATE_START_FULL }}", document.getDateFrom().toString());
        placeholders.put("{{ DATE_END }}", document.getDateTo().toString());
        placeholders.put("{{ COST }}", document.getPrice().toString());
        String templatePath = TEMPLATE_DIRECTORY_ROOT + "templ_" + document.getType() + "_" + employee.getCompanyType() + ".docx";
        byte[] doc = wordDocumentGenerator.generateFromTemplate(templatePath, placeholders);
        try {
            HttpHeaders headers = new HttpHeaders();
            String filename = employee.getSurname() + "_" + employee.getName() + "_" + document.getType() + ".docx";
            filename = StringUtils.convertCyrilic(filename.toLowerCase());
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(doc);
        } catch (Exception e) {
            log.error("Error while generating document", e);
            return null;
        }
    }




    @PatchMapping("/update")
    public Employee updateEmployee(@RequestParam String id, @RequestParam String field, @RequestParam String value) {
        log.info("Update employee request id: {} field: {} value: {}", id, field, value);
        Employee employee = employeeRepository.findById(Long.valueOf(id)).orElseThrow(() -> new RuntimeException("Employee not found"));
        switch (field) {
            case "surname":
                employee.setSurname(value);
                break;
            case "name":
                employee.setName(value);
                break;
            case "secondName":
                employee.setSecondName(value);
                break;
            case "bankAccount":
                employee.setBankAccount(value);
                break;
            case "inn":
                employee.setInn(value);
                break;
            case "dateOfBirth":
                employee.setDateOfBirth(LocalDate.parse(value));
                break;
            case "documentType":
                employee.setDocumentType(DocumentType.valueOf(value));
                break;
            case "documentSeries":
                employee.setDocumentSeries(value);
                break;
            case "documentNumber":
                employee.setDocumentNumber(value);
                break;
            case "documentIssuedBy":
                employee.setDocumentIssuedBy(value);
                break;
            case "documentIssuedDate":
                employee.setDocumentIssuedDate(LocalDate.parse(value));
                break;
            case "nationality":
                employee.setNationality(CountryType.valueOf(value));
                break;
            case "birthPlace":
                employee.setBirthPlace(CountryType.valueOf(value));
                break;
            case "workObject":
                employee.setWorkObject(value);
                break;
            case "workAddress":
                employee.setWorkAddress(value);
                break;
            case "workPosition":
                employee.setWorkPosition(WorkPositionType.valueOf(value));
                break;
            case "omvd":
                employee.setOmvd(value);
                break;
            case "companyType":
                employee.setCompanyType(CompanyType.valueOf(value));
                break;
            default:
                throw new RuntimeException("Field not found");
        }
        return employeeRepository.save(employee);
    }
}
