package com.tech.gameService.common.exception;

import com.tech.gameService.common.entities.HttpResponse;
import com.tech.gameService.common.exception.category.CategoryAlreadyExistException;
import com.tech.gameService.common.exception.category.CategoryNotFoundException;
import com.tech.gameService.common.exception.game.GameExistException;
import com.tech.gameService.common.exception.game.GameNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ExceptionHandling implements ErrorController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String METHOD_IS_NOT_ALLOWED = "This request method is not allowed on this endpoint. Please send a '%s' request";
    private static final String INTERNAL_SERVER_ERROR_MSG = "An error occurred while processing the request";
    public static final String ERROR_PATH = "/error";

    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(HttpResponse.builder()
                .timeStamp(new Date())
                .httpStatusCode(httpStatus.value())
                .httpStatus(httpStatus)
                .reason(httpStatus.getReasonPhrase().toUpperCase())
                .message(message)
                .build(), httpStatus);
    }

    @ExceptionHandler(GameExistException.class)
    public ResponseEntity<HttpResponse> gameExistException(GameExistException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<HttpResponse> gameNotFoundException(GameNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(CategoryAlreadyExistException.class)
    public ResponseEntity<HttpResponse> categoryAlreadyExistException(CategoryAlreadyExistException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<HttpResponse> userNotFoundException(CategoryNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @RequestMapping(ERROR_PATH)
    public ResponseEntity<HttpResponse> notFound404() {
        return createHttpResponse(NOT_FOUND, "There is no mapping for this URL");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
        return createHttpResponse(METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> internalServerErrorException(Exception exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
    }

    public String getErrorPath() {
        return ERROR_PATH;
    }
}