package com.zhuojian.ct.handlers;

import com.zhuojian.ct.annotations.RouteHandler;
import com.zhuojian.ct.annotations.RouteMapping;
import com.zhuojian.ct.annotations.RouteMethod;
import com.zhuojian.ct.utils.AppUtil;
import com.zhuojian.ct.utils.SQLUtil;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RouteHandler("/api")
public class SecurityHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityHandler.class);

    @RouteMapping(value = "/permission", method = RouteMethod.POST)
    public Handler<RoutingContext> permissions() {
        return ctx -> {
            JDBCClient client = AppUtil.getJdbcClient(Vertx.vertx());
            client.getConnection(conn -> {
                if (conn.failed()) {
                    LOGGER.error(conn.cause().getMessage(), conn.cause());
                    ctx.fail(400);
                }

                User user = ctx.user();
                if (user == null) {
                    LOGGER.error("Error, no user");
                    ctx.fail(401);
                }
                SQLUtil.query(conn.result(), "select a.perm from ROLES_PERMS a join USER_ROLES b on a.role = b.role where b.username = ?", new JsonArray().add(user.principal().getString("username")), rs -> {
                    JsonArray permissions = new JsonArray();
                    for (JsonObject permission : rs.getRows()) {
                        permissions.add(permission);
                    }
                    ctx.response().end(permissions.encode());
                });
            });
        };
    }

}
