package com.zhuojian.ct.handlers;

import com.zhuojian.ct.annotations.RouteHandler;
import com.zhuojian.ct.annotations.RouteMapping;
import com.zhuojian.ct.annotations.RouteMethod;
import com.zhuojian.ct.dao.CTImageDao;
import com.zhuojian.ct.model.CTImage;
import com.zhuojian.ct.model.HttpCode;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wuhaitao on 2016/3/10.
 */
@RouteHandler("/api/consultation")
public class CTImageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CTImageHandler.class);

    private CTImageDao ctImageDao;

    public CTImageHandler(CTImageDao ctImageDao) {
        this.ctImageDao = ctImageDao;
    }

    @RouteMapping(method = RouteMethod.GET, value = "/:id")
    public Handler<RoutingContext> getCTImages(){
        return  ctx -> {
            int id = Integer.parseInt(ctx.request().getParam("id"));
            ctImageDao.getCTImages(id, result -> {
                JsonArray cts = new JsonArray();
                if (result != null) {
                    for (CTImage ctImage : result) {
                        JsonObject obj = new JsonObject();
                        obj.put("id", ctImage.getId());
                        obj.put("type", ctImage.getType());
                        obj.put("file", ctImage.getFile());
                        obj.put("diagnosis", ctImage.getDiagnosis());
                        obj.put("consultationId", ctImage.getConsultationId());
                        cts.add(obj);
                    }
                }
                HttpServerResponse response = ctx.response();
                response.setChunked(true);
                response.end(cts.encode());
            });
        };
    }

    @RouteMapping(method = RouteMethod.GET, value = "/deleteCT/:id")
    public Handler<RoutingContext> deleteCTImageById(){
        return  ctx -> {
            int id = Integer.parseInt(ctx.request().getParam("id"));
            ctImageDao.deleteCTImageById(id, result -> {
                HttpServerResponse response = ctx.response();
                response.setChunked(true);
                response.setStatusCode(result.getCode().getCode()).end(result.getContent());
            });
        };
    }

    @RouteMapping(method = RouteMethod.GET, value = "/record/:id")
    public Handler<RoutingContext> getCTImage(){
        return  ctx -> {
            int id = Integer.parseInt(ctx.request().getParam("id"));
            ctImageDao.getCTImageById(id, ctImage -> {
                JsonObject obj = new JsonObject();
                if (ctImage != null) {
                    obj.put("id", ctImage.getId());
                    obj.put("type", ctImage.getType());
                    obj.put("file", ctImage.getFile());
                    obj.put("diagnosis", ctImage.getDiagnosis());

                }
                HttpServerResponse response = ctx.response();
                response.setChunked(true);
                response.end(obj.encode());
            });
        };
    }

    @RouteMapping(method = RouteMethod.POST, value = "/record/updateDiagnosis")
    public Handler<RoutingContext> updateDiagnosis(){
        return  ctx -> {
            JsonObject data = ctx.getBodyAsJson();
            int id = data.getInteger("id");
            String diagnosis = data.getString("diagnosis");
            CTImage ctImage = new CTImage();
            ctImage.setId(id);
            ctImage.setDiagnosis(diagnosis);
            ctImageDao.updateCTImage(ctImage, responseMsg -> {
                HttpServerResponse response = ctx.response();
                response.setChunked(true);
                response.setStatusCode(responseMsg.getCode().getCode()).end(responseMsg.getContent());
            });
        };
    }

    @RouteMapping(method = RouteMethod.POST, value = "/ct/page")
    public Handler<RoutingContext> getCTImagesByPage(){
        return  ctx -> {
            JsonObject data = ctx.getBodyAsJson();
            int id = data.getInteger("id");
            int pageIndex = data.getInteger("pageIndex");
            int pageSize = data.getInteger("pageSize");
            ctImageDao.getCTImagesByPage(id, pageIndex, pageSize, result -> {
                HttpServerResponse response = ctx.response();
                response.setChunked(true);
                if (result != null) {
                    /*int count = result.getInteger("count");
                    JsonArray cts = new JsonArray();
                    List<JsonObject> array = result.getJsonArray("ct").getList();
                    for (JsonObject obj : array) {
                        cts.add(obj);
                    }*/
                    response.end(result.encode());
                }
                else{
                    response.setStatusCode(HttpCode.NULL_CONTENT.getCode()).end();
                }
            });
        };
    }
}

