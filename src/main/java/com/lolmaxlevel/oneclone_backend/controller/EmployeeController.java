package com.lolmaxlevel.oneclone_backend.controller;

import com.lolmaxlevel.oneclone_backend.model.Document;
import com.lolmaxlevel.oneclone_backend.model.Employee;
import com.lolmaxlevel.oneclone_backend.repository.DocumentRepository;
import com.lolmaxlevel.oneclone_backend.repository.EmployeeRepository;
import com.lolmaxlevel.oneclone_backend.service.PoiTlDocumentGenerator;
import com.lolmaxlevel.oneclone_backend.specification.GenericSpecification;
import com.lolmaxlevel.oneclone_backend.types.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.lolmaxlevel.oneclone_backend.utils.PlaceHoldersExtractor.getPlaceholdersFromEmployee;
import static com.lolmaxlevel.oneclone_backend.utils.PlaceHoldersExtractor.getPlaceholdersFromDocument;

@Slf4j
@RestController
@RequestMapping("employee")
public class EmployeeController {
//#TODO добавить возможность генерации нового договора на человека, при этом старый договори все связаные с ним
//допики\приложения должны помечаться как архивные и новые допики\приложения должны создаваться с новым номером договора
    private final String TEMPLATE_DIRECTORY_ROOT = "src/main/resources/document_templates/";

    private final EmployeeRepository employeeRepository;
    private final PoiTlDocumentGenerator poiTlDocumentGenerator;
    private final DocumentRepository documentRepository;

    public EmployeeController(EmployeeRepository employeeRepository,
                              PoiTlDocumentGenerator poiTlDocumentGenerator,
                              DocumentRepository documentRepository) {
        this.employeeRepository = employeeRepository;
        this.poiTlDocumentGenerator = poiTlDocumentGenerator;
        this.documentRepository = documentRepository;
    }

    private Employee getEmployeeById(Long employeeId) {
        return employeeRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    private HttpHeaders generateHeaders(String name, String surname, ActType documentType, Long companySpecificId) {
        HttpHeaders headers = new HttpHeaders();
        String filename = "";
        switch (documentType) {
            case CONTRACT:
                break;
            case ADDITIONAL:
                filename += "Доп.соглашение к договору ";
                break;
            case APPENDIX:
                filename += "Приложение к договору ";
                break;
            default:
                throw new RuntimeException("Document type not found");
        }
        filename += "№" + companySpecificId + "-2025" + " " + surname + " " + name + ".docx";
        // encode filename to support cyrillic characters
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encodedFilename);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        return headers;
    }

    private Document getOrCreateDocument(Employee employee, ActType documentType, LocalDate dateFrom, LocalDate dateTo, Double price) {
        Document existingDocument;
        if (dateFrom != null && dateTo != null && price != null) {
            existingDocument = documentRepository.findByOwnerAndTypeAndDateFromAndDateToAndPrice(employee, documentType, dateFrom, dateTo, price);
            if (existingDocument == null) {
                Document document = new Document();
                document.setSeqNumber(documentRepository.countByOwnerAndTypeAndArchive(employee, documentType, false) + 1);
                document.setNumber(Math.toIntExact(employee.getCompanySpecificId()));
                document.setType(documentType);
                document.setPrice(price);
                document.setDateFrom(dateFrom);
                document.setDateTo(dateTo);
                document.setOwner(employee);
                documentRepository.save(document);
                existingDocument = document;

                // Если создается новый контракт, автоматически создаем дополнительное соглашение и приложение
                if (documentType == ActType.CONTRACT) {
                    createAdditionalDocuments(employee, dateFrom, dateTo, price);
                }
            }
        } else {
            if (documentType == ActType.APPENDIX) {
                existingDocument = documentRepository.findTopByOwnerAndTypeOrderByDateToDesc(employee, ActType.CONTRACT);
            } else {
                existingDocument = documentRepository.findTopByOwnerAndTypeOrderByDateToDesc(employee, documentType);
            }
            if (existingDocument == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        }
        return existingDocument;
    }

    private void createAdditionalDocuments(Employee employee, LocalDate dateFrom, LocalDate dateTo, Double price) {
        // Создаем дополнительное соглашение
        // Дата начала = дата начала договора + 2 дня
        LocalDate additionalDateFrom = dateFrom.plusDays(2);
        LocalDate additionalDateTo = dateFrom.plusYears(1).minusDays(ThreadLocalRandom.current().nextInt(1, 6)); // от 1 до 5 дней

        Document additionalAgreement = new Document();
        additionalAgreement.setSeqNumber(documentRepository.countByOwnerAndTypeAndArchive(employee, ActType.ADDITIONAL, false) + 1);
        additionalAgreement.setNumber(Math.toIntExact(employee.getCompanySpecificId()));
        additionalAgreement.setType(ActType.ADDITIONAL);
        additionalAgreement.setPrice(price);
        additionalAgreement.setDateFrom(additionalDateFrom);
        additionalAgreement.setDateTo(additionalDateTo);
        additionalAgreement.setOwner(employee);
        documentRepository.save(additionalAgreement);

        Document appendix = new Document();
        appendix.setSeqNumber(documentRepository.countByOwnerAndTypeAndArchive(employee, ActType.APPENDIX, false) + 1);
        appendix.setNumber(Math.toIntExact(employee.getCompanySpecificId()));
        appendix.setType(ActType.APPENDIX);
        appendix.setPrice(price);
        appendix.setDateFrom(dateFrom);
        appendix.setDateTo(dateTo);
        appendix.setOwner(employee);
        documentRepository.save(appendix);

        log.info("Created additional documents for employee {}: Additional agreement from {} to {}, Appendix from {} to {}",
                 employee.getId(), additionalDateFrom, additionalDateTo, dateFrom, dateTo);
    }

    private Map<String, String> fillPlaceholders(Employee employee, Document document, ActType documentType) {
        Map<String, String> placeholders = getPlaceholdersFromEmployee(employee);
        placeholders.putAll(getPlaceholdersFromDocument(document));
        return placeholders;
    }

    @GetMapping("/all")
    public Page<Employee> getAllEmployees(Pageable pageable, @RequestParam MultiValueMap<String, String> allParams) {
        log.info("Get all employees request{}", allParams);
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
    public Employee addEmployee(@RequestBody Employee employee) {
        log.info("Add employee request{}", employee);
        employee.setCompanySpecificId(generateCompanySpecificId(employee.getCompanyType()));
        return employeeRepository.save(employee);
    }

    @PostMapping("/add-random")
    public List<Employee> addRandomEmployees() {
        log.info("Add random employees request");
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
            employee.setCompanySpecificId(generateCompanySpecificId(employee.getCompanyType()));
            employeeRepository.save(employee);
        }
        return employeeRepository.findAll();
    }

    private Long generateCompanySpecificId(CompanyType companyType) {
        Long maxId = employeeRepository.findMaxCompanySpecificIdByCompanyType(companyType);
        return (maxId == null ? 0 : maxId) + 1;
    }

    @PostMapping("/delete")
    public String deleteEmployee(@RequestParam Long id) {
        log.info("Delete employee request {}", id);
        employeeRepository.deleteById(id);
        return "Deleted";
    }

    @GetMapping("/get-document")
    public ResponseEntity<byte[]> generateDocument(@RequestParam Long employeeId,
                                                   @RequestParam ActType documentType,
                                                   @RequestParam(required = false) LocalDate dateFrom,
                                                   @RequestParam(required = false) LocalDate dateTo,
                                                   @RequestParam(required = false) Double price) {
        log.info("Generate document request id: {} type: {}", employeeId, documentType);

        Employee employee = getEmployeeById(employeeId);
        Document document = getOrCreateDocument(employee, documentType, dateFrom, dateTo, price);
        Map<String, String> placeholders = fillPlaceholders(employee, document, documentType);

        String templatePath = TEMPLATE_DIRECTORY_ROOT + "templ_" + documentType + "_" + employee.getCompanyType() + ".docx";
        byte[] doc = poiTlDocumentGenerator.generateFromTemplate(templatePath, placeholders);
        try {
            HttpHeaders headers = generateHeaders(employee.getName(), employee.getSurname(), documentType, employee.getCompanySpecificId());
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(doc);
        } catch (Exception e) {
            log.error("Error while generating document", e);
            return null;
        }
    }

    @GetMapping("/get-exist-document")
    public ResponseEntity<byte[]> getExistDocument(@RequestParam Long documentId) {
        log.info("Get exist document request id: {}", documentId);
        Document document = documentRepository.findById(documentId).orElseThrow(() -> new RuntimeException("Document not found"));
        Employee employee = document.getOwner();
        Map<String, String> placeholders = fillPlaceholders(employee, document, document.getType());

        String templatePath = TEMPLATE_DIRECTORY_ROOT + "templ_" + document.getType() + "_" + employee.getCompanyType() + ".docx";
        byte[] doc = poiTlDocumentGenerator.generateFromTemplate(templatePath, placeholders);
        try {
            HttpHeaders headers = generateHeaders(employee.getName(), employee.getSurname(), document.getType(), employee.getCompanySpecificId());
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(doc);
        } catch (Exception e) {
            log.error("Error while generating document", e);
            return null;
        }
    }

    @GetMapping("/objects")
    public List<String> getWorkObjects() {
        log.info("Get work objects request");
        return employeeRepository.findDistinctWorkObject();
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