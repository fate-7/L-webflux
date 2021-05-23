package com.flux.reactordemo.demo.spring_reactor;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * @author chengzhiqi
 * @date 2021/2/6 11:32 下午
 **/
@Slf4j
public class MonoAndFluxDemo {

    private static final SimpleDateFormat SIMPLEDATEFORMAT = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

    public static void main(String[] args) throws InterruptedException{
        MonoAndFluxDemo monoAndFluxDemo = new MonoAndFluxDemo();
        monoAndFluxDemo.reactorSubscribeOn();

    }


    /**
     * Mono 创建方式
     */
    public void createSimpleMono() {
        Mono.empty().subscribe(System.out::println);
        Mono.just("hello mono").subscribe(System.out::println);
    }

    /**
     * 演示mono defer
     */
    public void createMonoDefer(){
        //Mono just and defer
        final Mono<Date> just = Mono.just(new Date());
        final Mono<Date> defer = Mono.defer(() -> Mono.just(new Date()));
        just.subscribe(o -> System.out.println("just first" + SIMPLEDATEFORMAT.format(o)));
        defer.subscribe(o -> System.out.println("defer first" +SIMPLEDATEFORMAT.format(o)));

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        just.subscribe(o -> System.out.println("just second" + SIMPLEDATEFORMAT.format(o)));
        defer.subscribe(o -> System.out.println("defer second" +SIMPLEDATEFORMAT.format(o)));
    }


    /**
     * 创建flux
     */
    public void createFlux() {
        //Flux创建方式 0-n
        Flux.just(1,2,3,4,5,6).subscribe(System.out::println);
        Flux.fromIterable(Arrays.asList("a", "b", "c", "d")).subscribe(System.out::println);
        Flux.fromArray(new String[] {"a", "b", "c", "d"}).subscribe(System.out::println);
        Flux.fromStream(Stream.of(1,2,3,4,5,6)).subscribe(System.out::println);
        Flux.range(1, 10).subscribe(System.out::println);

        //create和generate适合复杂场景构造
        Flux.generate(() -> 0, (i, sink) -> {
            sink.next(i + " ^ 2" +" = " + i*i);
            if (i == 9) {
                sink.complete();
            }
            return i + 1;
        }).subscribe(System.out::println);


        Flux<String> flux = Flux.generate(
                AtomicLong::new,
                (state, sink) -> {
                    long i = state.getAndIncrement();
                    sink.next("3 x " + i + " = " + 3*i);
                    if (i == 10) {
                        sink.complete();
                    }
                    return state;
                }, (state) -> System.out.println("state: " + state));
        flux.subscribe(System.out::println);
    }


    /**
     * flux#subscribe 演示
     *
     * 1. subscribe();
     *
     * 2. subscribe(Consumer<? super T> consumer);
     *
     * 3. subscribe(Consumer<? super T> consumer,
     *           Consumer<? super Throwable> errorConsumer);
     *
     * 4. subscribe(Consumer<? super T> consumer,
     *           Consumer<? super Throwable> errorConsumer,
     *           Runnable completeConsumer);
     *
     * 5. subscribe(Consumer<? super T> consumer,
     *           Consumer<? super Throwable> errorConsumer,
     *           Runnable completeConsumer,
     *           Consumer<? super Subscription> subscriptionConsumer);
     */
    public void createFluxSignature() {
        //1
        log.info("show method signature 1");
        Flux<Integer> ints1 = Flux.range(1, 3);
        ints1.subscribe();

        //2
        log.info("show method signature 2");
        Flux<Integer> ints2 = Flux.range(1, 3);
        ints2.subscribe(System.out::println);

        //3
        log.info("show method signature 3");
        Flux<Integer> ints3 = Flux.range(1, 4)
                .map(i -> {
                    if (i <= 3) {
                        return i;
                    }
                    throw new RuntimeException("Got to 4");
                });
        ints3.subscribe(System.out::println, error -> System.err.println("Error: " + error));

        //4
        log.info("show method signature 4");
        Flux<Integer> ints4 = Flux.range(1, 4);
        ints4.subscribe(System.out::println,
                error -> System.err.println("Error " + error),
                () -> System.out.println("Done"));

        //5
        log.info("show method signature 5");
        Flux<Integer> ints5 = Flux.range(1, 4);
        ints5.subscribe(System.out::println,
                error -> System.err.println("Error " + error),
                () -> System.out.println("Done"),
                sub -> sub.request(2));
    }


    /**
     * 使用BaseSubscriber自定义订阅者
     */
    public void userSampleSubscriber() {
        SampleSubscriber<Integer> ss = new SampleSubscriber<>();
        Flux<Integer> ints = Flux.range(1, 4);
        ints.subscribe(ss);
    }

    /**
     * 使用BaseSubscriber自定义订阅者
     */
    public void useBaseSubscriber() {
        Flux.range(1, 10)
                .doOnRequest(r -> System.out.println("request of " + r))
                .subscribe(new BaseSubscriber<Integer>() {

                    @Override
                    public void hookOnSubscribe(Subscription subscription) {
                        request(1);
                    }

                    @Override
                    public void hookOnNext(Integer integer) {
                        System.out.println("Cancelling after having received " + integer);
                        cancel();
                    }
                });
    }

    /**
     * 演示操作符
     */
    public void showOperate() {
        Flux<Integer> flux = Flux.just(1, 2, 3, 4, 5, 6);
        //依次处理
        log.info("----------------map----------------------------");
        flux.map(i -> i*3).subscribe(System.out::println);

        Flux<Integer> array = Flux.fromArray(new Integer[]{1, 2, 3, 4, 5, 6});
        log.info("----------------flatMap------------------------");
        //拍平
        array.flatMap(i -> flux).subscribe(System.out::println);
        log.info("----------------filter-------------------------");
        //过滤
        array.filter(i -> i > 3).subscribe(System.out::println);
        log.info("----------------zip----------------------------");
        //合并
        Flux.zip(flux, array).subscribe(zip -> System.out.println(zip.getT1() + zip.getT2()));
    }

    /**
     * 创建线程
     * Schedulers.immediate(); //当前线程
     * Schedulers.single(); //可重用的单线程
     * Schedulers.elastic(); //弹性线程池，超过60s不使用的线程废弃
     * Schedulers.parallel(); //固定大小线程池，等同cpu线程
     * Schedulers.fromExecutorService(); //自定义线程池
     */
    public void reactorPublishOn() throws InterruptedException {
        Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);

        new Thread(() -> Flux
                .range(1, 2)
                .map(i -> {
                    log.info("map-1 Current Thread name = {}, i = {}", Thread.currentThread().getName(), i + 10);
                    return 10 + i;
                })
                .publishOn(s)
                .map(i -> {
                    log.info("map-2 Current Thread name = {}, i = {}", Thread.currentThread().getName(), i);
                    return "value " + i;
                }).subscribe(System.out::println), "Thread_A").start();
    }


    public void reactorSubscribeOn() {
        Scheduler s = Schedulers.newParallel("parallel-scheduler-1", 4);
        Scheduler ss = Schedulers.newParallel("parallel-scheduler-2", 4);
        new Thread(() -> Flux
                .range(1, 2)
                .map(i -> {
                    log.info("map-1 Current Thread name = {}, i = {}", Thread.currentThread().getName(), i + 10);
                    return 10 + i;
                })
                .subscribeOn(s)
                .publishOn(s)
                .map(i -> {
                    log.info("map-2 Current Thread name = {}, i = {}", Thread.currentThread().getName(), i);
                    return "value " + i;
                }).subscribe(System.out::println), "Thread_B").start();
    }



}


class SampleSubscriber<T> extends BaseSubscriber<T> {

    @Override
    public void hookOnSubscribe(Subscription subscription) {
        System.out.println("Subscribed");
        request(1);
    }

    @Override
    public void hookOnNext(T value) {
        System.out.println(value);
        request(1);
    }
}
