package com.zhuojian.ct.verticle;

import com.zhuojian.ct.annotations.HandlerDao;
import com.zhuojian.ct.annotations.RouteHandler;
import com.zhuojian.ct.annotations.RouteMapping;
import com.zhuojian.ct.annotations.RouteMethod;
import com.zhuojian.ct.utils.AppUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.LocalSessionStore;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by wuhaitao on 2015/12/10.
 */
public class WebServer extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebServer.class);

    // Scan handlers from package 'com.zhuojian.ct.handlers.*'
    private static final Reflections handlerReflections = new Reflections("com.zhuojian.ct.handlers");
    // Scan daos from package 'com.zhuojian.ct.dao.*'
    private static final Reflections daoReflections = new Reflections("com.zhuojian.ct.dao");

    private Integer port = AppUtil.configInt("port");

    protected Router router;

    private Map<Class<?>,Object> daoMap;

    @Override
    public void start() throws Exception {

        LOGGER.info("Start server at port {} .....", port);

        daoMap = new HashMap<>();
        daoMap.put(vertx.getClass(), vertx);

        LOGGER.info("Start scanning daos....");
        Set<Class<?>> daos = daoReflections.getTypesAnnotatedWith(HandlerDao.class);
        for (Class<?> dao : daos) {
            LOGGER.info("Scan dao {}", dao.getSimpleName());
            Constructor constructor = dao.getConstructor(Vertx.class);
            if (constructor == null){
                LOGGER.error("{} is not satisfiable, dao must have a constructor with param type Vertx.class!",dao.getSimpleName());
                continue;
            }
            daoMap.put(dao, constructor.newInstance(vertx));
        }
        LOGGER.info("Scanning dao finished!");

        router = Router.router(vertx);

        router.route().handler(CookieHandler.create());
        router.route().handler(BodyHandler.create().setUploadsDirectory(AppUtil.getUploadDir()));
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

        // registerHandlers
        registerHandlers();

        // Must be the latest handler to register
        router.route().handler(StaticHandler.create());

        vertx.createHttpServer().requestHandler(router::accept).listen(port);

    }

    private void registerHandlers() {
        LOGGER.info("Register available request handlers...");
        Set<Class<?>> handlers = handlerReflections.getTypesAnnotatedWith(RouteHandler.class);
        for (Class<?> handler : handlers) {
            try {
                registerNewHandler(handler);
            } catch (Exception e) {
                LOGGER.error("Error register {}", handler);
            }
        }
        LOGGER.info("Register request handlers finished!");
    }

    private void registerNewHandler(Class<?> handler) throws Exception {
        String root = "";
        if (handler.isAnnotationPresent(RouteHandler.class)) {
            RouteHandler routeHandler = handler.getAnnotation(RouteHandler.class);
            root = routeHandler.value();
        }
        Object instance = null;
        LOGGER.info("Handler:{}", handler.getSimpleName());
        Constructor[] constructors = handler.getConstructors();
        for (Constructor constructor:constructors){
            Class<?>[] clazzs = constructor.getParameterTypes();
            int length = clazzs.length;
            Object[] objects = new Object[length];
            for (int i = 0; i < length; i++) {
                objects[i] = daoMap.get(clazzs[i]);
            }
            instance = constructor.newInstance(objects);
            break;
        }

        Method[] methods = handler.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(RouteMapping.class)) {
                RouteMapping mapping = method.getAnnotation(RouteMapping.class);
                RouteMethod routeMethod = mapping.method();
                String url = root + mapping.value();
                Handler<RoutingContext> methodHandler = (Handler<RoutingContext>) method.invoke(instance);
                LOGGER.info("Register New Handler -> {}:{}", routeMethod, url);
                switch (routeMethod) {
                    case POST:
                        router.post(url).handler(methodHandler);
                        break;
                    case PUT:
                        router.put(url).handler(methodHandler);
                        break;
                    case DELETE:
                        router.delete(url).handler(methodHandler);
                        break;
                    case GET: // fall through
                    default:
                        router.get(url).handler(methodHandler);
                        break;
                }
            }
        }
    }

}
