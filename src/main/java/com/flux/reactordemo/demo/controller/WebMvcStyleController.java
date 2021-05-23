package com.flux.reactordemo.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author chengzhiqi
 * @date 2021/1/18 5:14 下午
 **/


@RestController
public class WebMvcStyleController {

    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("Welcome to web flux world ~");
    }


    @GetMapping("/generateHello")
    public Flux<String> generateHello() {
        return Flux.generate(sink -> {
            sink.next("hello");
            sink.complete();
        });
    }

    @GetMapping("/createHello")
    public Flux<String> createHello() {
        return Flux.create(sink -> {
            sink.next("hello");
            sink.next("world");
            sink.complete();
        });
    }
}
