package org.example.simpleweibobackend.post.create.audit.handler.sensitiveword;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.post.create.audit.AuditContext;
import org.example.simpleweibobackend.post.create.audit.handler.PostAuditHandler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/** 敏感词审核节点 */
@Component
@Order(0)
@RequiredArgsConstructor
public class SensitiveWordAuditHandler implements PostAuditHandler {

    private static final Long SENSITIVE_WORD_SCORE = 80L;
    private static final String SENSITIVE_WORD_REASON = "标题或正文包含敏感词";

    private final SensitiveWordMapper sensitiveWordMapper;

    @Override
    public void handle(AuditContext context) {
        Set<String> words = sensitiveWordMapper.selectList(Wrappers.emptyWrapper()).stream()
                .map(SensitiveWord::getWord)
                .collect(Collectors.toSet());
        SensitiveWordDfa dfa = SensitiveWordDfa.of(words);

        if (dfa.containsAny(context.getTitle()) || dfa.containsAny(context.getContent())) {
            context.setRiskScore(context.getRiskScore() + SENSITIVE_WORD_SCORE);
            context.getReasons().add(SENSITIVE_WORD_REASON);
        }
    }
}
