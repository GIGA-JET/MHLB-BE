package com.gigajet.mhlb.domain.user.controller;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.domain.mail.service.MailService;
import com.gigajet.mhlb.domain.mypage.dto.MypageResponseDto;
import com.gigajet.mhlb.domain.status.service.StatusService;
import com.gigajet.mhlb.domain.user.dto.UserRequestDto;
import com.gigajet.mhlb.domain.user.dto.UserResponseDto;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.service.OAuthService;
import com.gigajet.mhlb.domain.user.service.UserService;
import com.gigajet.mhlb.security.user.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

@Tag(name = "User", description = "회원 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final StatusService statusService;
    private final OAuthService oAuthService;
    private final MailService mailService;

    // 중복 체크
    @Operation(summary = "중복 이메일 체크", description = "이메일 중복 확인 API")
    @PostMapping("/duplicate-email")
    public ResponseEntity<SendMessageDto> duplicateEmail(@Valid @RequestBody UserRequestDto.CheckEmail emailDto) {
        return userService.duplicateEmail(emailDto.getEmail());
    }

    // 유효 체크
    @Operation(summary = "유효 이메일 체크", description = "이메일 유효 확인 API")
    @PostMapping("/validate-email")
    public ResponseEntity<SendMessageDto> validateEmail(@Valid @RequestBody UserRequestDto.CheckEmail emailDto) {
        return userService.validateEmail(emailDto.getEmail());
    }

    // 회원가입
    @Operation(summary = "회원가입", description = "회원가입 API")
    @PostMapping("/register")
    public ResponseEntity<SendMessageDto> register(@Valid @RequestBody UserRequestDto.Register registerDto) {
        User user = userService.register(registerDto);
        return statusService.register(user);
    }

    // 초대 메일로 회원가입
    @Operation(summary = "메일 회원가입", description = "메일 회원가입 API")
    @PostMapping("/register/{uuid}")
    public ResponseEntity<SendMessageDto> register(@PathVariable String uuid, @RequestBody UserRequestDto.Register registerDto) {
        Map<Object, Object> userInfo = mailService.getUserInfo(uuid);
        User user = userService.register(registerDto, userInfo);

        return statusService.register(user);
    }

    // 로그인
    @Operation(summary = "로그인", description = "로그인 API")
    @PostMapping("/login")
    public ResponseEntity<SendMessageDto> login(@Valid @RequestBody UserRequestDto.Login loginDto, HttpServletResponse response) {
        return userService.login(loginDto, response);
    }

    // 상시 프로필
    @GetMapping("/user-info")
    public UserResponseDto userInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.userInfo(userDetails.getUser());
    }

    /*
        소셜 로그인
     */
    @Operation(summary = "구글 소셜 로그인", description = "구글 소셜 로그인 API")
    @PostMapping("/auth/google/callback")
    public ResponseEntity<SendMessageDto> callback(@RequestParam(name = "code") String code) {
        return oAuthService.oAuthLogin(code);
    }

}
