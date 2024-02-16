package com.tech.gameService.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.gameService.common.error.RestError;
import com.tech.gameService.common.exception.GenericException;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.Retryable;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.IOException;
import java.util.Arrays;

public class UtilsAppelAPI {

    public static final String CORRELATION_ID = "correlationId";
    private UtilsAppelAPI() {}

    @Retryable(maxAttempts=10,value=RuntimeException.class,backoff = @Backoff(delay = 10000,multiplier=2))
    public static <T> T callAPIWithMethod(String url, HttpMethod httpMethod,  HttpHeaders httpHeaders, Object objectBody, Class<T> t,
                                          MediaType contentType, boolean withoutTimeout) throws GenericException {

        RestTemplate restTemplate = null;
        if (withoutTimeout) {
            restTemplate = new RestTemplate(getClientHttpRequestFactory());
        } else {
            restTemplate = new RestTemplate();
        }

        HttpHeaders headers;
        if(httpHeaders != null){
            headers = httpHeaders;
        }else{
            headers = new HttpHeaders();
        }

        String correlationId = null;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            correlationId = (String) requestAttributes.getAttribute(CORRELATION_ID,
                    RequestAttributes.SCOPE_SESSION);
        }
        headers.set(CORRELATION_ID, correlationId);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));

        headers.setContentType(contentType);

        String body = null;

        if (objectBody != null) {
            if (MediaType.APPLICATION_JSON_UTF8.equals(contentType)) {
                ObjectMapper obj = new ObjectMapper();
                try {
                    body = obj.writeValueAsString(objectBody);
                } catch (JsonProcessingException e) {
                    // On ne fait rien
                }
            } else {
                body = (String) objectBody;
            }
        }

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    httpMethod,
                    entity,
                    t);

            return response.getBody();
        } catch (HttpStatusCodeException exception) {
            manageExceptionAPI(exception, url);
        }

        return null;
    }

    /**
     * Fonction permettant de faire un appel vers une des APIs et de récupérer un objet correspondant à
     * une classe donnée en paramètre
     * @param url Correspond à l'URL à appeler
     * @param httpMethod Correspond à la méthode HTTP à appeler
     * @param objectBody Correspond au body de la requête à envoyer
     * @param t Correspond à la classe que l'on s'attend à avoir en retour
     * @return Retourne le retour de l'appel, mappé sur un objet correspondant à la classe donnée en paramètre
     * @throws GenericException Exception remontée par l'API sous la forme attendue par l'application SILVA
     */
    @Retryable(maxAttempts=10,value=RuntimeException.class,backoff = @Backoff(delay = 10000,multiplier=2))
    public static <T> T callAPIWithMethod(String url, HttpMethod httpMethod, Object objectBody, Class<T> t) throws GenericException {

        return callAPIWithMethod(url, httpMethod, null, objectBody, t, MediaType.APPLICATION_JSON_UTF8, false);
    }


    //Override timeouts in request factory
    private static SimpleClientHttpRequestFactory getClientHttpRequestFactory()
    {
        SimpleClientHttpRequestFactory clientHttpRequestFactory
                = new SimpleClientHttpRequestFactory();
        //Connect timeout
        clientHttpRequestFactory.setConnectTimeout(0);

        //Read timeout
        clientHttpRequestFactory.setReadTimeout(0);
        return clientHttpRequestFactory;
    }

    /**
     * Fonction permettant de gérer les exceptions générées par Spring Rest Client
     * @param exception
     * @param url
     */
    private static void manageExceptionAPI(RestClientResponseException exception, String url) throws GenericException {

        ObjectMapper mapper = new ObjectMapper();
        RestError error = null;
        try {
            error = mapper.readValue(exception.getResponseBodyAsString(), RestError.class);
        } catch (IOException e) {
            throw exception;
        }

        if (error != null) {
            throw new GenericException(error.getExceptionMessage(), error.getTranslationCodeError(), url);
        } else {
            throw exception;
        }
    }
}