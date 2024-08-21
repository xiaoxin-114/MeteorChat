package com.meteor.chat.consumer;



public abstract class AbstractConsumer<T> {

    public AbstractConsumer() {
        ConsumerExecutor.register(getKey(), this);
    }

    public abstract void consume(T t);

    public abstract String getKey();

}
