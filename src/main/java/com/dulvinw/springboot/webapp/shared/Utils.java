/*
 * Copyright (c) 2020, FinWhaleLabs Inc. (http://www.finwhalelabs.com) All Rights Reserved.
 *
 * Author : Dulvin Witharane
 * Date Created : 25/01/2020, 23:59
 *
 */

package com.dulvinw.springboot.webapp.shared;

import com.dulvinw.springboot.webapp.security.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

@Component
public class Utils {
    private final Random RANDOM = new SecureRandom();
    private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static boolean hasTokenExpired(String token) {
        boolean returnValue = true;
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SecurityConstants.getTokenSecret())
                    .parseClaimsJws(token).getBody();
            Date expirationDate = claims.getExpiration();
            Date today = new Date();

            returnValue = expirationDate.before(today);
        } catch (ExpiredJwtException e) {
            returnValue = true;
        }

        return returnValue;
    }

    public String generateUserId(int length) {
        return generateRandomString(length);
    }
    public String generateAddressId(int length) {
        return generateRandomString(length);
    }

    private String generateRandomString(int length) {
        StringBuilder returnValue = new StringBuilder(length);
        for(int i=0; i<length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return new String(returnValue);
    }

    public String generateEmailVerificationToken(String publicUserId) {
        String token = Jwts.builder()
                .setSubject(publicUserId)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                .compact();

        return token;
    }
}
