package com.zhuojian.ct;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Created by wuhaitao on 2016/3/7.
 */
public class FileTest {
    public static void main(String[] args) throws IOException {
        /*URL url = FileTest.class.getClassLoader().getResource("webroot/db/zhuojian");
        String img = URLDecoder.decode(url.getFile(), "UTF-8");
        System.out.println(img);
        File file = new File(img);
        System.out.println(file.exists());*/
        URL url = FileTest.class.getClassLoader().getResource("webroot");
        String path = URLDecoder.decode(url.getPath(), "UTF-8");
        System.out.println(path);
        File file = new File(path);
        System.out.println(file.exists());
    }
}
