package org.example.simpleweibobackend.post.audit.handler;

import org.example.simpleweibobackend.post.audit.AuditContext;

/**
 * 发帖审核责任链节点
 */
public interface PostAuditHandler {

    /**
     * 执行本节点审核逻辑,向上下文累加风险分(不做最终裁决)
     *
     * @param context 审核上下文,读取待审核内容并回填风险分
     */
    void handle(AuditContext context);
}
