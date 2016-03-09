package com.zhuojian.ct.handlers;

import com.zhuojian.ct.annotations.RouteHandler;
import com.zhuojian.ct.annotations.RouteMapping;
import com.zhuojian.ct.annotations.RouteMethod;
import com.zhuojian.ct.dao.ConsultationDao;
import com.zhuojian.ct.model.Consultation;
import com.zhuojian.ct.model.HttpCode;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wuhaitao on 2016/3/9.
 */
@RouteHandler("/api/consultation")
public class ConsultationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultationHandler.class);

    private ConsultationDao consultationDao;

    public ConsultationHandler(ConsultationDao consultationDao) {
        this.consultationDao = consultationDao;
    }

    @RouteMapping(method = RouteMethod.GET)
    public Handler<RoutingContext> getConsultations(){
        return  ctx -> {
            consultationDao.getConsultations(result -> {
                JsonArray cts = new JsonArray();
                if (result != null){
                    for (Consultation consultation : result){
                        JsonObject obj = new JsonObject();
                        obj.put("id", consultation.getId());
                        obj.put("created", consultation.getCreated());
                        obj.put("ctfile", consultation.getCtfile());
                        obj.put("record", consultation.getRecord());
                        obj.put("updated", consultation.getUpdated());
                        cts.add(obj);
                    }
                }
                HttpServerResponse response = ctx.response();
                response.setChunked(true);
                response.end(cts.encode());
            });
        };
    }

    @RouteMapping(method = RouteMethod.POST, value = "/add")
    public Handler<RoutingContext> addConsultation(){
        return ctx -> {
            JsonObject data = ctx.getBodyAsJson();
            String id = data.getString("id");
            String created = data.getString("created");
            String ctfile = data.getString("ctfile");
            String record = data.getString("record");
            String updated = data.getString("updated");
            consultationDao.addConsultation(new Consultation(id,created,ctfile,record,updated), responseMsg -> {
                if (responseMsg.getCode().getCode() == HttpCode.OK.getCode()){
                    ctx.response().end(responseMsg.getMsg());
                }
                else{
                    ctx.response().setStatusCode(responseMsg.getCode().getCode()).end(responseMsg.getMsg());
                }
            });
        };
    }

    @RouteMapping(method = RouteMethod.POST, value = "/update")
    public Handler<RoutingContext> updateConsultation(){
        return ctx -> {
            JsonObject data = ctx.getBodyAsJson();
            String id = data.getString("id");
            String ctfile = data.getString("ctfile");
            String record = data.getString("record");
            Consultation consultation = new Consultation(id);
            if (StringUtils.isNotEmpty(ctfile)){
                consultation.setCtfile(ctfile);
            }
            if (StringUtils.isNotEmpty(record)){
                consultation.setRecord(record);
            }
            consultationDao.addConsultation(consultation, responseMsg -> {
                if (responseMsg.getCode().getCode() == HttpCode.OK.getCode()){
                    ctx.response().end(responseMsg.getMsg());
                }
                else{
                    ctx.response().setStatusCode(responseMsg.getCode().getCode()).end(responseMsg.getMsg());
                }
            });
        };
    }
}
