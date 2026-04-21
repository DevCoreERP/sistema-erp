package com.devcoreerp.backend_erp.auth.application.AuthCookieConstants;

public class AuthConstants {
    private AuthConstants() {
      throw new UnsupportedOperationException("This class should never be instantiated");
    }
    public static final String TOKEN_COOKIE_NAME = "auth-token";
    public static final boolean HTTP_ONLY = true;
    public static final boolean COOKIE_SECURE = true;
    public static final int COOKIE_MAX_AGE = 60 * 12; // 12 min
    public static final String SAME_SITE = "Strict";
}
