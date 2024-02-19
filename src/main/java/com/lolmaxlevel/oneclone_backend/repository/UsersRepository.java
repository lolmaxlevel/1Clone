package com.lolmaxlevel.oneclone_backend.repository;

import com.lolmaxlevel.oneclone_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);
}
