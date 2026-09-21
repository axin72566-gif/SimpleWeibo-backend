package org.example.simpleweibobackend.post.create.audit.handler;

import org.example.simpleweibobackend.post.create.audit.AuditContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 敏感词审核节点:标题或正文命中敏感词时累加风险分
 */
@Component
@Order(1)
public class SensitiveWordAuditHandler implements PostAuditHandler {

    /**
     * 每命中一个敏感词累加的风险分
     */
    private static final Long SENSITIVE_WORD_SCORE = 100L;

    /**
     * 敏感词库
     */
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
