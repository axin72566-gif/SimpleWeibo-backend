package org.example.simpleweibobackend.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.simpleweibobackend.user.entity.User;

import java.util.List;

public interface UserService {

    List<User> listAll();

    User getById(Long id);

    IPage<User> page(long current, long size);
}
