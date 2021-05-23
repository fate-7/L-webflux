package com.flux.reactordemo.demo.jdk_reactive;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * 使用Flow api实现 processor
 * @author chengzhiqi
 * @date 2021/2/8 10:14 上午
 **/
@Slf4j
public class MyProcessorImpl extends SubmissionPublisher<Integer> implements Flow.Processor<Integer, Integer> {

    private Flow.Subscription subscription;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        log.info("【Processor 收到订阅请求】");
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(Integer item) {
        log.info("【onNext 收到发布者数据  : {} 】", item);

        //中间处理 数据转换发给真正的订阅者
        if (item % 2 == 0) {
            //筛选偶数 发送给 订阅者
            this.submit(item);
        }
        //背压实现的核心
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        log.info("【onNext】发生异常");
        this.subscription.cancel();
    }

    @Override
    public void onComplete() {
        log.info("【onComplete】Processor接收数据完成");
    }
}
