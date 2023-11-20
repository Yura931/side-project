package subproject.admin.global.exception.handler;

import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import subproject.admin.common.dto.Result;
import subproject.admin.global.exception.InvalidTokenException;
import subproject.admin.global.exception.LoginFailException;
import subproject.admin.global.exception.UserDuplicateException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice @Slf4j
public class ExceptionResponseHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    public Result<?> handleInvalidTokenException(InvalidTokenException e) {
        return errorHandler(e, 401);
    }

    @ExceptionHandler(LoginFailException.class)
    public Result<?> handleLoginFailException(LoginFailException e) {
        return errorHandler(e, 401);
    }

    @ExceptionHandler(UserDuplicateException.class)
    public Result<?> handleUserDuplicateException(UserDuplicateException e) {
        return errorHandler(e, 401);
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public Result<?> handleUnsupportedJwtException(UnsupportedJwtException e) {
        return errorHandler(e, 401);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return errorHandler(e, 401);
    }

    // BindException 감지
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validErrorHandler(errors, HttpStatus.BAD_REQUEST.value()));
    }

    private Result<?> errorHandler(Exception e, Integer status) {
        log.error(e.getMessage());
        return Result.builder()
                .status(status)
                .msg(e.getMessage())
                .errorName(e.getClass().getSimpleName())
                .build();
    }

    private Result<?> validErrorHandler(Map<String, String> errors, Integer status) {
        return Result.builder()
                .status(status)
                .msg("valid error")
                .data(errors)
                .build();
    }

}
