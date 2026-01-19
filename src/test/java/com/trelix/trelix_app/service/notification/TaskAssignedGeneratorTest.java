package com.trelix.trelix_app.service.notification;

import com.trelix.trelix_app.enums.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TaskAssignedGeneratorTest {

    private TaskAssignedGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new TaskAssignedGenerator();
    }

    @Test
    @DisplayName("generate() should return correct message with actor name and task title")
    void generate_withValidInputs_returnsFormattedMessage() {

        String actorName = "John Doe";
        Map<String, String> metadata = new HashMap<>();
        metadata.put("taskTitle", "Complete API documentation");

        String result = generator.generate(actorName, metadata);

        assertEquals("John Doe assigned you to task: Complete API documentation", result);
    }

    @Test
    @DisplayName("generate() should show N/A when taskTitle is missing from metadata")
    void generate_withMissingTaskTitle_returnsMessageWithNA() {
        String actorName = "Jane Smith";
        Map<String, String> metadata = new HashMap<>();

        String result = generator.generate(actorName, metadata);

        assertEquals("Jane Smith assigned you to task: N/A", result);
    }

    @Test
    @DisplayName("getType() should return TASK_ASSIGNED")
    void getType_returnsTaskAssigned() {

        NotificationType type = generator.getType();
        assertEquals(NotificationType.TASK_ASSIGNED, type);
    }
}
