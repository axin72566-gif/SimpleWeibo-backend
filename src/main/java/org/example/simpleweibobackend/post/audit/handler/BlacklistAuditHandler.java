package org.example.simpleweibobackend.post.audit.handler;

import org.example.simpleweibobackend.post.audit.AuditContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Order(0)
public class BlacklistAuditHandler implements PostAuditHandler {

    private static final Long BLACKLIST_SCORE = 100L;

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
