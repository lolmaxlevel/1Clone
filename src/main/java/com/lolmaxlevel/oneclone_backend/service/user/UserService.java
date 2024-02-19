package com.lolmaxlevel.oneclone_backend.service.user;


import com.lolmaxlevel.oneclone_backend.model.User;
import lombok.NonNull;

import java.util.Optional;

public interface UserService {
    Optional<User> getUser(@NonNull String username);

    boolean addUser(@NonNull User user);
}
