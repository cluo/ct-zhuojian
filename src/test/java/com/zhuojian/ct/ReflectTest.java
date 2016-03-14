package com.zhuojian.ct;

import com.zhuojian.ct.annotations.RouteHandler;
import com.zhuojian.ct.dao.ConsultationDao;
import org.junit.Test;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.TypeVariable;
import java.util.Set;

/**
 * Created by wuhaitao on 2016/3/9.
 */
public class ReflectTest {
    @Test
    public void test() {
        Reflections reflections = new Reflections("com.zhuojian.ct.handlers");
        Set<Class<?>> handlers = reflections.getTypesAnnotatedWith(RouteHandler.class);
        for (Class<?> handler : handlers) {
            Object instance = null;
            System.out.println(handler.getSimpleName());
            Constructor[] constructors = handler.getConstructors();
            System.out.println(constructors.length);
            for (Constructor constructor : constructors) {
                Class<?>[] clazzs = constructor.getParameterTypes();
                for (Class<?> clazz : clazzs) {
                    System.out.println(clazz.getSimpleName());
                }
                break;
            }
            System.out.println("-----------------------------------------");

        }
    }
}
