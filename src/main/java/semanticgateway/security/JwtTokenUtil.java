package semanticgateway.security;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.servlet.http.Cookie;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {

	private static final long serialVersionUID = -2550185165626007488L;

	public static final long JWT_TOKEN_VALIDITY_A_MONTH = 60; // this is minutes
	public static final int COOKIE_VALIDITY_WEEK = 3600; // this is seconds
	private final String secret ="The bird fights its way out of the egg. The egg is the world. Who would be born must first destroy a world. The bird flies to God. That God's name is Abraxas.";



	/**
	 * Token creation
	 **/
	public String generateToken(String username, long validityMinutes) {
		return buildToken(username,validityMinutes);
	}

	public String generateToken(String username) {
		return buildToken(username,JWT_TOKEN_VALIDITY_A_MONTH);
	}

	private String buildToken(String subject, long validity) {
		Map<String, Object> claims = new HashMap<>();
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(Date.from(ZonedDateTime.now().plusMinutes(validity).toInstant()))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

	public Cookie createTokenCookie(String token, Boolean https, int duration) {
		Cookie cookie = new Cookie("jwt", token);
		cookie.setMaxAge(duration);
		cookie.setSecure(false);
		if(https)
			cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		return cookie;
	}

	public Cookie createTokenCookie(String username) {
		String token = generateToken(username);
		Cookie cookie = new Cookie("jwt", token);
		cookie.setMaxAge(COOKIE_VALIDITY_WEEK);
		cookie.setSecure(false);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		return cookie;
	}



	/**
	 * Token validation
	 **/

	public Boolean validateToken(String token, String originalUsername) {
		final String username = getUsernameFromToken(token);
		return (username.equals(originalUsername) && !isTokenExpired(token));
	}

	public Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}


	private Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
			final Claims claims = getAllClaimsFromToken(token);
			return claimsResolver.apply(claims);
		}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}


}