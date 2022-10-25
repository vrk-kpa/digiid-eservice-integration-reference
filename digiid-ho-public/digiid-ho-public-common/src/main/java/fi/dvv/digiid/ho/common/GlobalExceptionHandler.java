package fi.dvv.digiid.ho.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
class ErrorDto{
    String timestamp;
    String reason;
    int status;
    String message;
}
//new Date().toString(),ex.
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDto> handleBadRequestException(ResponseStatusException ex) {
        ErrorDto error=new ErrorDto();
        error.setReason(ex.getReason());
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now().toString());
        error.setStatus(ex.getRawStatusCode());
        return ResponseEntity.status(ex.getRawStatusCode()).body(error);
    }
}
