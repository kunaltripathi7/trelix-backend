package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.ProjectMember;
import com.trelix.trelix_app.enums.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMember.ProjectMemberId> {

    List<ProjectMember> findByIdProjectId(UUID projectId);

    Optional<ProjectMember> findByIdProjectIdAndIdUserId(UUID projectId, UUID userId);

    boolean existsByIdProjectIdAndIdUserId(UUID projectId, UUID userId);

    long countByIdProjectIdAndRole(UUID projectId, ProjectRole role);

    @Query("SELECT pm.role FROM ProjectMember pm WHERE pm.id.projectId = :projectId AND pm.id.userId = :userId")
    Optional<ProjectRole> findRoleByProjectIdAndUserId(@Param("projectId") UUID projectId, @Param("userId") UUID userId);
}
