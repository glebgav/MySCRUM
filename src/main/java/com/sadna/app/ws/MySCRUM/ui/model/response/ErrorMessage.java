package com.sadna.app.ws.MySCRUM.ui.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

/**
 * Response model for error message
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessage {
    private Date timeStamp;
    private String message;
}
