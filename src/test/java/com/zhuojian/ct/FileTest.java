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
        URL url = FileTest.class.getClassLoader().getResource("webroot/ctimage/1/1/IMG-0001-00001.jpg");
        String img = URLDecoder.decode(url.getFile(), "UTF-8");
        System.out.println(img);
        File file = new File(img);
        System.out.println(file.exists());
        BufferedImage bi = ImageIO.read(file);
    }
}
