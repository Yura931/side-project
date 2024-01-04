package sideproject.admin.jwt.service.impl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import sideproject.admin.jwt.principal.PrincipalDetails;
import sideproject.admin.user.entity.Member;
import sideproject.admin.user.entity.MemberRole;
import sideproject.admin.user.entity.enums.MemberRoles;
import sideproject.admin.user.repository.MemberRepository;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static sideproject.admin.jwt.properties.JwtProperties.SECRET;

@SpringBootTest
@Rollback(value = false)
@Transactional
class JWTServiceImplTest {
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;    // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일

    @Autowired
    JWTService jwtService;
    @Autowired
    UserService userService;

    @Autowired
    MemberRepository memberRepository;
    PrincipalDetails principalDetails;

    private Map<String, String> tokenDto;
    private Map<String, String> invalidJwtToken;

    @BeforeEach
    public void beforeSetting() {
        Member member = Member.joinNewAdminMember("admin@email.com", "admin");
        MemberRole memberRole = MemberRole.generateNewMemberByRoleAdmin(member);
        member.saveMemberRole(Collections.singletonList(memberRole));

        principalDetails = new PrincipalDetails(member, Collections.singletonList(
                new SimpleGrantedAuthority(MemberRoles.ROLE_ADMIN.toString())
        ));

        String accessToken = Jwts.builder()
                .setSubject(member.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(member.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();

        Map<String, String> map = new HashMap<>();
        map.put("accessToken", accessToken);
        map.put("refreshToken", refreshToken);
        tokenDto = map;


        accessToken = Jwts.builder()
                .setSubject(member.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() - 1000000))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();

        refreshToken = Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(member.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() - 1000000))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();

        map = new HashMap<>();
        map.put("accessToken", accessToken);
        map.put("refreshToken", refreshToken);
        invalidJwtToken = map;
    }
    private Key getSignKey() {
        byte[] key = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(key);
    }
    @Test
    public void accessToken유효시간체크() throws Exception {
        assertTrue(jwtService.isTokenValid(tokenDto.get("accessToken"), principalDetails));
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(invalidJwtToken.get("accessToken"), principalDetails));
    }

    @Test
    public void refreshToken유효시간체크() throws Exception {
        assertTrue(jwtService.isTokenValid(tokenDto.get("refreshToken"), principalDetails));
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(invalidJwtToken.get("refreshToken"), principalDetails));
    }

    @Test
    public void extractUserName() throws Exception {
        String claim = jwtService.extractUserName(tokenDto.get("accessToken"));
        Assertions.assertThat(claim).isEqualTo(principalDetails.getUsername());
    }
}