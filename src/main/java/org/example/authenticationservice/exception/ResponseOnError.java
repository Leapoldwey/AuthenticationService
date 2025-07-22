package org.example.authenticationservice.exception;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ResponseOnError {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
    private LocalDateTime timestamp;
    private HttpStatus status;
    private String message;
}
