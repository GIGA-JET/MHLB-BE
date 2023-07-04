package com.gigajet.mhlb.domain.mail.service;

import com.gigajet.mhlb.global.common.dto.SendMessageDto;
import com.gigajet.mhlb.global.common.util.AESUtil;
import com.gigajet.mhlb.global.common.util.SuccessCode;
import com.gigajet.mhlb.domain.mail.dto.MailResponseDto;
import com.gigajet.mhlb.domain.user.dto.UserRequestDto;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
import com.gigajet.mhlb.domain.user.social.SocialType;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceInvite;
import com.gigajet.mhlb.global.exception.CustomException;
import com.gigajet.mhlb.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final UserRepository userRepository;

    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    private final AESUtil aesUtil;

    @Value("${spring.mail.username}")
    private String myAddress;

    /*
        메일 발송이 매우 느린 이슈가 있음

        TODO : 메일 발송을 비동기 처리로 바꿔보기
     */

    // 비밀번호 찾기 메일 발송
    public ResponseEntity<SendMessageDto> sendFindPasswordMail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.UNREGISTER_USER));
        if (user.getType() == SocialType.GOOGLE) {
            throw new CustomException(ErrorCode.SOCIAL_USER);
        }

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        saveRandomNumberAndEmail(uuid, email);

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setFrom(myAddress);
            mimeMessageHelper.setSubject("PIN ME :: 이메일 인증");
            mimeMessageHelper.setText("<h1><a href='http://localhost:3000/reset-password/" + uuid + "'>비밀번호 변경</a></h1>", true);

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return SendMessageDto.toResponseEntity(SuccessCode.VALID_EMAIL);
    }

    // 비밀번호 찾기 캐시 저장
    private void saveRandomNumberAndEmail(String randomNumber, String email) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(randomNumber, email, Duration.ofMinutes(10));
    }

    // 워크스페이스 초대 캐시 저장
    private void saveRandomNumberAndEmail(String randomNumber, WorkspaceInvite workspaceInvite) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();

        Map<String, String> map = new HashMap<>(3);
        map.put("email", workspaceInvite.getEmail());
        map.put("inviteId", workspaceInvite.getId().toString());

        if (workspaceInvite.getUser() == null) {
            map.put("isUser", "N");
        } else {
            map.put("isUser", "Y");
        }
        hashOperations.putAll(randomNumber, map);
        hashOperations.getOperations().expire(randomNumber, Duration.ofMinutes(3));
    }

    // 비밀번호 찾기 인증 코드 유효 검사
    public ResponseEntity<SendMessageDto> checkFindPasswordCode(String uuid) {
        String email = redisTemplate.opsForValue().get(uuid);
        if (email == null) {
            throw new CustomException(ErrorCode.INVALID_CODE);
        }

        return SendMessageDto.toResponseEntity(SuccessCode.VALID_CODE);
    }

    // 비밀번호 변경
    @Transactional
    public ResponseEntity<SendMessageDto> resetPassword(String uuid, UserRequestDto.Password passwordDto) {
        String email = redisTemplate.opsForValue().get(uuid);
        if (email == null) {
            throw new CustomException(ErrorCode.INVALID_CODE);
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.UNREGISTER_USER));
        user.resetPassword(aesUtil.encrypt(passwordDto.getPassword()));

        return SendMessageDto.toResponseEntity(SuccessCode.RESET_PASSWORD_SUCCESS);
    }

    // 워크스페이스 초대 메일 발송
    public void sendInviteMail(WorkspaceInvite workspaceInvite) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        saveRandomNumberAndEmail(uuid, workspaceInvite);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setTo(workspaceInvite.getEmail());
            mimeMessageHelper.setFrom(myAddress);
            mimeMessageHelper.setSubject("PIN ME :: 이메일 인증");
            mimeMessageHelper.setText("<h1><a href='http://localhost:3000/invite-workspace/" + uuid + "'>Workspace 참여</a></h1>", true);

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    // 워크스페이스 초대 인증 코드 유효 검사 및 회원가입 유무 검사
    public ResponseEntity<MailResponseDto.CheckInviteCode> checkInviteWorkspaceCode(String uuid) {
        if (Boolean.FALSE.equals(redisTemplate.hasKey(uuid))) {
            throw new CustomException(ErrorCode.INVALID_CODE);
        }

        Map<Object, Object> entries = redisTemplate.opsForHash().entries(uuid);
        return ResponseEntity.ok(new MailResponseDto.CheckInviteCode((String) entries.get("isUser")));
    }

    // 워크스페이스 초대 인증 코드로 유저 정보 반환
    public Map<Object, Object> getUserInfo(String uuid) {
        if (Boolean.FALSE.equals(redisTemplate.hasKey(uuid))) {
            throw new CustomException(ErrorCode.INVALID_CODE);
        }

        return redisTemplate.opsForHash().entries(uuid);
    }
}
