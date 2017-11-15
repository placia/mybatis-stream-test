package com.placia.spring5.mybatisstreamtest;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ResultHandlerWithSubscriber implements ResultHandler<User>  {
    private final AtomicInteger readCount;
    private final AtomicInteger publishCount;
    private List<FluxSink<User>> sinks;
    private Flux<User> resultStream;
    private int start = 0;
    private int size = Integer.MAX_VALUE;

    public ResultHandlerWithSubscriber() {
        this.readCount = new AtomicInteger();
        this.publishCount = new AtomicInteger();
        this.sinks = new ArrayList<>();

        this.resultStream =
                Flux.create(sink -> {
                    sinks.add(sink);
                    sink.onCancel(() -> sinks.remove(sink));
                });
    }

    public Flux<User> getResultStream() {
        return resultStream;
    }

    public void setStart(int start) {
        setLimit(start, Integer.MAX_VALUE);
    }

    public void setSize(int size) {
        setLimit(0, size);
    }

    public void setLimit(int start, int size) {
        this.start = start;
        this.size = size;
    }

    public int getPublishCount() {
        return publishCount.get();
    }

    @Override
    public void handleResult(ResultContext<? extends User> resultContext) {
        int index = readCount.getAndIncrement();

        sinks.forEach(handler -> {
            if (resultContext.isStopped()) {    //isStopped() is not work
                handler.complete();
            } else {
                if (start <= index) {
                    handler.next(resultContext.getResultObject());
                    int i = publishCount.incrementAndGet();
                    if (i == size)
                        handler.complete();
                }
            }
        });
    }
}
