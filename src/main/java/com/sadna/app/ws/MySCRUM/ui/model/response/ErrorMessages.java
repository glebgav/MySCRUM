package com.sadna.app.ws.MySCRUM.ui.model.response;


public enum ErrorMessages {

    MISSING_REQUIRED_FIELD("Missing required field. Please check documentation for required fields"),
    RECORD_ALREADY_EXISTS("Record already exists"),
    USER_ALREADY_EXISTS("Email is already register to a user"),
    INTERNAL_SERVER_ERROR("Internal server error"),
    NO_RECORD_FOUND("Record with provided id is not found"),
    AUTHENTICATION_FAILED("Authentication failed"),
    COULD_NOT_UPDATE_RECORD("Could not update record"),
    COULD_NOT_RETRIEVE_RECORD("Could not retrieve record"),
    COULD_NOT_DELETE_RECORD("Could not delete record"),
    USER_IS_NOT_IN_TEAM("Error - user is not in the team"),
    TASK_IS_ASSIGNED_TO_WRONG_TEAM("Error - task is assigned to a team that the user doesn't have"),
    TASK_IS_ASSIGNED_TO_WRONG_USER("Error - task is assigned to a user that is not in the team");


    private String errorMessage;

    ErrorMessages(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
