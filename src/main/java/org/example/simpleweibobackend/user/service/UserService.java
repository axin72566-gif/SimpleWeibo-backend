package org.example.simpleweibobackend.user.service;

import org.example.simpleweibobackend.user.entity.User;

import java.util.List;

public interface UserService {

    List<User> listAll();

    User getById(Long id);
}
