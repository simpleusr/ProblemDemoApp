package com.mycompany.problemdemo.service;

import reactor.core.publisher.Mono;

public interface IExternalService {

    Mono<String> getResponse(String input);

}
