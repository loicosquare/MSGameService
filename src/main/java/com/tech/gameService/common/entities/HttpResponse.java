package com.tech.gameService.common.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_NULL) //To send only the non_null element when sending the response back.
public class HttpResponse<T> implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss", timezone = "Europa/Paris")
    protected Date timeStamp;
    protected int httpStatusCode; // 200, 201, 400, 500
    protected HttpStatus httpStatus;
    protected String reason;
    protected String message;
    protected Collection<? extends T> games;
}
