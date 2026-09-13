package org.example.simpleweibobackend.post.audit;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditContext {

    private Long userId;

    private String title;

    private String content;

    @Builder.Default
    private Long riskScore = 0L;

}
