package org.example.simpleweibobackend.post.query;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.PostMapper;
import org.example.simpleweibobackend.post.PostVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 帖子查询服务
 */
@Service
@RequiredArgsConstructor
public class PostQueryService {

    private final PostMapper postMapper;

    /**
     * 查询帖子详情,并将浏览量在数据库端原子自增。
     * setSql 中的 "update_time = update_time" 用于阻止 MySQL 的
     * ON UPDATE CURRENT_TIMESTAMP 刷新更新时间
     *
     * @param id 帖子 ID
     * @return 帖子详情
     * @throws BizException 帖子不存在(404)或浏览量更新失败(500)
     */
    @Transactional
    public PostVO getPostById(Long id) {
        int updated = postMapper.update(null, new LambdaUpdateWrapper<Post>()
                .eq(Post::getId, id)
                .setSql("view_count = view_count + 1, update_time = update_time"));
        if (updated == 0) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "更新帖子视图次数失败");
        }

        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "帖子不存在");
        }
        return PostVO.from(post);
    }
}
