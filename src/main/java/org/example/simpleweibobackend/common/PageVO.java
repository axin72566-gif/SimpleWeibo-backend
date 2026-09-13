package org.example.simpleweibobackend.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 统一分页响应体
 *
 * @param <T> 记录类型
 */
@Data
@AllArgsConstructor
public class PageVO<T> {

    /**
     * 当前页记录列表
     */
    private List<T> records;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页码,从 1 开始
     */
    private int page;

    /**
     * 每页条数
     */
    private int size;

    /**
     * 快速构建分页对象
     */
    public static <T> PageVO<T> of(List<T> records, long total, int page, int size) {
        return new PageVO<>(records, total, page, size);
    }
}
