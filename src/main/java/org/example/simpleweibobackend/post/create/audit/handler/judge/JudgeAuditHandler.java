package org.example.simpleweibobackend.post.create.audit.handler.judge;

import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.post.create.audit.AuditContext;
import org.example.simpleweibobackend.post.create.audit.handler.PostAuditHandler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 审核链末位节点:按累计风险分裁决,达到阈值直接拒绝,链路走完即视为通过
 */
@Component
@Order(9999)
public class JudgeAuditHandler implements PostAuditHandler {

    private static final Long REJECT_THRESHOLD = 100L;

    @Override
    public void handle(AuditContext context) {
        if (context.getRiskScore() >= REJECT_THRESHOLD) {
            throw new BizException(ErrorCode.AUDIT_REJECTED, String.join("；", context.getReasons()));
        }
    }
}
