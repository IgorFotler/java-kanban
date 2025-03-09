package exception;

public class ErrorResponse {
    private String errorMessage;
    private Integer errorCode;
    private String url;


    public ErrorResponse(String errorMessage, Integer errorCode, String url) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.url = url;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
