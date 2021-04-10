package com.sadna.app.ws.MySCRUM.ui.model.request;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * Request model for User details
 */
@Getter
@Setter
public class UserDetailsRequestModel {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Boolean isManager;
    private List<TaskRequestModel> tasks;
    private List<TeamDetailsRequestModel> teams;


}