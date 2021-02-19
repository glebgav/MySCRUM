package com.sadna.app.ws.MySCRUM.service;

import com.sadna.app.ws.MySCRUM.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto user);
}
