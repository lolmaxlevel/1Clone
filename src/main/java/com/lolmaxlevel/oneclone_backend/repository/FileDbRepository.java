package com.lolmaxlevel.oneclone_backend.repository;

import com.lolmaxlevel.oneclone_backend.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileDbRepository extends JpaRepository<File, Long> {}
