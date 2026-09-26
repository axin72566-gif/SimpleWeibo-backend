package org.example.simpleweibobackend.post.create.audit;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuditResultEnum {
    PASS(1, "通过"),
    MANUAL_REVIEW(2, "人工审核中"),
    REJECT(3, "拒绝");

    private final Integer code;

    private final String desc;
}