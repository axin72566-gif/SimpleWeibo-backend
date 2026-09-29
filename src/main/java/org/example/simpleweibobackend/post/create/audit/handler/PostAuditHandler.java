package org.example.simpleweibobackend.post.create.audit.handler;

import org.example.simpleweibobackend.post.create.audit.AuditContext;

public interface PostAuditHandler {

    /** 执行审核 */
    void handle(AuditContext context);
}
