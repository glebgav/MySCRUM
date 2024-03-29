package com.sadna.app.ws.MySCRUM.ui.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * Response model for User
 */
@Getter
@Setter
public class UserRest {
    private String userId;
    private String firstName;
    private String LastName;
    private String email;
    private Boolean isManager;
    private List<TaskRest> tasks;
    private List<TeamFromUserRest> teams;
}
