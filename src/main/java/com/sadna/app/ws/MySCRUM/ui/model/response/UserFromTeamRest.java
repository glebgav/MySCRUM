package com.sadna.app.ws.MySCRUM.ui.model.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Response model for User in Team
 */
@Getter
@Setter
public class UserFromTeamRest {
    private String userId;
    private String firstName;
    private String LastName;
    private String email;
    private Boolean isManager;
}
