package com.cybex.gma.client.event;

/**
 *
 */
public class ValidateResultEvent {

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    private boolean isSuccess;

    private int fail_type;

    public int getFail_type() {
        return fail_type;
    }

    public void setFail_type(int fail_type) {
        this.fail_type = fail_type;
    }
}
