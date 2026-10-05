package br.edu.agenda;

import java.util.LinkedHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ErroHandler extends ResponseEntityExceptionHandler {
    @Override
    protected org.springframework.http.ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        var campos = new LinkedHashMap<String, String>();
        exception.getBindingResult().getFieldErrors().forEach(erro -> campos.put(erro.getField(), erro.getDefaultMessage()));
        var problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Confira os campos enviados");
        problema.setProperty("campos", campos);
        return handleExceptionInternal(exception, problema, headers, status, request);
    }
}
