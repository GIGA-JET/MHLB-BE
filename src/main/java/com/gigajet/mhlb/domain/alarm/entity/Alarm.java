package com.gigajet.mhlb.domain.alarm.entity;

import com.gigajet.mhlb.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long workspaceId;
    @Column(nullable = false)
    private Boolean unreadMessage;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private String uuid;
    @Column
    private AlarmTypeEnum type;

    @Builder
    public Alarm(Long workspaceId, String uuid, Boolean unreadMessage, AlarmTypeEnum type, User user) {
        this.workspaceId = workspaceId;
        this.unreadMessage = unreadMessage;
        this.type = type;
        this.uuid = uuid;
        this.user = user;
    }

    public void toggleUnreadMessage() {
        unreadMessage = !unreadMessage;
    }
}
