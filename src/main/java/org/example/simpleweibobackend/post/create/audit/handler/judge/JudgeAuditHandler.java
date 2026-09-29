package org.example.simpleweibobackend.post.create.audit.handler.judge;

import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.post.create.audit.AuditContext;
import org.example.simpleweibobackend.post.create.audit.handler.PostAuditHandler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** 审核裁决节点 */
@Slf4j
@Component
@Order(9999)
public class JudgeAuditHandler implements PostAuditHandler {

    private static final Long REJECT_THRESHOLD = 100L;

    @Override
    public void handle(AuditContext context) {
        if (context.getRiskScore() >= REJECT_THRESHOLD) {
            log.warn("发帖被拒绝: 风险分={}, 原因={}", context.getRiskScore(), context.getReasons());
            throw new BizException(ErrorCode.AUDIT_REJECTED);
        }
    }
}
