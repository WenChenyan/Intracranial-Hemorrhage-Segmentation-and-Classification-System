<template >
  <div  class="wrapper" >
    <div style="color: #ffffff;margin: 6% 0 0 0; text-align: center; font-size: 50px;opacity: 1;font-"><b> 颅内出血分割与分型系统</b></div>
    <div class="container" :class="{ 'right-panel-active': isSignUp }" style="margin-left: 8%;margin-top: 6%">
      <div class="form-container sign-up-container" >
        <el-form :model="user" :rules="rules" ref="userForm" style="margin:0 10px 10px 10px;">
          <div style="color: #5acce6;margin: 20px 0; text-align: center; font-size: 24px;opacity: 1"><b> 注 册 账 号</b></div>
          <el-form-item prop="username">
            <el-input placeholder="请输入账号" size="medium" prefix-icon="el-icon-user" v-model="user.username"></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input placeholder="请输入密码" size="medium" prefix-icon="el-icon-lock" show-password v-model="user.password"></el-input>
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input placeholder="请确认密码" size="medium" prefix-icon="el-icon-lock" show-password v-model="user.confirmPassword"></el-input>
          </el-form-item>
          <el-form-item style="margin: 5px 0; text-align: right">
            <el-button type="primary" size="small" autocomplete="off" @click="login" style="padding: 12px 30px;">注 册</el-button>
          </el-form-item>
        </el-form>

      </div>
      <div class="form-container sign-in-container">

        <div class = "login_card">
          <div style="color: #5acce6;margin: 90px 0 20px 0; text-align: center; font-size: 24px;opacity: 1"><b>用 户 登 录</b></div>
          <div style="width: 350px ;margin: auto auto">
            <el-tabs type="card" stretch="ture">
              <el-tab-pane label="账号登陆">
                <el-form :model="user" :rules="userRules" ref="userForm">
                  <el-form-item prop="username">
                    <el-input size="medium" prefix-icon="el-icon-user" v-model="user.username" placeholder="请输入用户名"></el-input>
                  </el-form-item>
                  <el-form-item prop="password">
                    <el-input size="medium" prefix-icon="el-icon-lock" show-password placeholder="请输入密码"
                              v-model="user.password"></el-input>
                  </el-form-item>
                  <el-form-item style="margin: 5px 0; text-align: right;">
                    <el-button type="warning" size="small" autocomplete="off" @click="$router.push('/forgetPassword')" style="padding: 12px 15px;margin-right: 15px;"> 忘记密码</el-button>
                    <el-button type="primary" size="small" autocomplete="off" @click="usernameLogin" style="padding: 12px 30px;" >登 录</el-button>
                  </el-form-item>
                </el-form>

              </el-tab-pane>

              <el-tab-pane label="邮箱登陆">
                <el-form :model="user" :rules="emailRules" ref="emailForm" >
                  <el-form-item prop="email">
                    <el-input size="medium" prefix-icon="el-icon-message" v-model="user.email" placeholder="请输入邮箱"></el-input>
                  </el-form-item>
                  <el-form-item prop="code">
                    <el-input size="medium" prefix-icon="el-icon-lock" placeholder="请输入验证码"
                              style="width: 100%" v-model="user.code"></el-input>

                  </el-form-item>
                  <el-form-item style="margin: 5px 0; text-align: right">
                    <el-button type="primary" class="ml-5" size="small" style="padding: 12px 15px;" @click="sendEmailCode">发送验证码</el-button>
                    <el-button type="primary" size="small" autocomplete="off" @click="emailLogin" style="padding: 12px 30px;">登 录</el-button>
                  </el-form-item>
                </el-form>
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>
      </div>
      <div class="overlay-container">
        <div class="overlay">
          <div class="overlay-panel overlay-left">
            <h1>已有账号？</h1>
            <p>使用您的账号进行登录</p>
            <button class="ghost" @click="toggleSignIn">登录</button>
          </div>
          <div class="overlay-panel overlay-right">
            <h1>没有账号?</h1>
            <p>立即注册加入我们</p>
            <button class="ghost" @click="toggleSignUp">注册</button>
          </div>
        </div>
      </div>
    </div>
  </div>

</template>

<script>
import {setRoutes} from "@/router";


export default {
  name: "Login",
  data() {
    const checkEmail = (rule, value, callback) => {
      if (value && !/^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.(com|cn|net)$/.test(value))  {
        callback(new Error('请输入正确邮箱'));
      }
      callback();
    };
    return {
      isSignUp: false,
      user: {},
      userRules: {
        username: [
          {required: true, message: '请输入用户名', trigger: 'blur'},
          {min: 3, max: 10, message: '长度在 3 到 5 个字符', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'},
          {min: 6, max: 18, message: '长度在 6 到 18 个字符', trigger: 'blur'}
        ],
      },
      emailRules: {
        email: [
          {required: true, message: '请输入邮箱', trigger: 'blur'},
          {validator:checkEmail, trigger: 'blur'}
        ],
        code:[
          {required: true, message: '请输入验证码', triangle: 'blur'},
          {min:6 ,max:6 , message: '验证码为6位' ,triangle: 'change'}
        ]
      },
      texts: ["脑出血图像的精准分割和分类", "3D建模可视化", "论坛交流"], // 存储要展示的文字数组
      currentIndex: 0, // 当前展示文字的索引
      currentText: "", // 当前展示的文字
      showText: true ,// 控制文字显示与隐藏
    };
  },
  mounted() {
    // 在组件挂载后开始动画
    this.startAnimation();
    //背景处理
  },
  methods: {

    toggleSignUp() {
      this.isSignUp = true;
    },
    toggleSignIn() {
      this.isSignUp = false;
    },
    usernameLogin() {
      this.$refs['userForm'].validate((valid) => {
        if (valid) {  // 表单校验合法
          this.request.post("/user/login", this.user).then(res => {
            if (res.code === '200') {
              this.$message.success({message:"登录成功" , duration:1500})

              sessionStorage.setItem("user", JSON.stringify(res.data))  // 存储用户信息到浏览器
              sessionStorage.setItem("menus", JSON.stringify(res.data.menus))  // 存储用户信息到浏览器
              sessionStorage.setItem("role", JSON.stringify(res.data.role))
              // 动态设置当前用户的路由
              setRoutes()

              if (res.data.role !== 'ROLE_ADMIN') {
                this.$router.push("/front/home")
              } else {
                this.$router.push("/")
              }
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      });
    },
    emailLogin() {
      this.$refs['emailForm'].validate((valid) => {
        if (valid) {  // 表单校验合法
          this.request.post("/user/loginEmail", this.user).then(res => {
            if (res.code === '200') {
              this.$message.success({message:"登录成功" , duration:1500})

              sessionStorage.setItem("user", JSON.stringify(res.data))  // 存储用户信息到浏览器
              sessionStorage.setItem("menus", JSON.stringify(res.data.menus))  // 存储用户信息到浏览器
              sessionStorage.setItem("role", JSON.stringify(res.data.role))
              // 动态设置当前用户的路由
              setRoutes()

              if (res.data.role !== 'ROLE_ADMIN') {
                this.$router.push("/front/home")
              } else {
                this.$router.push("/")
              }
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      });
    },
    sendEmailCode(){
      this.request.get("user/email/"+this.user.email+"/"+303).then(res =>{
        if(res.code === "200"){
          this.$message.success("发送成功")
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    login() {
      this.$refs['userForm'].validate((valid) => {
        if (valid) {  // 表单校验合法
          if (this.user.password !== this.user.confirmPassword) {
            this.$message.error("两次输入的密码不一致")
            return false
          }
          this.request.post("/user/register", this.user).then(res => {
            if(res.code === '200') {
              this.$message.success("注册成功")
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      });
    },



  },

};
</script>

<style scoped>
* {
  box-sizing: border-box;
}
body {
  font-family: 'Montserrat',sans-serif;
  background-image: linear-gradient(120deg,#3498db,#8e44ad);;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  margin: -20px 0 50px;
}

h1 {
  font-weight: bold;
  margin: 0;
}
p {
  font-size: 14px;
  line-height: 20px;
  letter-spacing: .5px;
  margin: 20px 0 30px;
}
span {
  font-size: 12px;
}
a {
  color: #333;
  font-size: 14px;
  text-decoration: none;
  margin: 15px 0;
}
.container {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 14px 28px rgba(0, 0, 0, .25), 0 10px 10px rgba(0, 0, 0, .22);
  position: absolute;
  overflow: hidden;
  width: 768px;
  max-width: 100%;
  min-height: 480px;
  top:10%;
  left: 20%;

}

.form-container form {
  background: #fff;
  display: flex;
  flex-direction: column;
  height: 100%;
  justify-content: center;
  text-align: center;
}
.social-container {
  margin: 20px 0;
}

.social-container a {
  border: 1px solid #ddd;
  border-radius: 50%;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  margin: 0 5px;
  height: 40px;
  width: 40px;
}

.social-container a:hover {
  background-color: #eee;

}

.txtb {
  border-bottom: 2px solid #adadad;
  position: relative;
  margin: 10px 0;
}

.txtb input {
  font-size: 15px;
  color: #333;
  border: none;
  width: 100%;
  outline: none;
  background: none;
  padding: 0 3px;
  height: 35px;
}

.txtb span::before {
  content: attr(data-placeholder);
  position: absolute;
  top: 50%;
  left: 5px;
  color: #adadad;
  transform: translateY(-50%);
  transition: .5s;
}
.txtb span::after {
  content: '';
  position: absolute;
  left: 0%;
  top: 100%;
  width: 0%;
  height: 2px;
  background-image: linear-gradient(120deg,#3498db,#8e44ad);
  transition: .5s;
}

.focus + span::before {
  top: -5px;
}

.focus + span::after {
  width: 100%;
}

button {
  border-radius: 20px;
  border: 1px solid #ff4b2b;
  background: #ff4b2b;
  color: #fff;
  font-size: 12px;
  font-weight: bold;
  padding: 12px 45px;
  letter-spacing: 1px;
  text-transform: uppercase;
  transition: transform 80ms ease-in;
  cursor: pointer;
}

button:active {
  transform: scale(.95);
}

button:focus {
  outline: none;
}

button.ghost {
  background: transparent;
  border-color: #fff;
}

.form-container {
  position: absolute;
  top: 0;
  height: 100%;
  transition: all .6s ease-in-out;
}

.form-container button {
  background: linear-gradient(120deg, #3498db, #8e44ad);
  border: none;
  background-size: 200%;
  color: #fff;
  transition: .5s;
}

.form-container button:hover {
  background-position: right;
}

.sign-in-container {
  left: 0;
  width: 50%;
  z-index: 2;
}
.sign-in-container form a {
  position: relative;
  left: 100px;
}
.sign-up-container {
  left: 0;
  width: 50%;
  z-index: 1;
  opacity: 0;
}
.sign-up-container button {
  margin-top: 20px;
}
.overlay-container {
  position:absolute;
  top: 0;
  left: 50%;
  width: 50%;
  height: 100%;
  overflow: hidden;
  transition: transform .6s ease-in-out;
  z-index: 100;
}
.overlay {
  background-image: linear-gradient(120deg,#3498db,#8e44ad);
  color: #fff;
  position: relative;
  left: -100%;
  height: 100%;
  width: 200%;
  transform: translateY(0);
  transition: transform .6s ease-in-out;
}
.overlay-panel {
  position: absolute;
  top: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 0 40px;
  height: 100%;
  width: 50%;
  text-align: center;
  transform: translateY(0);
  transition: transform .6s ease-in-out;
}
.overlay-right {
  right: 0;
  transform: translateY(0);

}

.overlay-left {
  transform: translateY(-20%);
}

.container.right-panel-active .sign-in-container {
  transform: translateY(100%);
}

.container.container.right-panel-active .overlay-container {
  transform: translateX(-100%);
}
.container.right-panel-active .sign-up-container {
  transform: translateX(100%);
  opacity: 1;
  z-index: 5;
}
.container.container.right-panel-active .overlay {
  transform: translateX(50%);
}
.container.container.right-panel-active .overlay-left {
  transform: translateY(0);
}
.container.container.right-panel-active .overlay-right {
  transform: translateY(20%);
}
.wrapper {
  height: 100vh;
  background-image: linear-gradient(to bottom right, #5193bb, #4ace7e);
  overflow: hidden;
}

</style>
