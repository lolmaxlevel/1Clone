package com.lolmaxlevel.oneclone_backend.repository;

import com.lolmaxlevel.oneclone_backend.model.Employee;
import com.lolmaxlevel.oneclone_backend.types.CompanyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    @Query("SELECT MAX(e.companySpecificId) FROM Employee e WHERE e.companyType = :companyType")
    Long findMaxCompanySpecificIdByCompanyType(@Param("companyType") CompanyType companyType);
}

