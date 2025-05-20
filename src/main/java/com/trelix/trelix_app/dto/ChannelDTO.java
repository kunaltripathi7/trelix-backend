package com.trelix.trelix_app.dto;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChannelDTO {
    private UUID id;
    private String name;
}