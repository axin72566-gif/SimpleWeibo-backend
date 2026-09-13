package org.example.simpleweibobackend.post.audit;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.post.audit.handler.PostAuditHandler;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 发帖审核责任链：按 {@code @Order} 顺序执行所有节点累加风险分，返回携带总分的上下文。
 */
@Component
@RequiredArgsConstructor
public class PostAuditChain {

    private final List<PostAuditHandler> handlers;

    public void audit(AuditContext context) {
        for (PostAuditHandler handler : handlers) {
            handler.handle(context);
        }
    }
}
