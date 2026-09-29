package org.example.simpleweibobackend.post.create.audit;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.post.create.audit.handler.PostAuditHandler;
import org.springframework.stereotype.Component;

import java.util.List;

/** 发帖审核责任链 */
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
