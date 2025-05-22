package com.trelix.trelix_app.service;

import com.trelix.trelix_app.repository.TaskMemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskMemberService {

    private final TaskService taskService;
    private final AuthorizationService authService;
    private final TaskMemberRepository taskMemberRepository;
}
