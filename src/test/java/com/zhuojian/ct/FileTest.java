package com.zhuojian.ct;

import com.zhuojian.ct.utils.AppUtil;
import org.junit.Test;

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
    //@Test
    public void test() throws IOException {
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
    @Test
    public void testFilePath(){
        String file = AppUtil.getUploadDir()+File.separator+"614d72e-2ba0-40e4-aa09-2983da86dd31";
        System.out.println(file);
        File image = new File("upload/b614d72e-2ba0-40e4-aa09-2983da86dd31");
        /*String[] files = image.list();
        System.out.println(files.length);*/
        System.out.println(image.exists());
    }
}
