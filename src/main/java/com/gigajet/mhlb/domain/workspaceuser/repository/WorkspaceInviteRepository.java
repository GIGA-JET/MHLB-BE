package com.gigajet.mhlb.domain.workspaceuser.repository;

import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceInviteRepository extends JpaRepository<WorkspaceInvite, Long> {
    List<WorkspaceInvite> findByWorkspaceOrderByIdDesc(Workspace workspace);

    Optional<WorkspaceInvite> findByWorkspace_IdAndId(Long workspaceId, Long inviteId);

    Optional<WorkspaceInvite> findByWorkspaceAndEmail(Workspace workspace, String email);

    void deleteByWorkspace(Workspace workspace);
}
