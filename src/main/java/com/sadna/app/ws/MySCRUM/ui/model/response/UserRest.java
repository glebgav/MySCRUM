package com.sadna.app.ws.MySCRUM.ui.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRest {
    private String userId;
    private String firstName;
    private String LastName;
    private String email;
    private Boolean isManager;
}
