package org.example.simpleweibobackend.post.create.audit.handler.judge;

import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.post.create.audit.AuditContext;
import org.example.simpleweibobackend.post.create.audit.handler.PostAuditHandler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 审核链末位节点:按累计风险分裁决通过或拒绝
 */
@Component
@Order(9999)
public class JudgeAuditHandler implements PostAuditHandler {

    private static final Long RISK_THRESHOLD = 100L;

    @Override
    public void handle(AuditContext context) {
        if (context.getRiskScore() >= RISK_THRESHOLD) {
            throw new BizException(ErrorCode.AUDIT_REJECTED, "内容存在风险，发帖被拒绝");
        }
    }
}
