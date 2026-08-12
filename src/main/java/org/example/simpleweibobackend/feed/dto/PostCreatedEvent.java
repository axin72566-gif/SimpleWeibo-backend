package org.example.simpleweibobackend.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCreatedEvent implements Serializable {

    private Long postId;

    private Long userId;
}
