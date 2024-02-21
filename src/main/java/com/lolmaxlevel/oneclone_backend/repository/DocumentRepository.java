package com.lolmaxlevel.oneclone_backend.repository;

import com.lolmaxlevel.oneclone_backend.model.Document;
import com.lolmaxlevel.oneclone_backend.model.Employee;
import com.lolmaxlevel.oneclone_backend.types.ActType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Document findTopByOwnerAndTypeOrderByDateToDesc(Employee employee, ActType actType);

    Document findByOwnerAndTypeAndDateFromAndDateToAndPrice(Employee employee, ActType documentType, LocalDate dateFrom, LocalDate dateTo, Double price);
}