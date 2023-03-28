package com.gigajet.mhlb.domain.workspace.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceRequestDto;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUser;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String image;

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<WorkspaceUser> workspaceUsers;

    @Column(nullable = false)
    private Integer isShow;

    public Workspace(WorkspaceRequestDto.Create workspaceDto, String image) {
        this.title = workspaceDto.getWorkspaceTitle();
        this.description = workspaceDto.getWorkspaceDesc();
        this.image = image;
        this.isShow = 1;
    }

    public void imageChange(String image) {
        this.image = image;
    }

    public void titleChange(String title) {
        this.title = title;
    }

    public void descChange(String workspaceDesc) {
        this.description = workspaceDesc;
    }

    public void updateIsShow() {
        this.isShow = 0;
    }
}