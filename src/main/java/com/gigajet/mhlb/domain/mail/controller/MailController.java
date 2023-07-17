package com.gigajet.mhlb.domain.mail.controller;

import com.gigajet.mhlb.global.common.dto.SendMessageDto;
import com.gigajet.mhlb.domain.mail.dto.MailResponseDto;
import com.gigajet.mhlb.domain.mail.service.MailService;
import com.gigajet.mhlb.domain.user.dto.UserRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Mail", description = "메일 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    // 비밀번호 찾기 이메일 발송
    @Operation(summary = "비밀번호 찾기 이메일 발송 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 발송 성공"),
            @ApiResponse(responseCode = "400", description = "U-02, U-04", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @PostMapping("/check/email")
    public ResponseEntity<SendMessageDto> sendFindPasswordMail(@RequestBody UserRequestDto.CheckEmail emailDto) {
        ResponseEntity<SendMessageDto> responseEntity = mailService.beforeSendFindPasswordMail(emailDto.getEmail());
        mailService.sendFindPasswordMail(emailDto.getEmail());
        return responseEntity;
    }

    // 비밀번호 찾기 인증 코드 유효 검사
    @Operation(summary = "비밀번호 찾기 인증 코드 유효 검사 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유효한 인증 코드"),
            @ApiResponse(responseCode = "400", description = "U-05", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @PostMapping("/check/code/{uuid}")
    public ResponseEntity<SendMessageDto> checkFindPasswordCode(@PathVariable String uuid) {
        return mailService.checkFindPasswordCode(uuid);
    }

    // 비밀번호 변경
    @Operation(summary = "비밀번호 변경 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "U-02, U-05", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @PostMapping("/reset/password/{uuid}")
    public ResponseEntity<SendMessageDto> resetPassword(@PathVariable String uuid, @RequestBody UserRequestDto.Password passwordDto) {
        return mailService.resetPassword(uuid, passwordDto);
    }

    // 워크스페이스 초대 인증 코드 유효 검사 및 회원가입 유무 검사
    @Operation(summary = "워크스페이스 초대 인증 코드 유효 검사 및 회원가입 유무 검사 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "U-05", content = @Content(schema = @Schema(implementation = SendMessageDto.class)))
    })
    @PostMapping("/check/invite/code/{uuid}")
    public ResponseEntity<MailResponseDto.CheckInviteCode> checkInviteWorkspaceCode(@PathVariable String uuid) {
        return mailService.checkInviteWorkspaceCode(uuid);
    }

}
