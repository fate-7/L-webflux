package com.flux.reactordemo.demo.handler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author chengzhiqi
 * @date 2021/1/18 5:21 下午
 **/

@Component
public class Timehandler {

    public Mono<ServerResponse> getTime(ServerRequest request) {
        return ok().contentType(MediaType.TEXT_PLAIN)
                .body(Mono.just("Now is " + new SimpleDateFormat("HH:mm:ss")
                        .format(new Date())), String.class);
    }

    public Mono<ServerResponse> getDate(ServerRequest response) {
        return ok().contentType(MediaType.TEXT_PLAIN)
                .body(Mono.just("Now is " + new SimpleDateFormat("yyyy:MM:dd")
                        .format(new Date())), String.class);
    }

}
