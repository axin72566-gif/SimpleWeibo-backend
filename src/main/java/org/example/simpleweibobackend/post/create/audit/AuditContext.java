package org.example.simpleweibobackend.post.create.audit;

import lombok.Builder;
import lombok.Data;

/**
 * 审核上下文:责任链各节点共享的待审核信息与累计风险分
 */
@Data
@Builder
public class AuditContext {

    /**
     * 待审核的帖子标题
     */
    private String title;

    /**
     * 待审核的帖子正文
     */
    private String content;

    /**
     * 累计风险分,各审核节点按规则累加,裁决节点按阈值判定
     */
    @Builder.Default
    private Long riskScore = 0L;

}
