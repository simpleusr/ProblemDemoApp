package com.mycompany.problemdemo.service.impl;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.mycompany.problemdemo.service.IExternalService;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Service
public class ExternalService implements IExternalService, InitializingBean {

    @Value("${external.service.api.baseUrl}")
    private String    clientBaseUrl;
    @Value("${external.service.api.resourceUri}")
    private String    clientResourceUri;

    private WebClient webClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        webClient = WebClient.builder().baseUrl(clientBaseUrl).clientConnector(new ReactorClientHttpConnector(HttpClient.create())).build();
    }

    @Override
    public Mono<String> getResponse(String input) {
        return webClient.method(HttpMethod.POST).uri(clientResourceUri).body(BodyInserters.fromValue(input)).retrieve().bodyToMono(String.class);
    }

}
