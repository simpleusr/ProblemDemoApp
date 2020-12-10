package com.mycompany.problemdemo.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.mycompany.problemdemo.service.IExternalService;

import reactor.core.publisher.Mono;

@Configuration
public class RouterConfig {

    @Autowired
    IExternalService externalService;

    @Bean
    RouterFunction<ServerResponse> performSearch() {
        return RouterFunctions.route(RequestPredicates.GET("/demo"), serverReq -> doReturnResp(serverReq));
    }

    private Mono<ServerResponse> doReturnResp(ServerRequest serverReq) {
        String paramVal = serverReq.queryParam("param1").orElseGet(() -> "unavailable");
        return ServerResponse.ok().body(BodyInserters.fromPublisher(externalService.getResponse(paramVal), String.class));
    }

}
