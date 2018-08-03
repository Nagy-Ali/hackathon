package com.vispire.applications.volajj;

/**
 * Created by Abo3li on 8/1/2018.
 */

class ErrorBundle {
    private final int errorCode;
    private final String error;
    private final String message;

    public ErrorBundle(int errorCode, String error, String message) {
        this.errorCode = errorCode;
        this.error = error;
        this.message = message;
    }
}

public class FaceVerificationResult {

    public final boolean isIdentical;

    public final double confidence;

    public final ErrorBundle errorBundle;

    public FaceVerificationResult(boolean isIdentical, double confidence, ErrorBundle errorBundle) {
        this.isIdentical = isIdentical;
        this.confidence = confidence;
        this.errorBundle = errorBundle;
    }
}
