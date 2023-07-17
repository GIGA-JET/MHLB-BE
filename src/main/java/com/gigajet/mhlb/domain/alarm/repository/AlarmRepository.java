package com.gigajet.mhlb.domain.alarm.repository;

import com.gigajet.mhlb.domain.alarm.entity.Alarm;
import com.gigajet.mhlb.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    Optional<Alarm> findByUserIdAndWorkspaceIdAndUuid(Long userId, Long workspaceId, String uuid);

    Optional<Alarm> findByUserIdAndWorkspaceIdAndUuidAndUnreadMessage(Long userId, Long workspaceId, String uuid, Boolean unreadMessage);

    List<Alarm> findAllByUserAndWorkspaceIdAndUnreadMessage(User user, Long workspaceId, Boolean unreadMessage);

    List<Alarm> findAllByUserIdAndWorkspaceIdAndUnreadMessage(Long userId, Long workspaceId, Boolean unreadMessage);

}
