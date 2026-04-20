package com.devcoreerp.backend_erp.auth.infrastructure.persistance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devcoreerp.backend_erp.auth.domain.User;
import com.devcoreerp.backend_erp.auth.domain.UserRepository;

@Repository
public interface PostgresUserRepository extends JpaRepository<User, Long>, UserRepository {
}
