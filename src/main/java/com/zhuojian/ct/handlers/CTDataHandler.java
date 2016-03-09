package com.zhuojian.ct.handlers;

import com.zhuojian.ct.annotations.RouteHandler;
import com.zhuojian.ct.annotations.RouteMapping;
import com.zhuojian.ct.annotations.RouteMethod;
import com.zhuojian.ct.utils.FileUtil;
import com.zhuojian.ct.utils.ImageUtil;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by wuhaitao on 2016/2/25.
 */
@RouteHandler("/api/ct")
public class CTDataHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CTDataHandler.class);
    private CloseableHttpClient httpClient = HttpClients.createDefault();

    @RouteMapping(method = RouteMethod.GET)
    public Handler<RoutingContext> list() {
        return ctx -> {
            LOGGER.debug("Start get list");

            JsonArray cts = new JsonArray();
            try {
                String[] ctlist = FileUtil.getCTNumbers();
                for (String ct : ctlist){
                    JsonObject obj = new JsonObject();
                    obj.put("id", ct);
                    obj.put("desc", "desc");
                    obj.put("date", "2015-02-25");
                    cts.add(obj);
                }
                ctx.response().end(cts.encode());
            } catch (UnsupportedEncodingException e) {
                LOGGER.error(e.getMessage());
                ctx.fail(400);
            }
        };
    }

    @RouteMapping(value = "/:id", method = RouteMethod.GET)
    public Handler<RoutingContext> ctimages() {
        return ctx -> {
            String id = ctx.request().getParam("id");
            LOGGER.debug("Start get ct list by id "+id);
            JsonArray cts = new JsonArray();
            try {
                String[] ctlist = FileUtil.findCTById(id);
                for (String ct : ctlist){
                    JsonObject obj = new JsonObject();
                    obj.put("desc", "desc");
                    obj.put("src", ct);
                    cts.add(obj);
                }
                ctx.response().end(cts.encode());
            } catch (UnsupportedEncodingException e) {
                LOGGER.error(e.getMessage());
                ctx.fail(400);
            }
        };
    }

    @RouteMapping(value = "/predict", method = RouteMethod.POST)
    public Handler<RoutingContext> predictLesionType() {
        return ctx -> {
            JsonObject data = ctx.getBodyAsJson();
            String image = data.getString("image");
            int x1 = data.getInteger("x1");
            int y1 = data.getInteger("y1");
            int x2 = data.getInteger("x2");
            int y2 = data.getInteger("y2");
            URI uri = null;
            try {
                uri = new URIBuilder().setHost("http://127.0.0.1:8081/predict")
                        .setPort(8081)
                        .setParameter("image", image)
                        .setParameter("x1", String.valueOf(x1))
                        .setParameter("y1", String.valueOf(y1))
                        .setParameter("x2", String.valueOf(x2))
                        .setParameter("y2", String.valueOf(y2))
                        .build();
                StringBuilder sb = new StringBuilder("http://127.0.0.1:8081/predict?image=").append(image.replace("\\","/"))
                        .append("&x1=").append(x1)
                        .append("&y1=").append(y1)
                        .append("&x2=").append(x2)
                        .append("&y2=").append(y2);
                HttpGet get = new HttpGet(sb.toString());
                CloseableHttpResponse response = httpClient.execute(get);
                int statusCode = response.getStatusLine().getStatusCode();
                if (200 == statusCode) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String res = EntityUtils.toString(entity,"utf-8");
                        JsonObject result = new JsonObject();
                        result.put("lesion", res);
                        ctx.response().end(result.encode());
                    }
                } else {
                    ctx.response().setStatusCode(500).end();
                }

            } catch (URISyntaxException e) {
                e.printStackTrace();
                ctx.response().setStatusCode(500).end();
            } catch (IOException e) {
                e.printStackTrace();
                ctx.response().setStatusCode(500).end();
            }
        };
    }
}
