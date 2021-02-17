package com.sadna.app.ws.MySCRUM.shared.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
public class TaskDto implements Serializable {
    private long id;
    private String taskId;
    private String title;
    private int status;
    private String description;

}
