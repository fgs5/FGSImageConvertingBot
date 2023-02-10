public class WritingException extends Exception {
    private final String message;

    public WritingException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
