package org.example.simpleweibobackend.post.create.audit.handler.judge;

import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.post.create.audit.AuditContext;
import org.example.simpleweibobackend.post.create.audit.handler.PostAuditHandler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 审核链末位节点:按累计风险分裁决,达到阈值直接拒绝,链路走完即视为通过
 */
@Slf4j
@Component
@Order(9999)
public class JudgeAuditHandler implements PostAuditHandler {

    private static final Long REJECT_THRESHOLD = 100L;

    @Override
    public void handle(AuditContext context) {
        if (context.getRiskScore() >= REJECT_THRESHOLD) {
            log.warn("发帖被拒绝: 风险分={}, 原因={}", context.getRiskScore(), context.getReasons());
            throw new BizException(ErrorCode.AUDIT_REJECTED, String.join("；", context.getReasons()));
        }
    }
}
