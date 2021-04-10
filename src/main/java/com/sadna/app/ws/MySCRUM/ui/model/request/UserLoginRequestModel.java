package com.sadna.app.ws.MySCRUM.ui.model.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Request model for User login details
 */
@Getter
@Setter
public class UserLoginRequestModel {
    private String email;
    private String password;
}
