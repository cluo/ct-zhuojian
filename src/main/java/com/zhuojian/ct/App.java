package com.zhuojian.ct;

import com.zhuojian.ct.verticle.LesionRecognitionServer;
import com.zhuojian.ct.verticle.Server;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;

/**
 * Created by wuhaitao on 2016/2/25.
 */
public class App {
    public static void main(String[] args) {
        VertxOptions options = new VertxOptions();
        // 设置工作线程
        options.setInternalBlockingPoolSize(20);
        options.setMetricsOptions(new DropwizardMetricsOptions().setEnabled(true));
        options.setMaxEventLoopExecuteTime(Long.MAX_VALUE);
        Vertx vertx = Vertx.vertx(options);
        vertx.deployVerticle(Server.class.getName());
        /*vertx.deployVerticle(LesionRecognitionServer.class.getName());*/

        /** 添加钩子函数,保证vertx的正常关闭 */
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            vertx.close();
            System.out.println("server stop success!");
        }));
    }
}
