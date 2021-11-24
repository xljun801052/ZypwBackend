package com.zypw.zypwcommon.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * jwt(json web token)工具类
 */
@Slf4j
public class JWTUtils {
    //duration
    // TODO: 2021/11/20 change back to 15 mins when online.[unit:millisecond]
    private static final long DURATION = 7 * 24 * 60 * 60 * 1000; //default 7 days

    // TODO: 2021/11/20 extract the private key and put it into the configuration file.
    private static final String TOKEN_SECRET = "privateKey";

    /**
     *
     * @param **username**
     * @param **password**
     * @return
     */
    public static String sign(Integer userId) {
        try {
            // 设置过期时间
            Date date = new Date(System.currentTimeMillis() + DURATION);
            // 私钥和加密算法
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            // 设置头部信息
            Map<String, Object> header = new HashMap<>(2);
            header.put("Type", "Jwt");
            header.put("alg", "HS256");
            // 返回token字符串
            // TODO: 2021/9/12 token needs to be put in more info! not only a userId!
            return JWT.create()
                    .withHeader(header)
                    .withClaim("userId", userId)
                    .withClaim("authorities", Arrays.asList("ROLE_admin","ROLE_super_user","ACTION_all"))
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (Exception e) {
            log.error("Exception occurs when generate JWT, the exception info is :{}", e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 检验token是否正确
     *
     * @param **token**
     * @return
     */
    public static Integer verify(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            // TODO: 2021/2/7 有个BUG：这里access_token过了一会儿解析出来就是过期的：com.auth0.jwt.exceptions.TokenExpiredException: The Token has expired on Sun Feb 07 20:41:52 CST 2021.
            // TODO: 2021/2/7 说明这里的工具类实际上是包含了过期设置，我们应该如何调整auth0的默认过期时间？？？
            DecodedJWT jwt = verifier.verify(token);
            Integer userId = jwt.getClaim("userId").asInt();
            return userId;
        } catch (Exception e) {
            log.error("Exception occurs when try to parse the JWT:[{}]. The exception is:[{}]", token, e);
            return -1;
        }
    }

    /*public static void main(String[] args) {
        Long verify = verify("eyJUeXBlIjoiSnd0IiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJleHAiOjE2MTI3MDE3MTIsInVzZXJJZCI6MX0.EuNNxnEYqQ4Xg5i2lGOnZw_-TIRdXOjbB09_l_vDwhw");
        System.out.println("verify = " + verify);
    }*/
}
