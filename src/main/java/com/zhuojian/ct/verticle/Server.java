package com.zhuojian.ct.verticle;

import com.zhuojian.ct.annotations.RouteHandler;
import com.zhuojian.ct.annotations.RouteMapping;
import com.zhuojian.ct.annotations.RouteMethod;
import com.zhuojian.ct.dao.CTImageDao;
import com.zhuojian.ct.dao.ConsultationDao;
import com.zhuojian.ct.dao.FeatureDao;
import com.zhuojian.ct.utils.AppUtil;
import com.zhuojian.ct.utils.ReflectUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.LocalSessionStore;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by wuhaitao on 2015/12/10.
 */
public class Server extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    // Scan handlers from package 'com.zhuojian.ct.handlers.*'
    private static final Reflections reflections = new Reflections("com.zhuojian.ct.handlers");

    private Integer port = AppUtil.configInt("port");

    protected Router router;

    protected ConsultationDao consultationDao;
    protected CTImageDao ctImageDao;
    protected FeatureDao featureDao;

    @Override
    public void start() throws Exception {

        LOGGER.debug("Start server at port {} .....", port);

        consultationDao = new ConsultationDao(vertx);
        ctImageDao = new CTImageDao(vertx);
        featureDao = new FeatureDao(vertx);

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
        LOGGER.debug("Register available request handlers...");

        Set<Class<?>> handlers = reflections.getTypesAnnotatedWith(RouteHandler.class);
        for (Class<?> handler : handlers) {
            try {
                registerNewHandler(handler);
            } catch (Exception e) {
                LOGGER.error("Error register {}", handler);
            }
        }
    }

    private void registerNewHandler(Class<?> handler) throws Exception {
        String root = "";
        if (handler.isAnnotationPresent(RouteHandler.class)) {
            RouteHandler routeHandler = handler.getAnnotation(RouteHandler.class);
            root = routeHandler.value();
        }
        Object instance = null;
        if (ReflectUtil.hasParams(handler, ConsultationDao.class)){
            Constructor constructor = handler.getConstructor(ConsultationDao.class);
            instance = constructor.newInstance(consultationDao);
        }
        else if(ReflectUtil.hasParams(handler, CTImageDao.class)){
            Constructor constructor = handler.getConstructor(CTImageDao.class);
            instance = constructor.newInstance(ctImageDao);
        }
        else if(ReflectUtil.hasParams(handler, FeatureDao.class)){
            Constructor constructor = handler.getConstructor(FeatureDao.class);
            instance = constructor.newInstance(featureDao);
        }else{
            instance = handler.newInstance();
        }
        Method[] methods = handler.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(RouteMapping.class)) {
                RouteMapping mapping = method.getAnnotation(RouteMapping.class);
                RouteMethod routeMethod = mapping.method();
                String url = root + mapping.value();
                Handler<RoutingContext> methodHandler = (Handler<RoutingContext>) method.invoke(instance);
                LOGGER.debug("Register New Handler -> {}:{}", routeMethod, url);
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
