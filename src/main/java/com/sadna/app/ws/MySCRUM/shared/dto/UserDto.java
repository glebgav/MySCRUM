package com.sadna.app.ws.MySCRUM.shared.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class UserDto implements Serializable {
    private long id;
    private String userId;
    private String firstName;
    private String LastName;
    private String email;
    private String password;
    private String encryptedPassword;
    private Boolean isManager;
    private List<TaskDto> tasks;



}
