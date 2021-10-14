package com.zandriod.consumer.util;

import com.zandriod.consumer.exception.NonRecoverableException;
import com.zandriod.consumer.exception.RecoverableException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResponseValidationUtil {

    //These exceptions can be whatever custom exception you would like to have for the service interaction.
    public static void codeValidation(int statusCode) {
        switch (statusCode) {
            case 500:
                throw new NonRecoverableException("Internal Server Error when trying to call ");
            case 502:
                throw new NonRecoverableException("Bad Gateway when trying to call " );
            case 503:
                throw new RecoverableException("Service is temporarily unavailable at " );
            case 504:
                throw new RecoverableException("Gateway Timeout when trying to call " );
            case 408:
                throw new RecoverableException("Request Timeout when calling ");
            default:
                log.warn("Some non-validated exception occurred with status code {}", statusCode);
        }
    }
}
