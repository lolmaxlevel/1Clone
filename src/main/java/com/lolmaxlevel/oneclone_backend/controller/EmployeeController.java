package com.lolmaxlevel.oneclone_backend.controller;

import com.lolmaxlevel.oneclone_backend.model.Employee;
import com.lolmaxlevel.oneclone_backend.repository.EmployeeRepository;
import com.lolmaxlevel.oneclone_backend.service.WordDocumentGenerator;
import com.lolmaxlevel.oneclone_backend.specification.GenericSpecification;
import com.lolmaxlevel.oneclone_backend.types.CompanyType;
import com.lolmaxlevel.oneclone_backend.types.DocumentType;
import com.lolmaxlevel.oneclone_backend.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.http.HttpHeaders;
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


    public EmployeeController(EmployeeRepository employeeRepository, WordDocumentGenerator wordDocumentGenerator) {
        this.employeeRepository = employeeRepository;
        this.wordDocumentGenerator = wordDocumentGenerator;
    }


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
            employee.setNationality("Nationality" + i);
            employee.setBirthPlace("BirthPlace" + i);
            employee.setWorkObject("WorkObject" + i);
            employee.setWorkAddress("WorkAddress" + i);
            employee.setWorkPosition("WorkPosition" + i);
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
    public ResponseEntity<byte[]> generateDocument(@RequestParam Long employeeId, @RequestParam String documentType) {
        log.info("Generate document request id: {} type: {}", employeeId, documentType);

        // Fetch the employee
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Employee not found"));

        // Create a map of placeholders and fill it with the employee's data
        Map<String, String> placeholders = getPlaceholders(employee);

        String templatePath = TEMPLATE_DIRECTORY_ROOT + "templ_" + documentType + "_" + employee.getCompanyType() + ".docx";
        // Generate the document
        try {
            byte[] document = wordDocumentGenerator.generateFromTemplate(templatePath, placeholders);
            // Return the document
            HttpHeaders headers = new HttpHeaders();
            String filename = employee.getSurname() + "_" + employee.getName() + ".docx";
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
}
