package com.drbrosdev.klinkrest.activity.exception.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.print.attribute.standard.Severity;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Schema(
        description = "Default error message template"
)
public class Error implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonProperty("id")
    private String id;
    @JsonProperty("code")
    private String code;
    @JsonProperty("timestamp")
    @DateTimeFormat(
            iso = ISO.DATE_TIME
    )
    private OffsetDateTime timestamp;
    @JsonProperty("message")
    private String message;
    @JsonProperty("retryable")
    private Boolean retryable;
    @JsonProperty("severity")
    private Severity severity;
    @JsonProperty("details")
    private List<Serializable> details = null;
    @JsonProperty("requestReference")
    private String requestReference;

    public Error() {
        // empty constructor
    }

    public Error id(String id) {
        this.id = id;
        return this;
    }

    @Schema(
            required = true,
            title = "Unique error ID, used for logging purposes, UUID format"
    )
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Error code(String code) {
        this.code = code;
        return this;
    }

    @Schema(
            required = true,
            title = "An string coding the error type. This is given to caller so he can translate them if required."
    )
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Error timestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    @Schema(
            required = true,
            title = "Exact time of error"
    )
    public OffsetDateTime getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Error message(String message) {
        this.message = message;
        return this;
    }

    @Schema(title = "A short localized string that describes the error.")
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Error retryable(Boolean retryable) {
        this.retryable = retryable;
        return this;
    }

    @Schema(title = "A boolean that provides info is request retryable")
    public Boolean getRetryable() {
        return this.retryable;
    }

    public void setRetryable(Boolean retryable) {
        this.retryable = retryable;
    }

    public Error severity(Severity severity) {
        this.severity = severity;
        return this;
    }

    @Schema
    public Severity getSeverity() {
        return this.severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public Error details(List<Serializable> details) {
        this.details = details;
        return this;
    }

    public Error addDetailsItem(Serializable detailsItem) {
        if (this.details == null) {
            this.details = new ArrayList<>();
        }

        this.details.add(detailsItem);
        return this;
    }

    @Schema(title = "Exception detailed info")
    public List<Serializable> getDetails() {
        return this.details;
    }

    public void setDetails(List<Serializable> details) {
        this.details = details;
    }

    public Error requestReference(String requestReference) {
        this.requestReference = requestReference;
        return this;
    }

    @Schema(title = "Used for bulk request to reference to a sub item. This is only used in a error list")
    public String getRequestReference() {
        return this.requestReference;
    }

    public void setRequestReference(String requestReference) {
        this.requestReference = requestReference;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Error error = (Error) o;
            return Objects.equals(this.id, error.id) && Objects.equals(this.code, error.code) && Objects.equals(this.timestamp, error.timestamp) && Objects.equals(this.message, error.message) && Objects.equals(this.retryable, error.retryable) && Objects.equals(this.severity, error.severity) && Objects.equals(this.details, error.details) && Objects.equals(this.requestReference, error.requestReference);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(this.id, this.code, this.timestamp, this.message, this.retryable, this.severity, this.details, this.requestReference);
    }

    public String toString() {
        return "class Error {\n" +
                "    id: " + this.toIndentedString(this.id) + "\n" +
                "    code: " + this.toIndentedString(this.code) + "\n" +
                "    timestamp: " + this.toIndentedString(this.timestamp) + "\n" +
                "    message: " + this.toIndentedString(this.message) + "\n" +
                "    retryable: " + this.toIndentedString(this.retryable) + "\n" +
                "    severity: " + this.toIndentedString(this.severity) + "\n" +
                "    details: " + this.toIndentedString(this.details) + "\n" +
                "    requestReference: " + this.toIndentedString(this.requestReference) + "\n" +
                "}";
    }

    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }
}
