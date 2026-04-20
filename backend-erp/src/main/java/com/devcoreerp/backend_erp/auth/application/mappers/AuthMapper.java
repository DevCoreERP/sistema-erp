package com.devcoreerp.backend_erp.auth.application.mappers;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.devcoreerp.backend_erp.auth.domain.User;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.CreateUserDto;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.LoginRequestDTO;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.UserResponseDTO;

public class AuthMapper {

    private AuthMapper() {
        throw new UnsupportedOperationException("This class should never be instantiated");
    }

    public static User fromDto(final CreateUserDto createUserDto) {
        return User.builder()
            .email(createUserDto.email())
            .firstName(createUserDto.firstName())
            .build();
    }

    public static Authentication fromDto(final LoginRequestDTO loginRequestDTO) {
        return new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.password());
    }

    public static UserResponseDTO toDto(final User user) {
        return new UserResponseDTO(user.getId(), user.getFirstName(), user.getEmail(), user.getRole());
    }
    
}
