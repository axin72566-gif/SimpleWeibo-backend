package org.example.simpleweibobackend.post.create.audit;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.post.create.audit.handler.PostAuditHandler;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 发帖审核责任链：按 {@code @Order} 顺序执行所有节点累加风险分，返回携带总分的上下文。
 */
@Component
@RequiredArgsConstructor
public class PostAuditChain {

    private final List<PostAuditHandler> handlers;

    /**
     * 依次执行所有审核节点,各节点向上下文累加风险分;
     * 最终由裁决节点({@code JudgeAuditHandler})按总分决定通过或抛异常拒绝
     *
     * @param context 审核上下文,携带待审核内容并回填风险总分
     */
    public void audit(AuditContext context) {
        for (PostAuditHandler handler : handlers) {
            handler.handle(context);
        }
    }
}
