package com.ecommerce.shopping.serviceimpl;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ecommerce.shopping.entity.RefreshToken;
import com.ecommerce.shopping.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
@Service
public class JWtService  {

    @Value("${application.jwt.secret}")
	private String secret;
    
    private static final String ROLE="role";
    
    private RefreshTokenRepository refreshTokenRepository;
    
    
	
//	private String secret="EovFLZbS5XEYTBY0ELlkd3ZmCcBfg2KPJeWos7CJh5eOt2aSybdgn7jk3LdfFoDJoZARlZmI4XlpmuK3TsRjbQ==";

	public JWtService(RefreshTokenRepository refreshTokenRepository) {
		super();
		this.refreshTokenRepository = refreshTokenRepository;
	}

	public  String createJwtToken(String username,long expireDurationInMilis,String role) {
		return 	 Jwts.builder()
				.setClaims(Map.of(ROLE,role))
				.setSubject(username)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis()+ expireDurationInMilis))
				.signWith(getSignatureKey(), SignatureAlgorithm.HS512).compact();

	}

	private  Key getSignatureKey() {
		return  Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

	}
	
	private Claims parseJwtToken(String token) {

		JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(getSignatureKey()).build();
		return jwtParser.parseClaimsJws( token).getBody();	
	}

	public String extractUsername(String token)
	{
		String username = parseJwtToken(token).getSubject();
		return username;
	} 

	public Date extractIssuedAt(String token) {

		return  parseJwtToken(token).getIssuedAt();

	}
	
	public Date extarctExpiredDate(String token) {
	   return 	parseJwtToken(token).getExpiration();
	}
	
	public String extractUserRole(String token) {
		return parseJwtToken(token).get(ROLE,String.class);
	}

	public boolean isTokenValid(String token ) {
		try {
		RefreshToken refreshToken = refreshTokenRepository.findByToken(token);
	return 	parseJwtToken(token).getExpiration()
	                            .after(new Date())&& refreshToken!=null && !refreshToken.isBlocked();
		}
		catch(Exception e) {
			return false;
		}	
	}
}
