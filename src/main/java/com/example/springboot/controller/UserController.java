package com.example.springboot.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.RoleEnum;
import com.example.springboot.common.ValidationEnum;
import com.example.springboot.config.AuthAccess;
import com.example.springboot.controller.dto.UserDTO;
import com.example.springboot.controller.dto.UserPasswordDTO;
import com.example.springboot.common.Constants;
import com.example.springboot.common.Result;
import com.example.springboot.entity.User;
import com.example.springboot.entity.Validation;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.service.IUserService;
import com.example.springboot.service.IValidationService;
import com.example.springboot.utils.TokenUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Value("${files.upload.path}")
    private String filesUploadPath;

    @Resource
    private IUserService userService;

    @Resource
    private IValidationService validationService;

    String DEFAULT_PASS = "123456";

    @PostMapping("/login")
    public Result login(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        UserDTO dto = userService.login(userDTO);
        return Result.success(dto);
    }

    @PostMapping("/register")
    public Result register(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        return Result.success(userService.register(userDTO));
    }


    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody User user) {
        if (user.getId() == null && user.getPassword() == null) {  // 新增用户默认密码
            user.setPassword( SecureUtil.md5(DEFAULT_PASS));
        }
        if(StrUtil.isNotBlank(user.getEmail())){
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email", user.getEmail());
            User one = userService.getOne(queryWrapper);
            if(one != null && !one.getId().equals(user.getId())){
                throw new ServiceException(Constants.CODE_600,"邮箱已存在");
            }
        }
        return Result.success(userService.saveOrUpdate(user));
    }

    /**
     * 修改密码
     * @param userPasswordDTO
     * @return
     */
    @AuthAccess
    @PostMapping("/password")
    public Result password(@RequestBody UserPasswordDTO userPasswordDTO) {
        userPasswordDTO.setPassword(SecureUtil.md5(userPasswordDTO.getPassword()));
        userPasswordDTO.setNewPassword(SecureUtil.md5(userPasswordDTO.getNewPassword()));
        userService.updatePassword(userPasswordDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(userService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(userService.removeByIds(ids));
    }

    @GetMapping
    public Result findAll() {
        return Result.success(userService.list());
    }

    @GetMapping("/role/{role}")
    public Result findUsersByRole(@PathVariable String role) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role", role);
        List<User> list = userService.list(queryWrapper);
        return Result.success(list);
    }


    @GetMapping("/member/{office}")
    public Result findUsersByOffice(@PathVariable String office) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("office", office);
        List<User> list = userService.list(queryWrapper);
        return Result.success(list);
    }


    @PostMapping("/member/add_admin/{id}")
    public Result setOfficeAdmin(@PathVariable Integer id){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.ne("role", RoleEnum.ROLE_ADMIN.toString());

        User one = userService.getById(id);
        one.setRole(RoleEnum.ROLE_OFFICE_ADMIN.toString());

        userService.update(one, queryWrapper);
        return Result.success();
    }


    @PostMapping("/member/remove_admin/{id}")
    public Result removeOfficeAdmin(@PathVariable Integer id){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        User one = userService.getById(id);
        queryWrapper.eq("role", RoleEnum.ROLE_OFFICE_ADMIN.toString());
        one.setRole(RoleEnum.ROLE_LAWYER.toString());

        userService.update(one, queryWrapper);
        return Result.success();
    }


    @DeleteMapping("member/del/{id}")
    public Result delMember(@PathVariable Integer id){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("role", RoleEnum.ROLE_LAWYER.toString());
        User one = userService.getById(id);
        one.setOffice(" ");

        userService.update(one,queryWrapper);

        return Result.success();
    }

    @PostMapping("member/del/batch")
    public Result delMemberBatch(@RequestBody List<Integer> ids){
        for (Integer id : ids){
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", id);
            queryWrapper.eq("role", RoleEnum.ROLE_LAWYER.toString());
            User one = userService.getById(id);
            one.setOffice(" ");

            userService.update(one,queryWrapper);
        }
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(userService.getById(id));
    }

    @GetMapping("/username/{username}")
    public Result findByUsername(@PathVariable String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return Result.success(userService.getOne(queryWrapper));
    }

    @GetMapping("/avatar/{username}")
    public Result findAvatarByUsername(@PathVariable String username){
        User user = userService.getAvatarByUsername(username);
        return Result.success(user);
    }

    @GetMapping("/lawyer")
    public Result findLawyer(@RequestParam Integer pageNum,
                             @RequestParam Integer pageSize,
                             @RequestParam (defaultValue = "") String text,
                             @RequestParam (defaultValue = "") String office,
                             @RequestParam (defaultValue = "") String domain){

        return Result.success( userService.getLawyer(new Page<>(pageNum, pageSize),text,office,domain));
    }

    @GetMapping("/domain")
    public Result findAllDomain(){

        List<String> domains = userService.getDomain();

        return Result.success(domains);
    }

    @GetMapping("/lawyerDetail/{username}")
    public Result findLawyerDetail(@PathVariable String username){

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userService.getOne(queryWrapper);
        user.setPassword(null);
        return Result.success(user);
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String username,
                           @RequestParam(defaultValue = "") String nickname,
                           @RequestParam(defaultValue = "") String role,
                           @RequestParam(defaultValue = "") String office) {

        User token = TokenUtils.getCurrentUser();

        if (token != null && !token.getRole().equals(RoleEnum.ROLE_ADMIN.toString())){
            if(StrUtil.isNotBlank(token.getOffice())){
                office = token.getOffice();
            }
            else{
                office = "null";
            }

        }

        return Result.success(userService.findPage(new Page<>(pageNum, pageSize), username, nickname, role ,office));
    }

    /**
     * 导出接口
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        List<User> list = userService.list();
        // 通过工具类创建writer 写出到磁盘路径
//        ExcelWriter writer = ExcelUtil.getWriter(filesUploadPath + "/用户信息.xlsx");
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //自定义标题别名
        writer.addHeaderAlias("username", "用户名");
        writer.addHeaderAlias("password", "密码");
        writer.addHeaderAlias("nickname", "昵称");
        writer.addHeaderAlias("email", "邮箱");
        writer.addHeaderAlias("phone", "电话");
        writer.addHeaderAlias("address", "地址");
        writer.addHeaderAlias("createTime", "创建时间");
        writer.addHeaderAlias("avatarUrl", "头像");

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();

    }

    /**
     * excel 导入
     * @param file
     * @throws Exception
     */
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        // 方式1：(推荐) 通过 javabean的方式读取Excel内的对象，但是要求表头必须是英文，跟javabean的属性要对应起来
//        List<User> list = reader.readAll(User.class);

        // 方式2：忽略表头的中文，直接读取表的内容
        List<List<Object>> list = reader.read(1);
        List<User> users = CollUtil.newArrayList();
        for (List<Object> row : list) {
            User user = new User();
            user.setUsername(row.get(0).toString());
            user.setPassword(row.get(1).toString());
            user.setNickname(row.get(2).toString());
            user.setEmail(row.get(3).toString());
            user.setPhone(row.get(4).toString());
            user.setAddress(row.get(5).toString());
            user.setAvatarUrl(row.get(6).toString());
            users.add(user);
        }

        userService.saveBatch(users);
        return Result.success(true);
    }

    // 邮箱登录
    @AuthAccess
    @PostMapping("/loginEmail")
    public Result loginEmail(@RequestBody UserDTO userDTO) {
        String email = userDTO.getEmail();
        String code = userDTO.getCode();

        if (StrUtil.isBlank(email) || StrUtil.isBlank(code)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        UserDTO dto = userService.loginEmail(userDTO);
        return Result.success(dto);
    }

    // 忘记密码 | 重置密码
    @AuthAccess
    @PutMapping("/forget")
    public Result reset(@RequestBody UserPasswordDTO userPasswordDTO) {
        if (StrUtil.isBlank(userPasswordDTO.getEmail()) || StrUtil.isBlank(userPasswordDTO.getCode())) {
            throw new ServiceException(Constants.CODE_600, "参数异常");
        }
        // 先查询 邮箱验证的表，看看之前有没有发送过  邮箱code，如果不存在，就重新获取
        QueryWrapper<Validation> validationQueryWrapper = new QueryWrapper<>();
        validationQueryWrapper.eq("email", userPasswordDTO.getEmail());
        validationQueryWrapper.eq("code", userPasswordDTO.getCode());
        Validation one = validationService.getOne(validationQueryWrapper);
        if (one == null) {
            throw new ServiceException(Constants.CODE_600, "验证码错误");
        }

        validationQueryWrapper.ge("time", new Date());  // 查询数据库没过期的code, where time >= new Date()
        one = validationService.getOne(validationQueryWrapper);
        if (one == null) {
            throw new ServiceException(Constants.CODE_600, "验证码过期，请重新获取");
        }

        // 如果验证通过了，就查询要不过户的信息
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("email", userPasswordDTO.getEmail());  //存根据email查询用户信息
        User user = userService.getOne(userQueryWrapper);

        // 重置密码
        user.setPassword(SecureUtil.md5(DEFAULT_PASS));
        userService.updateById(user);
        BeanUtil.copyProperties(user, userPasswordDTO, true);
        userPasswordDTO.setPassword(DEFAULT_PASS);
        return Result.success(userPasswordDTO);
    }

    // 邮箱认证
    @AuthAccess
    @PostMapping("/authenticateEmail")
    public Result authenticateEmail(@RequestBody UserDTO userDTO) {
        String email = userDTO.getEmail();
        String code = userDTO.getCode();

        if (StrUtil.isBlank(email) || StrUtil.isBlank(code)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        userDTO.setUsername(TokenUtils.getCurrentUser().getUsername());
        userService.authenticateEmail(userDTO);
        return Result.success();
    }

    // 发送邮箱验证码
    @AuthAccess
    @GetMapping("/email/{email}/{type}")
    public Result sendEmailCode(@PathVariable String email, @PathVariable Integer type) throws MessagingException {
        if(StrUtil.isBlank(email)) {
            throw new ServiceException(Constants.CODE_400, "参数错误");
        }
        if(type == null) {
            throw new ServiceException(Constants.CODE_400, "参数错误");
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("email", email);
        User user = userService.getOne(userQueryWrapper);
        if(type.equals(ValidationEnum.AUTHENTICATE.getCode())){
            if(user != null){
                throw new ServiceException(Constants.CODE_600,"该邮箱已绑定用户");
            }
        } else if(user == null) {
            throw new ServiceException(Constants.CODE_600,"该邮箱未绑定用户");
        }
        userService.sendEmailCode(email, type);
        return Result.success();
    }



}

