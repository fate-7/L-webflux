package com.flux.reactordemo.demo.jdk_reactive;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.IntStream;

/**
 * @author chengzhiqi
 * @date 2021/2/8 10:33 上午
 **/
@Slf4j
public class ReactiveStreamProcessorDemo {

    public static void main(String[] args) throws Exception{
        //1. 创建一个发布者
        SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>();

        //2. 创建一个处理器
        MyProcessorImpl processor = new MyProcessorImpl();

        //3. 发布者与处理器建立订阅关系
        publisher.subscribe(processor);

        //4. 创建一个订阅者
        Flow.Subscriber<Integer> subscriber =  new Flow.Subscriber<>() {

            Flow.Subscription subscription;

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                log.info("【onSubscribe】建立订阅关系");
                this.subscription = subscription;
                subscription.request(1);//第一个需要发布者发布消息给订阅者
            }


            @Override
            public void onNext(Integer item) {
                log.info("【onNext 从Processor 接受到过滤后的 数据 item : {}】 ", item);
                subscription.request(10); //背压 约定订阅者需要的数据限制
            }

            @Override
            public void onError(Throwable throwable) {
                log.info("【onError】发生错误");
            }

            @Override
            public void onComplete() {
                log.info("【onComplete】数据接收完成");
            }
        };
        //5. 建立与处理器间的订阅关系
        processor.subscribe(subscriber);

        //6. 发布消息
        IntStream.range(0, 10).forEach(o -> {
            log.info("【生产数据 {} 】", o);
            publisher.submit(o);
        });

        publisher.close();

        //处理器最后关闭 防止消息未接受完成处理器关闭导致异常
        processor.close();
        //标准异步流处理 主线程需要做阻塞
        Thread.sleep(5000);
    }
}
