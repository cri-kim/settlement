package com.pilot.common.util;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Value;

import com.pilot.api.user.domain.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;

public class TokenUtils {
	
	private final static String securityKey = "Th!s_!s_Pil0t_Pr0ject";
	
	private final static String expiredMinutes ="10";
	
	public static String generateJwtToken(User user) {
		
		JwtBuilder builder = Jwts.builder()
				.setSubject(user.getUserId())
				.setHeader(createHeader())
				.setClaims(createClaims(user))
				.setExpiration(createExpiredDate())
				.signWith(io.jsonwebtoken.SignatureAlgorithm.HS512
						, createSigningKey());
		
		return builder.compact();
	}
	
	public static boolean isValidToken(String token) {
		try {
			Claims claims = getClaimsFromToken(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	public static String getTokenFromHeader(String header) {
		return header.split(" ")[1];
	}
	public static Map<String, Object> createHeader() {
		Map<String, Object> header = new HashMap<String, Object>();
		
		header.put("regDate", LocalDateTime.now().toString());
		return header;
	}
	private static Map<String, Object> createClaims(User user) {
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("userId", user.getUserId());
		claims.put("role", user.getRole());
		return claims;
	}
	private static Date createExpiredDate() {
		LocalDateTime expiredDtm = LocalDateTime.now().plusMinutes(Long.parseLong(expiredMinutes));
		return Date.from(expiredDtm.atZone(ZoneId.systemDefault()).toInstant());
	}
	private static Claims getClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(securityKey))
				.parseClaimsJws(token).getBody();
	}
	private static String getUserIdFromToken(String token) {
		Claims claims = getClaimsFromToken(token);
		return (String)claims.get("userId");
	}
	private static String getRoleFromToken(String token) {
		Claims claims = getClaimsFromToken(token);
		return (String)claims.get("role");
	}
	private static Key createSigningKey() {
		byte[] securityBytes = DatatypeConverter.parseBase64Binary(securityKey);
		return new SecretKeySpec(securityBytes, io.jsonwebtoken.SignatureAlgorithm.HS512.getJcaName());
	}
}
