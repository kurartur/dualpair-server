package lt.dualpair.server.interfaces.web.controller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice("lt.dualpair.server.interfaces.web.controller.rest")
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accessDeniedExceptionHandler(AccessDeniedException ade) {
        logger.error(ade.getMessage(), ade);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.from(ade, HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> illegalStateExceptionHandler(IllegalStateException ise) {
        logger.error(ise.getMessage(), ise);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.from(ise, HttpStatus.CONFLICT));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> illegalStateExceptionHandler(IllegalArgumentException iae) {
        logger.error(iae.getMessage(), iae);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.from(iae, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.from(e, HttpStatus.INTERNAL_SERVER_ERROR));
    }



}

