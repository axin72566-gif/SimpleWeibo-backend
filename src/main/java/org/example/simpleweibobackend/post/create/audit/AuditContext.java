package org.example.simpleweibobackend.post.create.audit;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** 审核上下文 */
@Data
@Builder
public class AuditContext {

    private String title;

    private String content;

    /** 累计风险分 */
    @Builder.Default
    private Long riskScore = 0L;

    @Builder.Default
    private List<String> reasons = new ArrayList<>();

}
