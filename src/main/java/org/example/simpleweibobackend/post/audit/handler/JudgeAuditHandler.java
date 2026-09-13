package org.example.simpleweibobackend.post.audit.handler;

import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.post.audit.AuditContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 裁决节点：审核链最后一个节点，根据风险总分决定通过或拒绝。
 */
@Component
@Order(9999)
public class JudgeAuditHandler implements PostAuditHandler {

    /**
     * 拒绝阈值:风险总分达到该值即拒绝发帖
     */
    private static final Long REJECT_THRESHOLD = 100L;

    /**
     * 裁决:风险总分达到阈值时抛出业务异常,发帖被拒绝
     */
    @Override
    public void handle(AuditContext context) {
        if (context.getRiskScore() >= REJECT_THRESHOLD) {
            throw new BizException(ErrorCode.AUDIT_REJECTED, "内容存在风险，发帖被拒绝");
        }
    }
}
