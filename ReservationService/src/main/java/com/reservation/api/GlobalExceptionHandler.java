package com.reservation.api;


import com.reservation.exception.CustomerAlreadyExistsException;
import com.reservation.exception.CustomerHasReservationsException;
import com.reservation.exception.CustomerNotFoundException;
import com.reservation.exception.ReservationNotFoundException;
import com.reservation.exception.TableCapacityExceededException;
import com.reservation.exception.TableNotAvailableException;
import com.reservation.exception.TableNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {
    // ---- 404 ----
    @ExceptionHandler({ReservationNotFoundException.class,
            TableNotFoundException.class,
            CustomerNotFoundException.class})
    public ProblemDetail notFound(RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ---- 409 ----
    @ExceptionHandler({TableNotAvailableException.class,
            CustomerAlreadyExistsException.class,
            CustomerHasReservationsException.class})
    public ProblemDetail conflict(RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    /** Dos peticiones simultaneas que pasaron la validacion: las restricciones unicas de la BD las frenan. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail integrity(DataIntegrityViolationException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "Los datos violan una restriccion (mesa ya reservada en ese turno o email duplicado)");
    }

    // ---- 400 ----
    @ExceptionHandler(TableCapacityExceededException.class)
    public ProblemDetail capacity(TableCapacityExceededException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Datos de entrada invalidos");
        pd.setProperty("errors", errors);
        return pd;
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ProblemDetail badRequest(Exception ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Formato invalido: fecha con formato yyyy-MM-dd y turno LUNCH o DINNER");
    }
}
