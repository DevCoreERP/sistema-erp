package com.devcoreerp.backend_erp.auth.domain.services;

import com.devcoreerp.backend_erp.auth.domain.User;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.CreateUserDto;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.LoginRequestDTO;

public interface AuthService {
    String login(LoginRequestDTO loginRequestDTO);
    boolean validateToken(String token);
    String getUserFromToken(String token);
    void createUser(CreateUserDto createUserDto);
    User getUser(Long id);
}
