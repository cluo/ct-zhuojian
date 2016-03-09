package com.zhuojian.ct;

import com.zhuojian.ct.annotations.RouteHandler;
import com.zhuojian.ct.dao.ConsultationDao;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.Set;

/**
 * Created by wuhaitao on 2016/3/9.
 */
public class ReflectTest {
    public static void main(String[] args) {
        Reflections reflections = new Reflections("com.zhuojian.ct.handlers");
        Set<Class<?>> handlers = reflections.getTypesAnnotatedWith(RouteHandler.class);
        for (Class<?> handler : handlers) {
            try {
                Object instance = null;
                Constructor constructor = handler.getConstructor(ConsultationDao.class);
                if (constructor != null){
                    System.out.println(handler.getSimpleName());
                }
            } catch (Exception e) {
                System.out.println("Error register {}");
            }
        }
    }
}
