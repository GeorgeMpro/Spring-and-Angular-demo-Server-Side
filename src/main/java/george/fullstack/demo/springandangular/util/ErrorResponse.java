package george.fullstack.demo.springandangular.util;

/**
 * A helper class for returning error message and url in the json path with the {@link george.fullstack.demo.springandangular.controller.GlobalControllerExceptionHandler} response entity.
 */
public class ErrorResponse {
    private String exception;
    private String url;

    public ErrorResponse() {
    }

    public ErrorResponse(String exception, String url) {
        this.exception = exception;
        this.url = url;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "exception='" + exception + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
