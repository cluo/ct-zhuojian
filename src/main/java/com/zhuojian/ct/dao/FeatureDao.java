package com.zhuojian.ct.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhuojian.ct.annotations.HandlerDao;
import com.zhuojian.ct.utils.AppUtil;
import com.zhuojian.ct.utils.Constants;
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
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wuhaitao on 2016/3/12.
 */
@HandlerDao
public class FeatureDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(CTImageDao.class);

    protected JDBCClient sqlite = null;
    private Map<String,Integer> lesionType;

    public FeatureDao(Vertx vertx) throws UnsupportedEncodingException {
        JsonObject sqliteConfig = new JsonObject()
                .put("url", AppUtil.configStr("db.url"))
                .put("driver_class", AppUtil.configStr("db.driver_class"));
        sqlite = JDBCClient.createShared(vertx, sqliteConfig, "feature");
        try {
            URL url = getClass().getClassLoader().getResource(Constants.LESION);
            LOGGER.debug("Initialize lesion type from path : {}", url);
            ObjectMapper mapper = new ObjectMapper();
            JsonObject lesion = new JsonObject((Map<String, Object>) mapper.readValue(url, Map.class));
            lesionType = new HashMap<>(5);
            for (int i = 1; i <= 5; i++) {
                lesionType.put(lesion.getString(String.valueOf(i)),i);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    public void addFeature(double[] feature, String label, Handler<String> handler){
        sqlite.getConnection(connection -> {
            if (connection.failed()){
                LOGGER.error("connection sqlite failed!");
                handler.handle("connection sqlite failed!");
            }
            else{
                SQLConnection conn = connection.result();
                JsonArray params = new JsonArray();
                for (int i=0;i<26;i++){
                    params.add(feature[i]);
                }
                params.add(lesionType.get(label));
                conn.updateWithParams("insert into feature(f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f13,f14,f15,f16,f17,f18,f19,f20,f21,f22,f23,f24,f25,f26,label) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",params,result -> {
                    if (result.succeeded()){
                        handler.handle("success");
                    }
                    else{
                        handler.handle(result.cause().getMessage());
                    }
                    JDBCConnUtil.close(conn);
                });
            }
        });
    }
}
