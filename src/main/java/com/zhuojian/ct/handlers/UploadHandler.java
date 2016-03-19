package com.zhuojian.ct.handlers;

import com.zhuojian.ct.annotations.RouteHandler;
import com.zhuojian.ct.annotations.RouteMapping;
import com.zhuojian.ct.annotations.RouteMethod;
import com.zhuojian.ct.dao.CTImageDao;
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
import java.util.ArrayList;
import java.util.List;
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

    /**
     * 文件上传
     * @return
     */
    @RouteMapping(method = RouteMethod.POST)
    public Handler<RoutingContext> upload() {
        return ctx -> {
            HttpServerRequest request = ctx.request();
            int id = Integer.parseInt(request.getParam("id"));
            int type = Integer.parseInt(request.getParam("type"));
            Set<FileUpload> files = ctx.fileUploads();
            for (FileUpload file : files) {
                String path = file.uploadedFileName();
                String img = path.substring(path.indexOf(AppUtil.configStr("upload.path"))+7);
                CTImage ctImage = new CTImage();
                ctImage.setType(type == 1 ? "肝脏" : "肺部");
                ctImage.setFile(img);
                ctImage.setDiagnosis("");
                ctImage.setConsultationId(id);
                LOGGER.info("upload path : {}", path);
                ctImageDao.addCTImage(ctImage, responseMsg -> {
                    if (responseMsg.getCode().getCode() == HttpCode.OK.getCode()) {
                        ctx.response().end(new JsonObject().put("filename", img).encode());
                    } else {
                        ctx.response().setStatusCode(responseMsg.getCode().getCode()).end(responseMsg.getMsg());
                    }
                });
                break;
            }
        };
    }

    /**
     * 文件上传
     * @return
     */
    @RouteMapping(method = RouteMethod.POST, value = "/files")
    public Handler<RoutingContext> uploadFiles() {
        return ctx -> {
            HttpServerRequest request = ctx.request();
            int id = Integer.parseInt(request.getParam("id"));
            int type = Integer.parseInt(request.getParam("type"));
            Set<FileUpload> files = ctx.fileUploads();
            List<CTImage> ctImages = new ArrayList<>(files.size());
            for (FileUpload file : files) {
                String path = file.uploadedFileName();
                String img = path.substring(path.indexOf(AppUtil.configStr("upload.path"))+7);
                CTImage ctImage = new CTImage();
                ctImage.setType(type == 1 ? "肝脏" : "肺部");
                ctImage.setFile(img);
                ctImage.setDiagnosis("");
                ctImage.setConsultationId(id);
                LOGGER.info("upload path : {}", path);
                ctImages.add(ctImage);
            }
            ctImageDao.addCTImages(ctImages, responseMsg -> {
                ctx.response().setStatusCode(responseMsg.getCode().getCode()).end(responseMsg.getMsg());
            });
        };
    }

    /**
     * 获取上传的图片在前端显示，这里的图片不包含在静态资源里，而是通过web请求获取
     * @return
     */
    @RouteMapping(method = RouteMethod.GET, value = "/:image")
    public Handler<RoutingContext> getCTImage(){
        return  ctx -> {
            String image = ctx.request().getParam("image");
            HttpServerResponse response = ctx.response();
            response.setChunked(true);
            response.sendFile(AppUtil.getUploadDir() + File.separator + image);
        };
    }

}
