package com.zhuojian.ct.handlers;

import com.zhuojian.ct.algorithm.cnn.Predict.CNNPredict;
import com.zhuojian.ct.annotations.RouteHandler;
import com.zhuojian.ct.annotations.RouteMapping;
import com.zhuojian.ct.annotations.RouteMethod;
import com.zhuojian.ct.dao.ConsultationDao;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by root on 16-3-21.
 */
@RouteHandler("/api/lung")
public class LungRecogHander {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultationHandler.class);

    private ConsultationDao consultationDao;

    private CNNPredict cnnPredict = new CNNPredict();

    public LungRecogHander(ConsultationDao consultationDao) {
        this.consultationDao = consultationDao;
    }

    @RouteMapping(value = "/:id", method = RouteMethod.GET)
    public Handler<RoutingContext> ctimages() {
        return ctx -> {
            String id = ctx.request().getParam("id");
            LOGGER.debug("Start get ct file by id "+id);

            //通过id找到文件名xxx
            String fileName = "7dd88ac4-67d5-40ae-93b1-ee552f7baf58";
            try {
                int val = cnnPredict.getPred(fileName);
                JsonArray cts = new JsonArray();
                JsonObject obj = new JsonObject();
                obj.put("desc", val);
                cts.add(obj);
                ctx.response().end(cts.encode());
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error(e.getMessage());
                ctx.fail(400);
            }
        };
    }
}
