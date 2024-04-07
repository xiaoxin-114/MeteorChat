package com.meteor.chat.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.Optional;

@Slf4j
public class JWTUtils {

    /**
     * 进行加解密的密钥
     */
    @Value("${mallchat.jwt.secret}")
    private static String secret;

    private static String UID = "uid";
    private static String CREATE_TIME = "createTime";

    public static String createToken(Long uid){
        String token = JWT.create()
                .withClaim(UID, uid)
                .withClaim(CREATE_TIME, System.currentTimeMillis())
                .sign(Algorithm.HMAC256(secret));
        return token;
    }

    public static Long getUid(String token){
        return Optional.ofNullable(decodeToken(token))
                .map(map -> map.get(UID))
                .map(uid -> uid.asLong())
                .orElse(null);
    }

    public static Map<String, Claim> decodeToken(String token) {
        if (token == null){
            return null;
        }
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secret)).build();
            DecodedJWT jwt = jwtVerifier.verify(token);
            return jwt.getClaims();
        }catch (Exception e){
            log.error("decode token fail, {}", e.getMessage());
        }
        return null;
    }


}
