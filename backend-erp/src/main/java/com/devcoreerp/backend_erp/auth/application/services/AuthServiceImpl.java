package com.devcoreerp.backend_erp.auth.application.services;

// Spring
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

// Logging (Log4j)
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.devcoreerp.backend_erp.auth.domain.User;
import com.devcoreerp.backend_erp.auth.domain.UserRepository;
// Tus clases (ajusta paquetes si cambian)
import com.devcoreerp.backend_erp.auth.domain.services.AuthService;
import com.devcoreerp.backend_erp.auth.domain.services.TokenService;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.CreateUserDto;
import com.devcoreerp.backend_erp.auth.infrastructure.dtos.LoginRequestDTO;
import com.devcoreerp.backend_erp.auth.application.mappers.AuthMapper;

@Service
public class AuthServiceImpl implements AuthService, UserDetailsService {
    private static final Logger logger = LogManager.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationConfiguration authenticationConfiguration;

    public AuthServiceImpl(
            UserRepository userRepository,
            TokenService tokenService,
            PasswordEncoder passwordEncoder,
            AuthenticationConfiguration authenticationConfiguration) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Override
    public void createUser(final CreateUserDto createUserDto) {
        final User createUser = AuthMapper.fromDto(createUserDto);
        createUser.setPassword(passwordEncoder.encode(createUserDto.password()));
        final User user = userRepository.save(createUser);
        logger.info("[USER] : User successfully created with id {}", user.getId());
    }

    @Override
    public User getUser(final Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with id: " + id));
    }

    @Override
    public String login(final LoginRequestDTO loginRequest) {
        try {
            final AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
            final Authentication authRequest = AuthMapper.fromDto(loginRequest);
            final Authentication authentication = authenticationManager.authenticate(authRequest);
            return tokenService.generateToken(authentication);

        } catch (Exception e) {
            logger.error("[USER] : Error while trying to login", e);
            throw new ProviderNotFoundException("Error while trying to login");
        }
    }

    @Override
    public boolean validateToken(final String token) {
        return tokenService.validateToken(token);
    }

    @Override
    public String getUserFromToken(final String token) {
        return tokenService.getUserFromToken(token);
    }

    @Override
    public UserDetails loadUserByUsername(final String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    logger.error("[USER] : User not found with email {}", username);
                    return new UsernameNotFoundException("User not found");
                });
    }
}