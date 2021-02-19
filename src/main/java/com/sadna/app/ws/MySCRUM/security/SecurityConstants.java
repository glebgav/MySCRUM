package com.sadna.app.ws.MySCRUM.security;

import com.sadna.app.ws.MySCRUM.SpringApplicationContext;

public class SecurityConstants {
    public static final long EXPIRATION_TIME = 864000000;   //days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users";

    public static String getTokenSecret(){
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("appProperties");
        return appProperties.getTokenSecret();
    }

}
