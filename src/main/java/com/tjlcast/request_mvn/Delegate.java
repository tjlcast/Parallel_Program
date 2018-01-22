package com.tjlcast.request_mvn;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by tangjialiang on 2018/1/22.
 */
public class Delegate implements InvocationHandler {

    private Factory factory ;

    public Factory getFactory() {
        return this.factory ;
    }

    public void setFactory(Factory factory) {
        this.factory = factory ;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(this.getFactory().getObject(), args) ;
    }
}
