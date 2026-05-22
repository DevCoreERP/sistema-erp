package com.devcoreerp.backend_erp.auth.application.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.core.Authentication;

class PermissionAuthorizationTest {

    @Test
    void autenticadoConPermisoPuedeAutorizarse() {
        Authentication authentication = new TestingAuthenticationToken(
            "user",
            "password",
            "USUARIO_CREAR"
        );
        AuthorityAuthorizationManager<Object> manager = AuthorityAuthorizationManager.hasAuthority("USUARIO_CREAR");

        assertThat(manager.check(() -> authentication, new Object()).isGranted()).isTrue();
    }

    @Test
    void autenticadoSinPermisoNoPuedeAutorizarse() {
        Authentication authentication = new TestingAuthenticationToken(
            "user",
            "password",
            "USUARIO_LISTAR"
        );
        AuthorityAuthorizationManager<Object> manager = AuthorityAuthorizationManager.hasAuthority("USUARIO_CREAR");

        assertThat(manager.check(() -> authentication, new Object()).isGranted()).isFalse();
    }
}
