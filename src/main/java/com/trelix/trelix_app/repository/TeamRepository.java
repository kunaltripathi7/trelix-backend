package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface TeamRepository extends JpaRepository<Team, UUID> {

    Optional<Team> findByName(String name);

    List<Team> findByDescriptionContainingIgnoreCase(String keyword);

}
