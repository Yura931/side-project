package sideproject.authservice.auth.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sideproject.authservice.auth.dto.RefreshTokenDto;
import sideproject.authservice.auth.dto.SignInDto;
import sideproject.authservice.auth.dto.SignUpDto;
import sideproject.authservice.auth.dto.item.SignInItem;
import sideproject.authservice.auth.dto.item.SignUpItem;
import sideproject.authservice.auth.dto.response.LogoutResponse;
import sideproject.authservice.auth.dto.response.ReIssueAccessTokenResponse;
import sideproject.authservice.auth.dto.response.SignInResponse;
import sideproject.authservice.auth.dto.response.SignUpResponse;
import sideproject.authservice.auth.repository.AuthRepository;
import sideproject.authservice.auth.service.AuthService;
import sideproject.authservice.common.util.CookieUtil;
import sideproject.authservice.global.exception.ExpiredRefreshTokenException;
import sideproject.authservice.global.exception.EmailDuplicateException;
import sideproject.authservice.global.exception.NicknameDuplicateException;
import sideproject.authservice.global.exception.enums.ErrorCode;
import sideproject.authservice.jwt.util.JwtUtil;
import sideproject.authservice.principal.PrincipalDetails;
import sideproject.authservice.principal.PrincipalDetailsService;
import sideproject.authservice.redis.RedisUtil;
import sideproject.authservice.redis.dto.RedisDto;
import sideproject.authservice.user.entity.UserRole;
import sideproject.authservice.user.entity.Users;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import static sideproject.authservice.jwt.properties.JwtProperties.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthRepository authRepository;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final CookieUtil cookieUtil;
    private final PrincipalDetailsService principalDetailsService;

    public SignUpResponse signUp(SignUpDto dto) {
        if(authRepository.existsByEmail(dto.email()))
            throw new EmailDuplicateException();

        if(authRepository.existsByNickname(dto.nickname()))
            throw new NicknameDuplicateException();

        Users users = Users.createUser(dto);
        users.saveUserRole(UserRole.generateNewMemberByRoleUser(users));
        Users saveUsers = authRepository.save(users);
        SignUpItem signUpItem = SignUpItem.UserEntityToDto(saveUsers);
        return new SignUpResponse(signUpItem);
    }
    public SignInResponse signIn(SignInDto dto, HttpServletResponse response) {
        final PrincipalDetails userDetails = (PrincipalDetails) principalDetailsService.userDetailsService().loadUserByUsername(dto.email());
        final String token = jwtUtil.generateToken(userDetails);
        final String refreshToken = jwtUtil.generateRefreshToken(new HashMap<>(), userDetails);
        cookieUtil.addCookie(response, REFRESH_PREFIX, refreshToken);
        return new SignInResponse(SignInItem.tokenItem(token));
    }

    public ReIssueAccessTokenResponse reIssueAccessToken(RefreshTokenDto dto, HttpServletResponse response) {

        final String userEmail = jwtUtil.extractUserName(dto.refreshToken());
        final PrincipalDetails userDetails = (PrincipalDetails) principalDetailsService.userDetailsService().loadUserByUsername(userEmail);
        final String token = jwtUtil.generateToken(userDetails);
        cookieUtil.addCookie(response, REFRESH_PREFIX, dto.refreshToken());

        return ReIssueAccessTokenResponse.from(token);
    }

    public LogoutResponse logout(HttpServletRequest request, HttpServletResponse response) {

        String accessToken = jwtUtil.getHeaderAccessToken(request);
        String userEmail = jwtUtil.extractUserName(accessToken);

        Cookie cookie = cookieUtil.getCookie(request, REFRESH_PREFIX)
                .orElseThrow(ExpiredRefreshTokenException::new);
        RefreshTokenDto dto = RefreshTokenDto.from(cookie.getValue());


        if(!Objects.isNull(redisUtil.get(REFRESH_TOKEN_KEY_PREFIX.concat(dto.refreshToken())))) {
            redisUtil.delete(REFRESH_TOKEN_KEY_PREFIX.concat(dto.refreshToken()));
        }

        Long getExpiration = jwtUtil.getExpiration(accessToken) - new Date().getTime();
        RedisDto accessTokenDto = RedisDto.of(userEmail, accessToken);
        redisUtil.setBlackList(ACCESS_TOKEN_KEY_PREFIX.concat(accessToken), accessTokenDto, getExpiration.intValue());
        cookieUtil.deleteCookie(request, response, REFRESH_PREFIX);

        return LogoutResponse.from("로그아웃 되었습니다");
    }

}
