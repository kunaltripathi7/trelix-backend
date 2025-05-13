package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findByTeamId(UUID teamId);

    List<Project> findByNameContainingIgnoreCase(String name);
}
