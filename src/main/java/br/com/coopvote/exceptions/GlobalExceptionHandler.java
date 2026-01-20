package br.com.coopvote.exceptions;

import br.com.coopvote.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<ErrorResponseDto.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorResponseDto.FieldError(error.getField(), error.getDefaultMessage()))
                .toList();

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                request.getMethod(),
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(PautaExistenteException.class)
    public ResponseEntity<ErrorResponseDto> handlePautaExistenteException(PautaExistenteException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(PautaNaoEncontradaException.class)
    public ResponseEntity<ErrorResponseDto> handlePautaNaoEncontradaException(PautaNaoEncontradaException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(PautaFechadaException.class)
    public ResponseEntity<ErrorResponseDto> handlePautaFechadaException(PautaFechadaException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(SessaoFechadaException.class)
    public ResponseEntity<ErrorResponseDto> handleSessaoFechadaException(SessaoFechadaException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(AssociadoNaoAutorizadoException.class)
    public ResponseEntity<ErrorResponseDto> handleAssociadoNaoAutorizadoException(AssociadoNaoAutorizadoException ex) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        
        if (ex.getMessage().contains("não encontrado") || ex.getMessage().contains("inválido")) {
            status = HttpStatus.NOT_FOUND;
        }

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                LocalDateTime.now(),
                status.value(),
                ex.getMessage()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }
}
