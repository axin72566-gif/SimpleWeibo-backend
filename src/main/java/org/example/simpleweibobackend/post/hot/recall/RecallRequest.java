package org.example.simpleweibobackend.post.hot.recall;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 召回条件。默认召回最近七天的微博。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecallRequest {

    /**
     * 召回起始时间，包含该时间点。
     */
    private LocalDateTime startTime = LocalDateTime.now().minusDays(7);

    /**
     * 召回结束时间，包含该时间点。
     */
    private LocalDateTime endTime = LocalDateTime.now();
}
