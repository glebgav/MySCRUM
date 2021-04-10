package com.sadna.app.ws.MySCRUM.ui.model.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Request model for User (without teams and tasks)
 */
@Getter
@Setter
public class UserRequestModel {
    private String userId;
    private String firstName;
    private String LastName;

}
