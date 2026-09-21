package org.example.simpleweibobackend.post.create.audit.handler;

import org.example.simpleweibobackend.post.create.audit.AuditContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 黑名单审核节点:命中黑名单的用户直接加满分,后续必被裁决拒绝
 */
@Component
@Order(0)
public class BlacklistAuditHandler implements PostAuditHandler {

    /**
     * 命中黑名单时累加的风险分(达到裁决阈值)
     */
    private static final Long BLACKLIST_SCORE = 100L;

    /**
     * 黑名单用户 ID 集合
     */
    private static final Set<Long> BLACKLIST_USER_IDS = Set.of(
            1L,
            2L
    );

    @Override
    public void handle(AuditContext context) {
        if (BLACKLIST_USER_IDS.contains(context.getUserId())) {
            context.setRiskScore(context.getRiskScore() + BLACKLIST_SCORE);
        }
    }
}
