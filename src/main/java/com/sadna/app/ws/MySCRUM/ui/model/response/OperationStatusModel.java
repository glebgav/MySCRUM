package com.sadna.app.ws.MySCRUM.ui.model.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Response model for Operation status message
 */
@Getter
@Setter
public class OperationStatusModel {
    private String operationName;
    private String operationResult;
}
