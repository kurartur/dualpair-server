package lt.dualpair.server.interfaces.web.controller.rest;

import lt.dualpair.server.service.match.MatchRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice("lt.dualpair.server.interfaces.web.controller.rest")
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, MatchRequestException.class})
    public ResponseEntity<ErrorResponse> illegalArgumentExceptionHandler(Exception e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.from(e, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> forbiddenExceptionHandler(ForbiddenException fe) {
        logger.error(fe.getMessage(), fe);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.from(fe, HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> illegalStateExceptionHandler(IllegalStateException ise) {
        logger.error(ise.getMessage(), ise);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.from(ise, HttpStatus.CONFLICT));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.from(e, HttpStatus.INTERNAL_SERVER_ERROR));
    }



}

