package com.zhuojian.ct.handlers;

import com.zhuojian.ct.annotations.RouteHandler;
import com.zhuojian.ct.annotations.RouteMapping;
import com.zhuojian.ct.annotations.RouteMethod;
import com.zhuojian.ct.dao.CTImageDao;
import com.zhuojian.ct.dao.ConsultationDao;
import com.zhuojian.ct.model.CTImage;
import com.zhuojian.ct.model.HttpCode;
import com.zhuojian.ct.utils.AppUtil;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Set;

/**
 * Created by wuhaitao on 2016/3/8.
 */
@RouteHandler("/upload")
public class UploadHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadHandler.class);

    private CTImageDao ctImageDao;

    public UploadHandler(CTImageDao ctImageDao) {
        this.ctImageDao = ctImageDao;
    }

    @RouteMapping(method = RouteMethod.POST)
    public Handler<RoutingContext> upload() {
        return ctx -> {
            HttpServerRequest request = ctx.request();
            int id = Integer.parseInt(request.getParam("id"));
            int type = Integer.parseInt(request.getParam("type"));
            System.out.println(id);
            System.out.println(type);
            Set<FileUpload> files = ctx.fileUploads();
            for (FileUpload file : files) {
                System.out.println(file.fileName());
                String path = file.uploadedFileName();
                System.out.println(path);
                String img = path.substring(path.indexOf(AppUtil.configStr("upload.path")));
                System.out.println(img);
                CTImage ctImage = new CTImage();
                ctImage.setType(type == 1 ? "肝脏" : "肺部");
                ctImage.setFile(img);
                ctImage.setDiagnosis("");
                ctImage.setConsultationId(id);
                File file1 = new File(path);
                System.out.println(file1.exists());
                LOGGER.info("upload path : {}", path);
                ctImageDao.addCTImage(ctImage, responseMsg -> {
                    if (responseMsg.getCode().getCode() == HttpCode.OK.getCode()) {
                        String filename = path.substring(path.lastIndexOf("\\") + 1);
                        ctx.response().end(new JsonObject().put("filename", filename).encode());
                    } else {
                        ctx.response().setStatusCode(responseMsg.getCode().getCode()).end(responseMsg.getMsg());
                    }
                });
                break;
            }
        };
    }

    @RouteMapping(method = RouteMethod.GET, value = "/:image")
    public Handler<RoutingContext> getCTImage(){
        return  ctx -> {
            String image = ctx.request().getParam("image");
            System.out.println(image);
            HttpServerResponse response = ctx.response();
            response.setChunked(true);
            response.sendFile(AppUtil.getUploadDir() + File.separator + image);
        };
    }

}
