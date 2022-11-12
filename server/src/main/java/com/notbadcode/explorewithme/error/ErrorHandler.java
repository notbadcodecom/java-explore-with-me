package com.notbadcode.explorewithme.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ApiError handleBadRequestException(BadRequestException ex) {
        log.info("Bad request: {}", ex.getMessage());
        return ApiError.builder()
                .status(ErrorStatus.BAD_REQUEST)
                .message(ex.getMessage())
                .reason("Incorrect data was sent in the request")
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiError handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> errors =  ex.getBindingResult().getFieldErrors().stream()
                .peek(e -> log.info("Validation error: {}", e.getDefaultMessage()))
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        return ApiError.builder()
                .errors(errors)
                .status(ErrorStatus.BAD_REQUEST)
                .message("Validation error")
                .reason("Incorrect data was sent in the request")
                .build();
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public ApiError handleForbiddenException(ForbiddenException ex) {
        log.info("Forbidden: {}", ex.getMessage());
        return ApiError.builder()
                .status(ErrorStatus.FORBIDDEN)
                .message(ex.getMessage())
                .reason("Access to the object is prohibited or there are no rights")
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ApiError handleNoSuchElementFoundException(NotFoundException ex) {
        log.info("Not found: {}", ex.getMessage());
        return ApiError.builder()
                .status(ErrorStatus.NOT_FOUND)
                .message(ex.getMessage())
                .reason("Requested an item that is missing from the service")
                .build();
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiError handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.info("Method not allowed: {}", ex.getMessage());
        return ApiError.builder()
                .status(ErrorStatus.METHOD_NOT_ALLOWED)
                .message(ex.getMessage().toLowerCase())
                .reason("The method used in the request is not allowed by this endpoint")
                .build();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ApiError handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.info("Data integrity violation: {}", ex.getMessage());
        return ApiError.builder()
                .status(ErrorStatus.CONFLICT)
                .message(Objects.requireNonNull(ex.getMessage()).split(";")[0])
                .reason("Database exception, invalid values sent")
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ApiError handleServerErrorException(RuntimeException ex) {
        log.info("Server error: {}, {}", ex.getClass(), ex.getMessage());
        ex.printStackTrace();
        return ApiError.builder()
                .status(ErrorStatus.CONFLICT)
                .message(ex.getMessage())
                .reason("Internal server error")
                .build();
    }
}
