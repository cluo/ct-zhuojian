package com.zhuojian.ct;

import com.zhuojian.ct.utils.AppUtil;
import org.junit.Test;

/**
 * Created by wuhaitao on 2016/3/21.
 */
public class AuthTest {
    @Test
    public void test(){
        String salt = AppUtil.computeHash("test", null, "SHA-512");
        String passwordHash = AppUtil.computeHash("test", salt, "SHA-512");
        System.out.println("password:"+passwordHash);
        System.out.println("password_salt:"+salt);
    }
}
