package com.trelix.trelix_app.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> fieldErrors;
}




