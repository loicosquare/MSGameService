package com.tech.gameService.common.error;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RestError {

    private String translationCodeError;
    private String exceptionMessage;
    private String correlationId;

    public RestError(String translationCodeError, String exceptionMessage, String correlationId) {
        this.translationCodeError = translationCodeError;
        this.exceptionMessage = exceptionMessage;
        this.correlationId = correlationId;
    }
}