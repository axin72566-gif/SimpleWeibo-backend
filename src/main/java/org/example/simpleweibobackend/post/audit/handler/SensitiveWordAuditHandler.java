package org.example.simpleweibobackend.post.audit.handler;

import org.example.simpleweibobackend.post.audit.AuditContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Order(1)
public class SensitiveWordAuditHandler implements PostAuditHandler {

    private static final Long SENSITIVE_WORD_SCORE = 100L;

    private static final Set<String> SENSITIVE_WORDS = Set.of(
            "赌博",
            "诈骗",
            "刷单",
            "代开发票",
            "外挂"
    );

    @Override
    public void handle(AuditContext context) {
        String text = context.getTitle() + context.getContent();
        for (String word : SENSITIVE_WORDS) {
            if (text.contains(word)) {
                context.setRiskScore(context.getRiskScore() + SENSITIVE_WORD_SCORE);
            }
        }
    }
}
