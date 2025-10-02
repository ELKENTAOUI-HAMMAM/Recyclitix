package cours.iir4.smartrecyclebackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.net.SocketException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                "Invalid email or password",
                request.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SocketException.class)
    public ResponseEntity<?> handleSocketException(SocketException ex, WebRequest request) {
        String errorMessage = ex.getMessage();

        if (errorMessage != null && errorMessage.toLowerCase().contains("broken pipe")) {
            ErrorDetails errorDetails = new ErrorDetails(
                    new Date(),
                    "Connection interrupted. Please try again with a smaller image or better connection.",
                    request.getDescription(false));

            return new ResponseEntity<>(errorDetails, HttpStatus.SERVICE_UNAVAILABLE);
        }

        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                "Network connection issue: " + errorMessage,
                request.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIOException(IOException ex, WebRequest request) {
        String errorMessage = ex.getMessage();

        if (errorMessage != null &&
            (errorMessage.toLowerCase().contains("broken pipe") || 
             errorMessage.toLowerCase().contains("connection reset") ||
             errorMessage.toLowerCase().contains("timeout"))) {

            ErrorDetails errorDetails = new ErrorDetails(
                    new Date(),
                    "Connection interrupted. Please try again with a smaller image or better connection.",
                    request.getDescription(false));

            return new ResponseEntity<>(errorDetails, HttpStatus.SERVICE_UNAVAILABLE);
        }

        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                "I/O error: " + errorMessage,
                request.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        String errorMessage = ex.getMessage();

        if (errorMessage != null &&
            (errorMessage.contains("Image size exceeds") || 
             errorMessage.contains("Invalid image data") ||
             errorMessage.contains("Image data cannot be empty"))) {

            String additionalInfo = "";
            if (errorMessage.contains("Image size exceeds")) {
                additionalInfo = "\n\nTips to reduce image size:\n" +
                        "1. Reduce image resolution (e.g., 1024x768 or lower)\n" +
                        "2. Use image compression before sending\n" +
                        "3. Use JPEG format with quality setting of 70-80%\n" +
                        "4. Crop unnecessary parts of the image";
            }

            ErrorDetails errorDetails = new ErrorDetails(
                    new Date(),
                    errorMessage + additionalInfo,
                    request.getDescription(false));

            return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
        }

        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                "Invalid argument: " + errorMessage,
                request.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    private static class ErrorDetails {
        private Date timestamp;
        private String message;
        private String details;

        public ErrorDetails(Date timestamp, String message, String details) {
            this.timestamp = timestamp;
            this.message = message;
            this.details = details;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public String getMessage() {
            return message;
        }

        public String getDetails() {
            return details;
        }
    }
}
