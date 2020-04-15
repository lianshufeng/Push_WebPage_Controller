package top.dzurl.pushwebpage.core.util;

import lombok.SneakyThrows;

import java.nio.charset.Charset;
import java.security.MessageDigest;

public class HashUtil {


    public static String md5(String buffer) {
        return md5(buffer.getBytes(Charset.forName("UTF-8")));
    }



    @SneakyThrows
    public static String md5(byte[] bin) {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(bin);
        return BytesUtil.binToHex(md.digest());
    }


}
