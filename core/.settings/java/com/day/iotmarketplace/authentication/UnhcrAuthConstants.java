package com.day.iotmarketplace.authentication;

public final class UnhcrAuthConstants {

    public static final String HANDLER_NAME = "UNHCR Authentication Handler";
    public static final String AUTH_TYPE = "UNHCR";
    public static final String LOGIN_REQUEST_METHOD = "POST";
    public static final String COOKIE_NAME = "unhcr.auth";
    public static final String URLSUFFIX_LOGINFORM = ".html";
    public static final String URLSUFFIX_LOGINACTION = ".login.html";
    public static final String URLSUFFIX_FORGOTFORM = ".forgot.html";
    public static final String URLSUFFIX_TERMSFORM = ".term.html";
    public static final String URLSUFFIX_TERMSACTION = ".accept.html";
    public static final String URLSUFFIX_LOGOUT = ".logout.html";
    public static final String TERMS_IAGREE = "iagree";
    public static final String CAPTCHA = "captcha";
    public static final String CAPTCHA_KEY = "captchaKey";
    public static final String REASON_PARAMETER_NAME = "reason";
    public static final String PASSWORD_TRIES_LEFT = "passwordTriesLeft";

    /**
     * Private constructor to prevent instantiation
     */
    private UnhcrAuthConstants() {
    }
}
