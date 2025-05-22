package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.Project;
import com.trelix.trelix_app.entity.ProjectMember;
import com.trelix.trelix_app.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, UUID> {
    Optional<ProjectMember> findByProjectIdAndUserIdAndRole(UUID projectId, UUID userId, Role role);

}
