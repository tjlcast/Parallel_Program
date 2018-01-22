package com.tjlcast.request_mvn.springmvn;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by tangjialiang on 2018/1/22.
 *
 * http://blog.csdn.net/han_dada/article/details/65450435
 *
 * ObjectFactoryDelegatingInvocationHandler类：该类是AutowireUtils的一个私有类
 * 该类拦截了除了equals、hashcode以及toString以外的其他方法，
 * 其中的objectFactory是RequestObjectFactory实例
 *
 *
 *
 *  既然需要从Threadlocal中获取对象，那springmvc在何时向Threadlocal设置了该对象呢？分别在如下两个类中完成：
 *  RequestContextListener和FrameworkServlet。
 *  RequestContextListener负责监听servletcontext，当servletcontext启动时，RequestContextListener向Threadlocal设置了httprequest对象。
 *  FrameworkServlet是DispatchServlet的基类，tomcat会在运行过程中启动新的线程，而该线程中并没有httprequest对象。
 *  因此servlet会在每次处理http请求的时候检验当前的Threadlocal中是否有httprequest对象，如果没有则设置该对象。
 *
 *
 *  所以在@Autowired的中HttpRequest对象其实是一个代理对象，它的所有方法都会被代理到ThreadLocal中存储的对象中，
 *  从而形成线程安全的对象。
 */
public class ObjectFactoryDelegatingInvocationHandler implements InvocationHandler {

    private final ObjectFactory objectFactory ;

    public ObjectFactoryDelegatingInvocationHandler(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory ;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName() ;
        if (methodName.equals("equals")) {
            return (proxy == args[0]) ;
        } else if (methodName.equals("hashCode")) {
            return System.identityHashCode(proxy) ;
        } else if (methodName.equals("toString")) {
            return this.objectFactory.toString() ;
        }

        try {
            return method.invoke(this.objectFactory.getObject(), args) ;
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException() ;
        }
    }
}
