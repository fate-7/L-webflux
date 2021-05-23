package com.flux.reactordemo.demo.config;

import com.flux.reactordemo.demo.handler.HelloHandler;
import com.flux.reactordemo.demo.handler.Timehandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import org.springframework.web.reactive.function.server.RouterFunction;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import org.springframework.web.reactive.function.server.ServerResponse;


/**
 * @author chengzhiqi
 * @date 2021/1/18 5:27 下午
 **/

@Configuration
public class RouterConfig {

    @Autowired
    private Timehandler timehandler;

    @Autowired
    private HelloHandler helloHandler;

    @Bean
    public RouterFunction<ServerResponse> timerRouter() {
        return route(GET("/time"), timehandler::getTime)
                .andRoute(GET("/date"), timehandler::getDate);
    }

    @Bean
    public RouterFunction<ServerResponse> helloRouter() {
        return route(GET("/hello"),helloHandler::getHello);
    }



}
