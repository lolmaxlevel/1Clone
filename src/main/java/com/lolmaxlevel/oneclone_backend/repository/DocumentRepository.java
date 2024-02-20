package com.lolmaxlevel.oneclone_backend.repository;

import com.lolmaxlevel.oneclone_backend.model.Document;
import com.lolmaxlevel.oneclone_backend.model.Employee;
import com.lolmaxlevel.oneclone_backend.types.ActType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Integer> {
    Document findTopByOwnerAndTypeOrderByDateToDesc(Employee employee, ActType actType);
}