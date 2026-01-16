package com.yellow.petshop;

import com.yellow.petshop.model.user.User;
import io.jsonwebtoken.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.UUID;

@SpringBootTest
class PetserverApplicationTests {
    private long expirationTime = 1000 * 60 * 60 * 24 * 7;
    private String secret = "secret";

    @Test
    public void jwt(User user){
        JwtBuilder jwtBuilder = Jwts.builder();
        String jwttoken = jwtBuilder
                //Header
                .setHeaderParam("typ","JWT")
                .setHeaderParam("alg","HS256")
                //Payload
                .setSubject(user.getId().toString())
                .claim("username", user.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .setId(UUID.randomUUID().toString())
                //Signature
                .signWith(SignatureAlgorithm.ES256, secret)
                .compact();
        System.out.println(jwttoken);
    }

    @Test
    public void parse(String token){
        JwtParser jwtParser = Jwts.parser();
        Jws<Claims> claims = jwtParser.setSigningKey(secret).parseClaimsJws(token);
        Claims body = claims.getBody();
        System.out.println(body.get("username"));
    }
}
