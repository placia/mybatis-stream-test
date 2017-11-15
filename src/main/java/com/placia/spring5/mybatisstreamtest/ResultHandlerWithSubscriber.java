package com.placia.spring5.mybatisstreamtest;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ResultHandlerWithSubscriber implements ResultHandler<User>  {
    private final AtomicInteger count;
    private List<FluxSink<User>> handlers;
    private Flux<User> resultStream;

    public ResultHandlerWithSubscriber() {
        this.count = new AtomicInteger();
        this.handlers = new ArrayList<>();

        this.resultStream =
                Flux.create(sink -> {
                    handlers.add(sink);
                    sink.onCancel(() -> handlers.remove(sink));
                });
    }

    public Flux<User> getResultStream() {
        return resultStream;
    }

    @Override
    public void handleResult(ResultContext<? extends User> resultContext) {
        handlers.forEach(handler -> {
            if (resultContext.isStopped()) {    //isStopped() is not work
                handler.complete();
            } else {
                count.incrementAndGet();
                handler.next(resultContext.getResultObject());
            }
        });
    }
}
