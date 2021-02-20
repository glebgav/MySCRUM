package com.sadna.app.ws.MySCRUM.ui.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDetailsRequestModel {
    private String firstName;
    private String LastName;
    private String email;
    private String password;
    private Boolean isManager;
    private List<TaskRequestModel> tasks;

}
