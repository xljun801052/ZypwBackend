package com.zypw.zypwcommon.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * jwt(json web token)工具类
 */
public class JWTUtils {
    //过期时间
    private static final long EXPIRE_TIME = 15 * 60 * 1000;
    //私钥
    private static final String TOKEN_SECRET = "privateKey";

    /**
     * 生成签名，15分钟过期
     * @param **username**
     * @param **password**
     * @return
     */
    public static String sign(Long userId) {
        try {
            // 设置过期时间
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
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
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (Exception e) {
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
    public static Long verify(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            // TODO: 2021/2/7 有个BUG：这里access_token过了一会儿解析出来就是过期的：com.auth0.jwt.exceptions.TokenExpiredException: The Token has expired on Sun Feb 07 20:41:52 CST 2021.
            // TODO: 2021/2/7 说明这里的工具类实际上是包含了过期设置，我们应该如何调整auth0的默认过期时间？？？
            DecodedJWT jwt = verifier.verify(token);
            Long userId = jwt.getClaim("userId").asLong();
            return userId;
        } catch (Exception e){
            return 0L;
        }
    }

    /*public static void main(String[] args) {
        Long verify = verify("eyJUeXBlIjoiSnd0IiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJleHAiOjE2MTI3MDE3MTIsInVzZXJJZCI6MX0.EuNNxnEYqQ4Xg5i2lGOnZw_-TIRdXOjbB09_l_vDwhw");
        System.out.println("verify = " + verify);
    }*/
}
