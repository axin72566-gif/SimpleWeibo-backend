package org.example.simpleweibobackend.post.create.audit.handler.sensitiveword;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.post.create.audit.AuditContext;
import org.example.simpleweibobackend.post.create.audit.handler.PostAuditHandler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 敏感词审核节点:词库每次审核实时查 sensitive_word 表并构建 DFA(改词即时生效),
 * 标题或正文命中任一敏感词即累加风险分
 */
@Component
@Order(1)
@RequiredArgsConstructor
public class SensitiveWordAuditHandler implements PostAuditHandler {

    /**
     * 命中敏感词累加的风险分
     */
    private static final Long SENSITIVE_WORD_SCORE = 100L;

    private final SensitiveWordMapper sensitiveWordMapper;

    @Override
    public void handle(AuditContext context) {
        Set<String> words = sensitiveWordMapper.selectList(Wrappers.emptyWrapper()).stream()
                .map(SensitiveWord::getWord)
                .collect(Collectors.toSet());
        SensitiveWordDfa dfa = SensitiveWordDfa.of(words);

        if (dfa.containsAny(context.getTitle()) || dfa.containsAny(context.getContent())) {
            context.setRiskScore(context.getRiskScore() + SENSITIVE_WORD_SCORE);
        }
    }
}
