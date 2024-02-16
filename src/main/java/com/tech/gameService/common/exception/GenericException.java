package com.tech.gameService.common.exception;

import lombok.Getter;

@Getter
public class GenericException extends Exception {

    @Getter
    private final String translationCodeMessage;

    @Getter
    private final String urlAPI;

    public GenericException(String message, String translationCodeMessage, String urlAPI) {
        super(message);
        this.translationCodeMessage = translationCodeMessage;
        this.urlAPI = urlAPI;
    }
}