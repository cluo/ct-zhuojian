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
            String fileName = ctx.request().getParam("id");
            LOGGER.debug("Start get ct file by id " + fileName);

            try {
                double[] val = cnnPredict.getPred(fileName);
                JsonObject obj = new JsonObject();
                String desc = "";
                if (val[0] > val[1] && val[0] > val[2]) {
                    desc = "大结节";
                } else if (val[1] >val[0] && val[1] > val[2]) {
                    desc = "小结节";
                } else {
                    desc = "正常";
                }
                obj.put("desc", desc);
                obj.put("djj", val[0]);
                obj.put("xjj", val[1]);
                obj.put("normal", val[2]);
                obj.put("file", fileName);
                ctx.response().end(obj.encode());
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error(e.getMessage());
                ctx.fail(400);
            }
        };
    }
}
