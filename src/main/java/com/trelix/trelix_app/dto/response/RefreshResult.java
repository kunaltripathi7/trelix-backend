package com.trelix.trelix_app.dto.response;

import com.trelix.trelix_app.entity.User;

public record RefreshResult(String newToken, User user) {
}
