package com.gigajet.mhlb.domain.user.service;

import com.gigajet.mhlb.global.common.dto.SendMessageDto;
import com.gigajet.mhlb.global.common.util.SuccessCode;
import com.gigajet.mhlb.domain.user.dto.GoogleOAuthRequestDto;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
import com.gigajet.mhlb.domain.user.social.GoogleOAuth;
import com.gigajet.mhlb.domain.user.social.SocialType;
import com.gigajet.mhlb.global.exception.CustomException;
import com.gigajet.mhlb.global.exception.ErrorCode;
import com.gigajet.mhlb.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {

    private final UserRepository userRepository;

    private final GoogleOAuth googleOAuth;
    private final JwtUtil jwtUtil;

    private final HttpServletResponse response;

    public ResponseEntity<SendMessageDto> oAuthLogin(String code) {
        GoogleOAuthRequestDto.Token tokenRequestDto = googleOAuth.getAccessToken(code);
        GoogleOAuthRequestDto.GoogleUser googleUserDto = googleOAuth.getUserInfo(tokenRequestDto);

        createOAuthUser(googleUserDto);

        /*
            UT 후 BE가 받은 유일한 피드백
            로그인 할 때 Token 방식을 사용할 때 Refresh Token은 선택이 아니라 필수

            TODO : Refresh Token 추가
         */

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(googleUserDto.getEmail()));

        return SendMessageDto.toResponseEntity(SuccessCode.LOGIN_SUCCESS);
    }

    @Transactional
    void createOAuthUser(GoogleOAuthRequestDto.GoogleUser googleUserDto) {
        Optional<User> userOptional = userRepository.findByEmail(googleUserDto.getEmail());

        if (userOptional.isEmpty()) {
            userRepository.save(new User(googleUserDto));

            /*
                UT 후 소셜 유저가 로그인 할 때만 Status 객체가 null인 이슈 발생
                기본 회원가입처럼 소셜 유저 회원가입 때 Status 객체를 생성해 데이터베이스에 저장하면 해결 됨

                TODO : 소셜 회원가입 때 Status 객체 생성해 DB에 저장
             */

        } else if (userOptional.get().getType() != SocialType.GOOGLE) {
            throw new CustomException(ErrorCode.NOT_SOCIAL_EMAIL);
        }
    }

}
