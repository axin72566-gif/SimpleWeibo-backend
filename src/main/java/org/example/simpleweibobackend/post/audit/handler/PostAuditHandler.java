package org.example.simpleweibobackend.post.audit.handler;

import org.example.simpleweibobackend.post.audit.AuditContext;

/**
 * 发帖审核责任链节点
 */
public interface PostAuditHandler {

    void handle(AuditContext context);
}
