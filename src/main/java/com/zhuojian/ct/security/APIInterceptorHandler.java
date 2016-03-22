package com.zhuojian.ct.security;

import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.impl.AuthHandlerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APIInterceptorHandler extends AuthHandlerImpl {
    private static Logger LOGGER = LoggerFactory.getLogger(APIInterceptorHandler.class);

    public APIInterceptorHandler(AuthProvider authProvider) {
        super(authProvider);
    }

    @Override
    public void handle(RoutingContext context) {
        Session session = context.session();
        if (session != null) {
            User user = context.user();
            if (user != null) {
                LOGGER.info("authorise user: {}", user.principal().encode());
                authorise(user, context);
            } else {
                LOGGER.info("user is not authorised!");
                context.response().setStatusCode(401).end(); // Unauthorized
            }
        } else {
            LOGGER.error("No session!");
            context.fail(new NullPointerException("No session.."));
        }
    }
}
