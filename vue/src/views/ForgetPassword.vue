<template>
  <div class="wrapper">
    <div class="login_card">
      <div style="color: #5acce6;margin: 20px 0; text-align: center; font-size: 24px"><b>找回密码</b></div>
      <el-form :model="user" :rules="emailRules" ref="emailForm" >
        <el-form-item prop="email">
          <el-input size="medium" prefix-icon="el-icon-message" v-model="user.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input size="medium" prefix-icon="el-icon-lock" placeholder="请输入验证码"
                    style="width: 60%" v-model="user.code"></el-input>
          <el-button type="primary" class="ml-5" size="small" style="margin-left: 10px" @click="sendEmailCode">发送验证码</el-button>
        </el-form-item>
      </el-form>
      <el-form :model="pass" :rules="passwordRules" ref="passwordForm">
        <el-form-item prop="newPassword">
          <el-input placeholder="请输入密码" size="medium" prefix-icon="el-icon-lock" show-password v-model="pass.newPassword"></el-input>
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input placeholder="请确认密码" size="medium" prefix-icon="el-icon-lock" show-password v-model="pass.confirmPassword"></el-input>
        </el-form-item>
        <el-form-item style="margin: 5px 0; text-align: right">
          <el-button type="primary" size="small"  autocomplete="off" @click="forgetPassword">修改密码</el-button>
          <el-button type="warning" size="small"  autocomplete="off" @click="redirectToLogin">返回登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import {setRoutes} from "@/router";


export default {
  name: "ForgetPassword",
  data() {
    const checkEmail = (rule, value, callback) => {
      if (value && !/^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.(com|cn|net)$/.test(value))  {
        callback(new Error('请输入正确邮箱'));
      }
      callback();
    };
    return {
      user: {},
      pass: {},
      emailRules: {
        email: [
          {required: true, message: '请输入邮箱', trigger: 'blur'},
          {validator:checkEmail, trigger: 'blur'}
        ]
      },
      passwordRules: {
        newPassword: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
        ],
      },

    }
  },

  methods: {
    forgetPassword() {
      this.$refs['emailForm'].validate((valid) => {
        if (valid) {  // 表单校验合法
          this.request.put("/user/forget", this.user).then(res => {
            if (res.code === '200') {
              this.pass.password = res.data.password
              this.pass.username = res.data.username
              this.updatePassword()
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      });
    },
    sendEmailCode(){
      this.$refs['emailForm'].validate((valid) => {
        if (valid) {  // 表单校验合法
          this.request.get("user/email/"+this.user.email+"/"+304).then(res =>{
            if(res.code === "200"){
              this.$message.success("发送成功")
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      });
    },
    updatePassword(){
      this.$refs['passwordForm'].validate((valid) => {
        if (valid) {  // 表单校验合法
          if (this.pass.newPassword !== this.pass.confirmPassword) {
            this.$message.error("2次输入的新密码不相同")
            return false
          }
          this.request.post("/user/password", this.pass).then(res => {
            if (res.code === '200') {
              this.$message.success("修改成功")
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      })
    },
    redirectToLogin() {
      // 使用 this.$router.push('/login') 来跳转到登录页面
      // 在跳转后，使用 this.$router.go(0) 来刷新页面
      this.$router.push('/login').then(() => {
        this.$router.go(0); // 刷新页面
      });
    }



  }

}
</script>

<style>
.wrapper{
  width: 100%;
  height: 100%;
  position: absolute;
  background-image: url("../../src/assets/背景.png");
  background-size: cover; /* 设置背景图片尺寸 */
  background-position: center; /* 设置背景图片位置 */
  z-index: 0;
}

.login_card {
  position: relative;

  background-color: white;
  width: 30%;
  height: 60%;
  padding: 20px;
  margin: 100px 400px;
  border-radius: 15px;
  z-index: 2;

  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2), 0 0 40px rgba(0, 0, 0, 0.1);

  border: 1px solid rgba(0, 0, 0, 0.1);
}


</style>
