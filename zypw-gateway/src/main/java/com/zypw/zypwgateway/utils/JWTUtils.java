package com.zypw.zypwgateway.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * jwt(json web token)工具类
 *
 * 参考文档：https://blog.csdn.net/a1036645146/article/details/109546416
 * */
public class JWTUtils {
    // token 签名的秘钥，可设置到配置文件中
    private static final String SECRET_KEY = "secretKey:123456";
    // token过期时间
    public static final long TOKEN_EXPIRE_TIME = 7200 * 1000;

    /**
     * 生成jwt
     */
    public String createJwt(String userId){
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        //设置头信息
        HashMap<String, Object> header = new HashMap<>(2);
        header.put("typ", "JWT");
        header.put("alg", "HS256");
        // 生成 token：头部+载荷+签名
        return JWT.create().withHeader(header)
                .withClaim(RequestKeyConstants.USER_ID,userId)
                .withExpiresAt(new Date(System.currentTimeMillis()+TOKEN_EXPIRE_TIME)).sign(algorithm);
    }

    /**
     * 解析jwt
     */
    public Map<String, Claim> parseJwt(String token) {
        Map<String, Claim> claims = null;
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            claims = jwt.getClaims();
            return claims;
        } catch (Exception e) {
            return null;
        }
    }
}
