package com.example.springboot.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.controller.dto.UserPasswordDTO;
import com.example.springboot.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


public interface UserMapper extends BaseMapper<User> {

    @Update("update sys_user set password = #{newPassword} where username = #{username} and password = #{password}")
    int updatePassword(UserPasswordDTO userPasswordDTO);

    Page<User> findPage(Page<User> page, @Param("username") String username, @Param("nickname") String nickname,
                        @Param("role") String role, @Param("office") String office);

    @Select("select username,nickname,avatar_url from sys_user where username = #{username}")
    User findAvatar(String username);


    Page<User> findLawyer(Page<User> page, @Param("text") String text,@Param("office") String office,@Param("domain") String domain);

    @Select("select distinct domain from sys_user where domain is not null order by domain ")
    List<String> getDomain();
}
