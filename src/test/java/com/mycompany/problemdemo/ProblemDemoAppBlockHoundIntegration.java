package com.mycompany.problemdemo;

import reactor.blockhound.BlockHound.Builder;
import reactor.blockhound.BlockingOperationError;
import reactor.blockhound.integration.BlockHoundIntegration;

public class ProblemDemoAppBlockHoundIntegration implements BlockHoundIntegration {

    @Override
    public void applyTo(Builder builder) {
        builder.allowBlockingCallsInside("org.springframework.util.ReflectionUtils", "invokeMethod")
               .allowBlockingCallsInside("org.apache.logging.log4j.core.config.LoggerConfig", "callAppenders")
               .allowBlockingCallsInside("java.time.LocalDate", "now")
               .blockingMethodCallback(method -> {
                   new Exception(method.toString()).printStackTrace();
                   throw new BlockingOperationError(method);
               });
    }

}