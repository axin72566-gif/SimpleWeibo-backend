package org.example.simpleweibobackend.post.create.audit.handler.judge;

import org.example.simpleweibobackend.post.create.audit.AuditContext;
import org.example.simpleweibobackend.post.create.audit.AuditResultEnum;
import org.example.simpleweibobackend.post.create.audit.handler.PostAuditHandler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 审核链末位节点:按累计风险分裁决通过或拒绝
 */
@Component
@Order(9999)
public class JudgeAuditHandler implements PostAuditHandler {

    private static final Long HALF_RISK_THRESHOLD = 50L;
    private static final Long MAX_RISK_THRESHOLD = 100L;

    @Override
    public void handle(AuditContext context) {
        if (context.getRiskScore() >= MAX_RISK_THRESHOLD) {
            context.setResult(AuditResultEnum.REJECT);
        } else if (context.getRiskScore() >= HALF_RISK_THRESHOLD) {
            context.setResult(AuditResultEnum.MANUAL_REVIEW);
        } else {
            context.setResult(AuditResultEnum.PASS);
        }
    }
}