package com.flux.reactordemo.demo.handler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * @author chengzhiqi
 * @date 2021/5/9 2:58 下午
 **/
@Component
public class HelloHandler {

    public Mono<ServerResponse> getHello(ServerRequest request) {
        return ok().contentType(MediaType.TEXT_PLAIN)
                .body(Mono.just("Hello"), String.class);
    }
}
