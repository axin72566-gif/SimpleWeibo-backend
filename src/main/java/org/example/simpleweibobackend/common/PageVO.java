package org.example.simpleweibobackend.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageVO<T> {

    private List<T> records;
    private long total;
    private int page;
    private int size;

    public static <T> PageVO<T> of(List<T> records, long total, int page, int size) {
        return new PageVO<>(records, total, page, size);
    }
}
