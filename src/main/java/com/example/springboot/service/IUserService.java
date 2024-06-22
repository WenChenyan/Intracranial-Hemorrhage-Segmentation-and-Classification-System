package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.controller.dto.UserDTO;
import com.example.springboot.controller.dto.UserPasswordDTO;
import com.example.springboot.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.mail.MessagingException;
import java.util.List;


public interface IUserService extends IService<User> {

    UserDTO login(UserDTO userDTO);

    User register(UserDTO userDTO);

    void updatePassword(UserPasswordDTO userPasswordDTO);

    Page<User> findPage(Page<User> objectPage, String username, String nickname, String email, String address);

    User getAvatarByUsername(String username);

    Page<User> getLawyer(Page<User> objectPage, String text ,String office ,String domain);

    List<String> getDomain();

    UserDTO loginEmail(UserDTO userDTO);

    void sendEmailCode(String email, Integer type)  throws MessagingException;

    void authenticateEmail(UserDTO userDTO);
}
