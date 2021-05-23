package com.flux.reactordemo.demo.jdk_reactive;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Reactive Stream 演示代码 体现背压机制
 * @author chengzhiqi
 * @date 2021/2/6 11:13 下午
 **/
@Slf4j
public class ReactiveStreamDemo {


    public static void main(String[] args) throws InterruptedException {

        //1. 创建一个发布者
        SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>();

        //2. 创建一个订阅者
        Flow.Subscriber<Integer> subscriber =  new Flow.Subscriber<>() {

            private Flow.Subscription subscription;

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                System.out.println("建立订阅关系");
                this.subscription = subscription;
                System.out.println("订阅成功。。");
                subscription.request(1);//第一个需要发布者发布消息给订阅者
                System.out.println("订阅方法里请求一个数据");
            }

            @SneakyThrows
            @Override
            public void onNext(Integer item) {
                log.info("【onNext 接受到数据 item : {}】 ", item);
                TimeUnit.SECONDS.sleep(1);
                subscription.request(10); //背压 约定订阅者需要的数据限制
            }

            @Override
            public void onError(Throwable throwable) {
                log.info("【onError 出现异常】");
                subscription.cancel();
            }

            @Override
            public void onComplete() {
                log.info("【onComplete 所有数据接收完成】");
            }
        };
        //3. 建立订阅关系
        publisher.subscribe(subscriber);

        //4. 发布消息
        IntStream.range(0, 1000).forEach(o -> {
            log.info("【生产数据 {} 】", o);
            publisher.submit(o);
        });

        //5. 发布者 数据都已发布完成后，关闭发送，此时会回调订阅者的onComplete方法
        publisher.close();
        //标准异步流处理 主线程需要做阻塞
        Thread.sleep(1000 * 100);

    }
}
