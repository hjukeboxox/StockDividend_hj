package com.dayone.security;

import com.dayone.service.MemberService;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; //1hour
    private static final String KEY_ROLES = "roles";
    private final MemberService memberService;


    @Value("{spring.jwt.secret}")
    private String secretKey;

    /**
     * 토큰 생성(발급)
     * @param username
     * @param roles
     * @return
     */
    public String generateToken(String username, List<String> roles) {
        //사용자의 권한정보를 저장하는 클래임생성
        Claims claims= Jwts.claims().setSubject(username);
        //클래임 저장시 키벨류로 저장
        claims.put(KEY_ROLES, roles);
        //토큰생성시간

        var now= new Date();
        //토큰만료시간
        var expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setClaims(claims) //사용자 정보
                .setIssuedAt(now) //토큰 생성 시간
                .setExpiration(expiredDate) //토큰만료시간
                .signWith(SignatureAlgorithm.ES512,this.secretKey) //사용할 암호화 알고리즘, 비밀키
                .compact();

    }

    public Authentication getAuthentication(String jwt) {
        //jwt 토큰에서 인증정보 가져오는
        UserDetails userDetails =this.memberService.loadUserByUsername(this.getUsername(jwt));
        return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    }


    public String getUsername(String token) {
        return this.parseClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) return false;

        var claims = this.parseClaims(token);
        return claims.getExpiration().before(new Date());
    }


    //토큰유효확인 메소드... 다른데서 안쓰이게 private
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
        }catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }


}
