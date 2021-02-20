package com.sadna.app.ws.MySCRUM.ui.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TeamDetailsRequestModel {
    private String name;
    private List<TaskRequestModel> tasks;
    private List<UserDetailsRequestModel> users;
}
