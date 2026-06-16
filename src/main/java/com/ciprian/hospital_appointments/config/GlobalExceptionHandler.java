package com.ciprian.hospital_appointments.config;

import com.ciprian.hospital_appointments.config.exceptions.BadRequestException;
import com.ciprian.hospital_appointments.config.exceptions.NotFoundException;
import com.ciprian.hospital_appointments.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<?>> handleAllUnknownExceptions(Exception ex) {

        ResponseDto<?> responseDto = ResponseDto.
                builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseDto<?>> handleNotFoundException(NotFoundException ex) {

        ResponseDto<?> responseDto = ResponseDto.
                builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseDto<?>> handleBadRequestException(BadRequestException ex) {

        ResponseDto<?> responseDto = ResponseDto.
                builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }
}
