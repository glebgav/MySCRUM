package com.sadna.app.ws.MySCRUM.ui.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskRequestModel {
    private String title;
    private int status;
    private String description;
}
