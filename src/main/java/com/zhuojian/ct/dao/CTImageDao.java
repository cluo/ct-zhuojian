package com.zhuojian.ct.dao;

import com.zhuojian.ct.annotations.HandlerDao;
import com.zhuojian.ct.model.CTImage;
import com.zhuojian.ct.model.HttpCode;
import com.zhuojian.ct.model.ResponseMsg;
import com.zhuojian.ct.utils.AppUtil;
import com.zhuojian.ct.utils.JDBCConnUtil;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by wuhaitao on 2016/3/10.
 */
@HandlerDao
public class CTImageDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(CTImageDao.class);

    protected JDBCClient sqlite = null;

    public CTImageDao(Vertx vertx) throws UnsupportedEncodingException {
        /*String db = URLDecoder.decode(CTImageDao.class.getClassLoader().getResource("webroot/db/zhuojian").getFile(), "UTF-8");*/
        /*String db = "E:/毕业论文/JavaProject/ct-zhuojian/src/main/resources/webroot/db/zhuojian";*/
        JsonObject sqliteConfig = new JsonObject()
                .put("url", AppUtil.configStr("db.url"))
                .put("driver_class", AppUtil.configStr("db.driver_class"));
        sqlite = JDBCClient.createShared(vertx, sqliteConfig, "ct");
    }

    public void getCTImageById(int id, Handler<CTImage> ctImageHandler){
        sqlite.getConnection(connection -> {
            if (connection.failed()){
                LOGGER.error("connection sqlite failed!");
                ctImageHandler.handle(null);
            }
            else{
                SQLConnection conn = connection.result();
                conn.query("select * from ct where id = "+id, result -> {
                    if (result.succeeded()){
                        List<JsonObject> objs = result.result().getRows();
                        CTImage ctImage = null;
                        if (objs != null && !objs.isEmpty()) {
                            for (JsonObject obj : objs) {
                                ctImage = new CTImage();
                                ctImage.setId(obj.getInteger("id"));
                                ctImage.setType(obj.getString("type"));
                                ctImage.setFile(obj.getString("file"));
                                ctImage.setDiagnosis(obj.getString("diagnosis"));
                                ctImage.setConsultationId(obj.getInteger("consultationId"));
                                ctImageHandler.handle(ctImage);
                                return;
                            }
                        }
                        else{
                            ctImageHandler.handle(null);
                        }
                    }
                    else{
                        LOGGER.error("get ctimage by id failed!");
                        ctImageHandler.handle(null);
                    }
                    JDBCConnUtil.close(conn);
                });
            }
        });
    }

    public void getCTImages(int consultationId, Handler<List<CTImage>> ctsHandler){
        sqlite.getConnection(connection -> {
            if (connection.failed()){
                LOGGER.error("connection sqlite failed!");
                ctsHandler.handle(null);
            }
            else{
                SQLConnection conn = connection.result();
                conn.query("select * from ct where consultationId = " + consultationId, result -> {
                    if (result.succeeded()) {
                        List<JsonObject> objs = result.result().getRows();
                        List<CTImage> cts = null;
                        CTImage ctImage = null;
                        if (objs != null && !objs.isEmpty()) {
                            cts = new ArrayList<>();
                            for (JsonObject obj : objs) {
                                ctImage = new CTImage();
                                ctImage.setId(obj.getInteger("id"));
                                ctImage.setType(obj.getString("type"));
                                ctImage.setFile(obj.getString("file"));
                                ctImage.setDiagnosis(obj.getString("diagnosis"));
                                ctImage.setConsultationId(obj.getInteger("consultationId"));
                                cts.add(ctImage);
                            }
                            ctsHandler.handle(cts);
                        } else {
                            ctsHandler.handle(null);
                        }
                    } else {
                        LOGGER.error("insert data failed!");
                        ctsHandler.handle(null);
                    }
                    JDBCConnUtil.close(conn);
                });
            }
        });
    }

    public void addCTImage(CTImage ctImage, Handler<ResponseMsg> responseMsgHandler){
        sqlite.getConnection(connection -> {
            if (connection.failed()){
               /*System.out.println("connection sqlite failed!");*/
                LOGGER.error("connection sqlite failed!");
                responseMsgHandler.handle(new ResponseMsg(HttpCode.INTERNAL_SERVER_ERROR, "sqlite connected failed!"));
                return;
            }
            SQLConnection conn = connection.result();
            JsonArray params = new JsonArray().add(ctImage.getType()).add(ctImage.getFile()).add(ctImage.getDiagnosis()).add(ctImage.getConsultationId());
            String sql = "insert into ct(type,file,diagnosis,consultationId) values(?,?,?,?)";
            conn.updateWithParams(sql, params, insertResult -> {
                if (insertResult.succeeded()){
                    LOGGER.info("insert data success!");
                    responseMsgHandler.handle(new ResponseMsg(HttpCode.OK, "insert ct success!"));
                }
                else{
                    LOGGER.error("insert data failed!");
                    responseMsgHandler.handle(new ResponseMsg(HttpCode.INTERNAL_SERVER_ERROR, "sqlite insert data failed!"));
                }
            });
        });
    }

    public void addCTImages(List<CTImage> ctImages, Handler<ResponseMsg> responseMsgHandler){
        sqlite.getConnection(connection -> {
            if (connection.failed()){
                LOGGER.error("connection sqlite failed!");
                responseMsgHandler.handle(new ResponseMsg(HttpCode.INTERNAL_SERVER_ERROR, "sqlite connected failed!"));
                return;
            }
            ResponseMsg responseMsg = new ResponseMsg(HttpCode.OK, "upload ct files success!");
            LOGGER.info("start receive file");
            SQLConnection conn = connection.result();
            for (CTImage ctImage:ctImages) {
                JsonArray params = new JsonArray().add(ctImage.getType()).add(ctImage.getFile()).add(ctImage.getDiagnosis()).add(ctImage.getConsultationId());
                String sql = "insert into ct(type,file,diagnosis,consultationId) values(?,?,?,?)";
                conn.updateWithParams(sql, params, insertResult -> {
                    /*JDBCConnUtil.close(conn);*/
                    LOGGER.info("receive file");
                    if (insertResult.failed()) {
                        LOGGER.info("insert ct {} failed!", ctImage.getFile());
                        /*responseMsg.setCode(HttpCode.INTERNAL_SERVER_ERROR);
                        responseMsg.setMsg("upload ct files occurs sqlite exception!");*/
                    }
                });
            }
            LOGGER.info("receive file finished");
            responseMsgHandler.handle(responseMsg);
        });
    }

    public void updateCTImage(CTImage ctImage, Handler<ResponseMsg> responseMsgHandler){
        sqlite.getConnection(connection -> {
            if (connection.failed()){
               /*System.out.println("connection sqlite failed!");*/
                LOGGER.error("connection sqlite failed!");
                responseMsgHandler.handle(new ResponseMsg(HttpCode.INTERNAL_SERVER_ERROR, "sqlite connected failed!"));
                return;
            }
            SQLConnection conn = connection.result();
            JsonArray params = new JsonArray().add(ctImage.getDiagnosis()).add(ctImage.getId());
            String sql = "update ct set diagnosis = ? where id = ?";
            conn.updateWithParams(sql, params, insertResult -> {
                if (insertResult.succeeded()) {
                    LOGGER.info("update ct success!");
                    responseMsgHandler.handle(new ResponseMsg(HttpCode.OK, "update ct success!"));
                } else {
                    LOGGER.error("update ct failed!");
                    responseMsgHandler.handle(new ResponseMsg(HttpCode.INTERNAL_SERVER_ERROR, "sqlite update ct failed!"));
                }
            });
        });
    }
}
