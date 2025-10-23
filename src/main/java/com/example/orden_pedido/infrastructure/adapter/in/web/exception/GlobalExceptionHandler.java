package com.example.orden_pedido.infrastructure.adapter.in.web.exception;

import com.example.orden_pedido.shared.util.BaseResponse;
import com.example.orden_pedido.shared.util.JsonPropertyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new BaseResponse<>("ERROR",ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        log.error("[EXCEPTION HANDLER] Error inesperado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse<>("ERROR","Unexpected error occurred"));
    }

    @ExceptionHandler(ResourceValidationException.class)
    public ResponseEntity<?> handleValidation(ResourceValidationException ex) {
        log.warn("[VALIDATION jSON] {}", ex.getErrors());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new BaseResponse<>(ex.getMessage(), ex.getErrors()));
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        // En este punto, puedes agregar lógica para loguear el error o inspeccionar la causa
        String errorMessage = "El JSON enviado es inválido o está malformado. Por favor, revisa la sintaxis.";
        log.error("❌ Error de JSON: {}", ex.getMessage());

        // Retorna una respuesta 400 Bad Request con un mensaje personalizado
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>("Corregir el request Json ", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        error -> JsonPropertyMapper.mapFieldPathToJsonPath(ex.getBindingResult().getTarget(), error.getField()),
                        error -> error.getDefaultMessage()
                ));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>( "Validaciones de JSON fallidas", errors));
    }

    @ExceptionHandler(PurchaseOrderAlreadyExistsException.class)
    public ResponseEntity<?> handlePurchaseOrderAlreadyExists(PurchaseOrderAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new BaseResponse<>("ERROR",ex.getMessage()) );
    }

    @ExceptionHandler(FactoryOrderAlreadyExistsException.class)
    public ResponseEntity<?> handleFactoryOrderAlreadyExists(FactoryOrderAlreadyExistsException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new BaseResponse<>(ex.getMessage(),ex.getExistingOrderIds()) );
    }


    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<?> handleInternalServerErrorException(InternalServerErrorException ex) {
        log.error("❌ Error al registrar la orden :{} ", ex.getMessage(), ex);
        BaseResponse<?> response = new BaseResponse<>(
                "ERROR",
                String.format("Error interno: %s",ex.getMessage())
         );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }



    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> handleDataAccess(DataAccessException ex) {
        log.error("❌ Error de base de datos: {} ", ex.getMessage(), ex);
        String message;

        if (ex instanceof BadSqlGrammarException) {
            Throwable rootCause = ((BadSqlGrammarException) ex).getRootCause();
            if (rootCause instanceof SQLException) {
                String errorMessage = rootCause.getMessage();
                if (errorMessage != null && errorMessage.toLowerCase().contains("data type mismatch")) {
                    // Expresión regular para encontrar "P#=NN" y capturar el número
                    Pattern pattern = Pattern.compile("P#=(\\d+)");
                    Matcher matcher = pattern.matcher(errorMessage);

                    if (matcher.find()) {
                        String parameterNumber = matcher.group(1);
                        message = "Error: Incompatibilidad de tipo de dato en el parámetro #" + parameterNumber + ".";
                    } else {
                        message = "Error: Incompatibilidad de tipo de dato. Revise los valores.";
                    }
                } else {
                    message = "Error en la sentencia SQL ejecutada.";
                }
            } else {
                message = "Error en la sentencia SQL ejecutada.";
            }
        } else if (ex instanceof DuplicateKeyException) {
            message = "Registro duplicado, clave ya existente.";
        } else {
            message = ex.getMessage();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse<>("ERROR", message));
    }

}
