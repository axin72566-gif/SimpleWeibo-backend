package org.example.simpleweibobackend.post.create.audit.handler.sensitiveword;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.post.create.audit.AuditContext;
import org.example.simpleweibobackend.post.create.audit.handler.PostAuditHandler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 敏感词审核节点:DFA 整体缓存于本地,未命中查库构建;改词最迟一个过期周期生效
 */
@Component
@Order(1)
@RequiredArgsConstructor
public class SensitiveWordAuditHandler implements PostAuditHandler {

    private static final Long SENSITIVE_WORD_SCORE = 100L;

    private static final String DFA_CACHE_KEY = "sensitive_word_dfa";

    private static final Duration DFA_CACHE_EXPIRE = Duration.ofMinutes(5);

    private final SensitiveWordMapper sensitiveWordMapper;

    private final Cache<String, SensitiveWordDfa> dfaCache = Caffeine.newBuilder()
            .expireAfterWrite(DFA_CACHE_EXPIRE)
            .build();

    @Override
    public void handle(AuditContext context) {
        SensitiveWordDfa dfa = dfaCache.get(DFA_CACHE_KEY, key -> SensitiveWordDfa.of(loadWords()));

        if (dfa.containsAny(context.getTitle()) || dfa.containsAny(context.getContent())) {
            context.setRiskScore(context.getRiskScore() + SENSITIVE_WORD_SCORE);
        }
    }

    private Set<String> loadWords() {
        return sensitiveWordMapper.selectList(Wrappers.emptyWrapper()).stream()
                .map(SensitiveWord::getWord)
                .collect(Collectors.toSet());
    }
}
