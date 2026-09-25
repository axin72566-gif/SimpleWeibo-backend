package org.example.simpleweibobackend.post.create.audit;

import lombok.Builder;
import lombok.Data;

/**
 * 审核上下文:责任链各节点共享的待审核信息与累计风险分
 */
@Data
@Builder
public class AuditContext {

    private String title;

    private String content;

    /**
     * 各节点累加,裁决节点按阈值判定
     */
    @Builder.Default
    private Long riskScore = 0L;

}
