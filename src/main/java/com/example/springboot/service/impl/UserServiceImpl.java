package com.example.springboot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.common.Constants;
import com.example.springboot.common.RoleEnum;
import com.example.springboot.common.ValidationEnum;
import com.example.springboot.controller.dto.UserDTO;
import com.example.springboot.controller.dto.UserPasswordDTO;
import com.example.springboot.entity.Menu;
import com.example.springboot.entity.User;
import com.example.springboot.entity.Validation;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.RoleMapper;
import com.example.springboot.mapper.RoleMenuMapper;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.service.IMenuService;
import com.example.springboot.service.IUserService;
import com.example.springboot.service.IValidationService;
import com.example.springboot.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final Log LOG = Log.get();

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private IMenuService menuService;

    @Resource
    private IValidationService validationService;

    @Override
    public UserDTO login(UserDTO userDTO) {
        // 用户密码 md5加密
        userDTO.setPassword(SecureUtil.md5(userDTO.getPassword()));
        User one = getUserInfo(userDTO);
        if (one != null) {

            if(!one.getEnable()){
                throw new ServiceException(Constants.CODE_600, "账号被封禁请联系管理员");
            }

            BeanUtil.copyProperties(one, userDTO, true);
            // 设置token
            String token = TokenUtils.genToken(one.getId().toString(), one.getPassword());
            userDTO.setToken(token);

            String role = one.getRole(); // ROLE_ADMIN
            // 设置用户的菜单列表
            List<Menu> roleMenus = getRoleMenus(role);
            userDTO.setMenus(roleMenus);
            return userDTO;
        } else {
            throw new ServiceException(Constants.CODE_600, "用户名或密码错误");
        }
    }

    @Override
    public User register(UserDTO userDTO) {
        // 用户密码 md5加密
        userDTO.setPassword(SecureUtil.md5(userDTO.getPassword()));
        User one = getUserInfo(userDTO);
        if (one == null) {
            one = new User();
            BeanUtil.copyProperties(userDTO, one, true);
            // 默认一个普通用户的角色
            one.setRole(RoleEnum.ROLE_USER.toString());
            if (one.getNickname() == null) {
                one.setNickname(one.getUsername());
            }
            save(one);  // 把 copy完之后的用户对象存储到数据库
        } else {
            throw new ServiceException(Constants.CODE_600, "用户已存在");
        }
        return one;
    }

    @Override
    public void updatePassword(UserPasswordDTO userPasswordDTO) {
        int update = userMapper.updatePassword(userPasswordDTO);
        if (update < 1) {
            throw new ServiceException(Constants.CODE_600, "密码错误");
        }
    }

    @Override
    public Page<User> findPage(Page<User> page, String username, String nickname,String role, String office) {
        return userMapper.findPage(page, username, nickname, role, office);
    }

    @Override
    public User getAvatarByUsername(String username) {
        return userMapper.findAvatar(username);
    }

    @Override
    public Page<User> getLawyer(Page<User> page,String text ,String office ,String domain) {

        return userMapper.findLawyer(page,text,office,domain);
    }


    @Override
    public List<String> getDomain() {
        return userMapper.getDomain();
    }

    private User getUserInfo(UserDTO userDTO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userDTO.getUsername());
        queryWrapper.eq("password", userDTO.getPassword());
        User one;
        try {
            one = getOne(queryWrapper); // 从数据库查询用户信息
        } catch (Exception e) {
            LOG.error(e);
            throw new ServiceException(Constants.CODE_500, "系统错误");
        }
        return one;
    }

    /**
     * 获取当前角色的菜单列表
     * @param roleFlag
     * @return
     */
    private List<Menu> getRoleMenus(String roleFlag) {
        Integer roleId = roleMapper.selectByFlag(roleFlag);
        // 当前角色的所有菜单id集合
        List<Integer> menuIds = roleMenuMapper.selectByRoleId(roleId);

        // 查出系统所有的菜单(树形)
        List<Menu> menus = menuService.findMenus("");
        // new一个最后筛选完成之后的list
        List<Menu> roleMenus = new ArrayList<>();
        // 筛选当前用户角色的菜单
        for (Menu menu : menus) {
            if (menuIds.contains(menu.getId())) {
                roleMenus.add(menu);
            }
            List<Menu> children = menu.getChildren();
            // removeIf()  移除 children 里面不在 menuIds集合中的 元素
            children.removeIf(child -> !menuIds.contains(child.getId()));
        }
        return roleMenus;
    }

    // 不要忘记导入相关的依赖和配置
    @Autowired
    JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    // 邮箱验证
    @Override
    public UserDTO loginEmail(UserDTO userDTO) {
        String email = userDTO.getEmail();
        String code = userDTO.getCode();

        // 先查询 邮箱验证的表，看看之前有没有发送过  邮箱code，如果不存在，就重新获取
        QueryWrapper<Validation> validationQueryWrapper = new QueryWrapper<>();
        validationQueryWrapper.eq("email", email);
        validationQueryWrapper.eq("code", code);
        Validation one = validationService.getOne(validationQueryWrapper);
        if (one == null) {
            throw new ServiceException(Constants.CODE_600, "验证码错误");
        }
        validationQueryWrapper.ge("time", new Date());  // 查询数据库没过期的code, where time >= new Date()
        one = validationService.getOne(validationQueryWrapper);
        if (one == null) {
            throw new ServiceException(Constants.CODE_600, "验证码过期，请重新获取");
        }

        // 如果验证通过了
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("email", email);  //存根据email查询用户信息
        User user = getOne(userQueryWrapper);

        if (user == null) {
            throw new ServiceException(Constants.CODE_600, "未找到用户");
        }

        BeanUtil.copyProperties(user, userDTO, true);
        // 设置token
        String token = TokenUtils.genToken(user.getId().toString(), user.getPassword());
        userDTO.setToken(token);

        String role = user.getRole();
        // 设置用户的菜单列表
        List<Menu> roleMenus = getRoleMenus(role);
        userDTO.setMenus(roleMenus);
        return userDTO;
    }


    @Override
    public void authenticateEmail(UserDTO userDTO) {
        String username = userDTO.getUsername();
        String email = userDTO.getEmail();
        String code = userDTO.getCode();

        // 先查询 邮箱验证的表，看看之前有没有发送过  邮箱code，如果不存在，就重新获取
        QueryWrapper<Validation> validationQueryWrapper = new QueryWrapper<>();
        validationQueryWrapper.eq("email", email);
        validationQueryWrapper.eq("code", code);
        Validation one = validationService.getOne(validationQueryWrapper);
        if (one == null) {
            throw new ServiceException(Constants.CODE_600, "验证码错误");
        }
        validationQueryWrapper.ge("time", new Date());  // 查询数据库没过期的code, where time >= new Date()
        one = validationService.getOne(validationQueryWrapper);
        if (one == null) {
            throw new ServiceException(Constants.CODE_600, "验证码过期，请重新获取");
        }

        // 如果验证通过了
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", username);  //存根据email查询用户信息
        User user = getOne(userQueryWrapper);

        if (user == null) {
            throw new ServiceException(Constants.CODE_600, "未找到用户");
        }
        user.setEmail(email);
        update(user,userQueryWrapper);

    }

    // 发送邮箱验证码
    @Override
    public void sendEmailCode(String email, Integer type) throws MessagingException {
        Date now = new Date();
        // 先查询同类型code


        QueryWrapper<Validation> validationQueryWrapper = new QueryWrapper<>();
        validationQueryWrapper.eq("email", email);
        validationQueryWrapper.eq("type", type);
        validationQueryWrapper.ge("time", now);  // 查询数据库没过期的code
        Validation validation = validationService.getOne(validationQueryWrapper);
        if (validation != null) {
            throw new ServiceException(Constants.CODE_600, "当前您的验证码仍然有效，请不要重复发送");
        }


        String code = RandomUtil.randomNumbers(6); // 随机一个 6位长度的验证码

        if (ValidationEnum.LOGIN.getCode().equals(type)) {
            SimpleMailMessage message=new SimpleMailMessage();
            message.setFrom(from);  // 发送人
            message.setTo(email);
            message.setSentDate(now);
            message.setSubject("【法律咨询与交流系统】登录邮箱验证");
            message.setText("您本次登录的验证码是：" + code + "，有效期5分钟。请妥善保管，切勿泄露");
            javaMailSender.send(message);
        } else if (ValidationEnum.FORGET_PASS.getCode().equals(type)){
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setSubject("【法律咨询与交流系统】忘记密码验证");
            helper.setFrom(from);  // 发送人
            helper.setTo(email);
            helper.setSentDate(now);  // 富文本
            String context="<b>尊敬的用户：</b><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;您好，您本次忘记密码的验证码是："+
                    "<b color=\"'red'\">"  + code + "</b><br>"
                    +"，有效期5分钟。请妥善保管，切勿泄露";
            helper.setText(context, true);
            javaMailSender.send(message);
        } else if (ValidationEnum.AUTHENTICATE.getCode().equals(type)){
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(message);
            helper.setSubject("【法律咨询与交流系统】邮箱验证");
            helper.setFrom(from);  // 发送人
            helper.setTo(email);
            helper.setSentDate(now);  // 富文本
            String context="<b>尊敬的用户：</b><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;您好，您的验证码是："+
                    "<b color=\"'red'\">"  + code + "</b><br>"
                    +"，有效期5分钟。请妥善保管，切勿泄露";
            helper.setText(context, true);
            javaMailSender.send(message);
        }

        // 发送成功之后，把验证码存到数据库
        validationService.saveCode(email, code, type, DateUtil.offsetMinute(now, 5));
    }

}
