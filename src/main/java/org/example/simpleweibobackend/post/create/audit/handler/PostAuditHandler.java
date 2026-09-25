package org.example.simpleweibobackend.post.create.audit.handler;

import org.example.simpleweibobackend.post.create.audit.AuditContext;

public interface PostAuditHandler {

    /**
     * 向上下文累加风险分,不做最终裁决
     */
    void handle(AuditContext context);
}
