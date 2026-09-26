package org.example.simpleweibobackend.post.create.audit.handler.remote;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.post.create.audit.AuditContext;
import org.example.simpleweibobackend.post.create.audit.handler.PostAuditHandler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@Slf4j
public class RemoteCheckHandler implements PostAuditHandler {

    private static final String REMOTE_CHECK_REASON = "远程校验风险分超过阈值";

    @Override
    public void handle(AuditContext context) {
        try {
            long remoteRiskScore = RandomUtil.randomLong(0, 100);
            if (remoteRiskScore >= 60) {
                context.getReasons().add(REMOTE_CHECK_REASON);
            }
            context.setRiskScore(context.getRiskScore() + remoteRiskScore);
        } catch (Exception e) {
            log.error("远程校验失败", e);
            context.setRiskScore(context.getRiskScore() + 10);
        }
    }
}
