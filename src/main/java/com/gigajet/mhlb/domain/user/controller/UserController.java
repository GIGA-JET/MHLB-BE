package com.gigajet.mhlb.domain.user.controller;

import com.gigajet.mhlb.global.common.dto.SendMessageDto;
import com.gigajet.mhlb.domain.mail.service.MailService;
import com.gigajet.mhlb.domain.status.service.StatusService;
import com.gigajet.mhlb.domain.user.dto.UserRequestDto;
import com.gigajet.mhlb.domain.user.dto.UserResponseDto;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.service.OAuthService;
import com.gigajet.mhlb.domain.user.service.UserService;
import com.gigajet.mhlb.security.user.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

@Tag(name = "User", description = "회원 API")
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final StatusService statusService;
    private final OAuthService oAuthService;
    private final MailService mailService;

    @Operation(summary = "이메일 중복 확인 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용 가능한 이메일"),
            @ApiResponse(responseCode = "400", description = "U-01", content = @Content(schema = @Schema(implementation = SendMessageDto.class))),
    })
    @PostMapping("/duplicate-email")
    public ResponseEntity<SendMessageDto> duplicateEmail(@Valid @RequestBody UserRequestDto.CheckEmail emailDto) {
        return userService.duplicateEmail(emailDto.getEmail());
    }

    @Operation(summary = "이메일 유효 확인 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유효한 사용자의 이메일"),
            @ApiResponse(responseCode = "400", description = "U-02", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @PostMapping("/validate-email")
    public ResponseEntity<SendMessageDto> validateEmail(@Valid @RequestBody UserRequestDto.CheckEmail emailDto) {
        return userService.validateEmail(emailDto.getEmail());
    }

    @Operation(summary = "회원가입 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "U-01", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<SendMessageDto> register(@Valid @RequestBody UserRequestDto.Register registerDto) {
        User user = userService.register(registerDto);
        return statusService.register(user);
    }

    @Operation(summary = "메일 회원가입 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "U-01, U-03, U-05, W-01", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @PostMapping("/register/{uuid}")
    public ResponseEntity<SendMessageDto> register(@PathVariable String uuid, @RequestBody UserRequestDto.Register registerDto) {
        Map<Object, Object> userInfo = mailService.getUserInfo(uuid);
        User user = userService.register(registerDto, userInfo);

        return statusService.register(user);
    }

    @Operation(summary = "로그인 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "U-02, U-04", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<SendMessageDto> login(@Valid @RequestBody UserRequestDto.Login loginDto, HttpServletResponse response) {
        return userService.login(loginDto, response);
    }

    @Operation(summary = "헤더 프로필 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 조회 성공")
    })
    @GetMapping("/user-info")
    public UserResponseDto userInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.userInfo(userDetails.getUser());
    }


    @Operation(summary = "구글 소셜 로그인 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "소셜 로그인 성공"),
            @ApiResponse(responseCode = "400", description = "U-07", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @PostMapping("/auth/google/callback")
    public ResponseEntity<SendMessageDto> callback(@RequestParam(name = "code") String code) {
        return oAuthService.oAuthLogin(code);
    }

}
