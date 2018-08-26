package ru.proshik.applepricebot.controller.exception;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GeneralExceptionHandler {

    private static final Logger LOG = Logger.getLogger(GeneralExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    protected void handleException(Exception ex) throws Exception {
        LOG.error("Unexpected error", ex);
        throw ex;
    }

}
