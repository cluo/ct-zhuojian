package com.zhuojian.ct.handlers;

import com.zhuojian.ct.annotations.RouteHandler;
import com.zhuojian.ct.annotations.RouteMapping;
import com.zhuojian.ct.annotations.RouteMethod;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Set;

/**
 * Created by wuhaitao on 2016/3/8.
 */
@RouteHandler("/upload")
public class UploadHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadHandler.class);

    @RouteMapping(method = RouteMethod.POST)
    public Handler<RoutingContext> upload() {
        return ctx -> {
            HttpServerRequest request = ctx.request();
            String username = request.getParam("username");
            System.out.println(username);
            Set<FileUpload> files = ctx.fileUploads();
            String filename = "";
            for (FileUpload file : files) {
                System.out.println(file.fileName());
                String path = file.uploadedFileName();
                File file1 = new File(path);
                System.out.println(file1.exists());
                System.out.println(path);
                LOGGER.debug("upload path : {}", path);
                filename = path.substring(path.lastIndexOf("\\") + 1);
            }

            JsonObject file = new JsonObject();
            file.put("filename", filename);
            ctx.response().end(file.encode());
        };
    }
}
