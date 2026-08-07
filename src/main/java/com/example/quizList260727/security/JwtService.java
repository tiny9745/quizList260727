package com.example.quizList260727.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * 負責 JWT Access Token 的產生、解析與驗證。
 *
 * 使用 jjwt 0.12.x 的新版 API：
 * - Jwts.parserBuilder() 在 0.12.0 之後已移除，改用 Jwts.parser()（本身就是 builder，呼叫 .build() 即可）
 * - setSigningKey(...) 改為 verifyWith(...)
 * - parseClaimsJws(...) 改為 parseSignedClaims(...)
 * - getBody() 改為 getPayload()
 * - signWith(SecretKey) 會依 key 型別自動推斷演算法，不用再另外傳 SignatureAlgorithm
 *
 * 需在 application.properties 加入：
 *   jwt.secret=<Base64 編碼的隨機密鑰，至少 256 bits>
 *   jwt.expiration=1800000   （選填，毫秒，預設 30 分鐘）
 *
 * 可用以下指令快速產生一組密鑰（macOS/Linux）：
 *   openssl rand -base64 32
 */
@Component
public class JwtService {

	@Value("${jwt.secret}")
	private String secretKey;

	/** Access Token 有效時間 (毫秒)，預設 30 分鐘 */
	@Value("${jwt.expiration:1800000}")
	private long jwtExpiration;

	/** 產生 Access Token，以 email 作為 subject，並帶入權限 claim */
	public String generateToken(String email, String permissions) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("permissions", permissions);
		return buildToken(claims, email, jwtExpiration);
	}

	private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
		Date now = new Date();
		return Jwts.builder()
				.claims(extraClaims)
				.subject(subject)
				.issuedAt(now)
				.expiration(new Date(now.getTime() + expiration))
				.signWith(getSignInKey())
				.compact();
	}

	/** 從 token 取出 email (subject) */
	public String extractEmail(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(getSignInKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	/** 驗證 token 是否合法，且 subject 與傳入的使用者一致、尚未過期 */
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String email = extractEmail(token);
		return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		return extractClaim(token, Claims::getExpiration).before(new Date());
	}

	private SecretKey getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}