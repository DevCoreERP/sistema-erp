package com.devcoreerp.backend_erp.auth.domain;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    
    Optional<User> findByEmail(String email);
    User save(User user);
    Optional<User> findById(UUID id);
    
}
