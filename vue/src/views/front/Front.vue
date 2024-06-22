<template>
  <div>
<!--    头部-->
    <div style="display: flex; height: 60px; line-height: 60px; background-color: #398dcd;opacity: 0.7"  >
      <div style="width: 300px; display: flex; padding-left: 30px">
        <div style="width: 60px">
          <img src="../../assets/人工智能.svg" alt="" style="width: 50px; position: relative; top: 5px; right: 0">
        </div>
        <div style="flex: 1;color: white;font-size: large">颅内出血分割与分型系统</div>
      </div>
      <div style="flex: 1">

        <el-menu :default-active="'1'"
                 class="el-menu-demo"
                 mode="horizontal"
                 background-color="#398dcd"
                 style="opacity: 0.7"
                 text-color="#fff"
                 active-text-color="#ffd04b"
                 router>
          <el-menu-item index="/front/home">首页</el-menu-item>
          <el-menu-item index="/front/detail">AI 诊 断</el-menu-item>
          <el-menu-item index="/front/article">交流论坛</el-menu-item>

          <el-menu-item index="/front/im">
            结果存档
            <el-badge value="new" class="hidden" is-dot="" v-if="newMessage>0">
            <div style="height: 20px">
            </div>
            </el-badge>
          </el-menu-item>

          <el-menu-item v-if="user.role === 'ROLE_ADMIN'||user.role === 'ROLE_OFFICE_ADMIN'" @click="$router.push('/home')">管理端</el-menu-item>
        </el-menu>
      </div>
      <div style="width: 200px;height: 60px ">
        <div v-if="!user.username" style="text-align: right; padding-right: 30px">
          <el-button @click="$router.push('/login')">登录</el-button>
          <el-button @click="$router.push('/register')">注册</el-button>
        </div>
        <div v-else>
          <el-dropdown style="width: 150px; cursor: pointer; text-align: right">
            <div style="display: inline-block;color: #fff;">
              <img :src="user.avatarUrl" alt=""
                   style="object-fit: cover;width: 30px; height: 30px; border-radius: 50%; position: relative; top: 10px; right: 5px">
              <span>{{ user.nickname }}</span><i class="el-icon-arrow-down" style="margin-left: 5px"></i>
            </div>
            <el-dropdown-menu slot="dropdown" style="width: 100px; text-align: center">
              <el-dropdown-item style="font-size: 14px; padding: 5px 0">
                <div style="font-size: 14px; padding: 5px 0">
                  <router-link to="/front/password">修改密码</router-link>
                </div>
              </el-dropdown-item>
              <el-dropdown-item v-if="user.role !== 'ROLE_USER'" >
                <div style="font-size: 14px; padding: 5px 0" @click="$router.push('/front/lawyerDetail?username=' + user.username)">
                  <span style="text-decoration: none" >个人主页</span>
                </div>
              </el-dropdown-item>
              <el-dropdown-item >
                <div style="font-size: 14px; padding: 5px 0"  >
                  <router-link to="/front/person">个人信息</router-link>
                </div>
              </el-dropdown-item>
              <el-dropdown-item >
                <div style="font-size: 14px; padding: 5px 0" @click="logout">
                  <span style="text-decoration: none" >退出</span>
                </div>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </div>

    <div style=" min-height: calc(100vh - 80px)">
      <router-view @refreshUser="getUser" />
    </div>

  </div>
</template>

<script>
export default {
  name: "Front",
  data() {
    return {
      user: sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {},
      newMessage: 0
    }
  },
  created() {
    this.getNewMessage()
  },
  methods: {
    logout() {
      this.$store.commit("logout")
      this.$message.success("退出成功")
    },
    getUser() {
      let username = this.user.username
      if (username) {
        // 从后台获取User数据
        this.request.get("/user/username/" + username).then(res => {
          // 重新赋值后台的最新User数据
          this.user = res.data
        })
      }

    },
    getNewMessage(){
      let username = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")).username : ""
        this.request.get("/contact/newMessage/"+username).then(res =>{
          this.newMessage = res.data
        } )
    }
  }
}
</script>

<style>
.item{
  display: inline-block;
  width: 100px;

  text-align: center;
  cursor: pointer
}
.el-menu-item.is-active {
  background-color: rgb(38, 52, 69) !important;
}
.el-menu-item:hover {
  background-color: rgb(38, 52, 69) !important;
}

.el-submenu__title:hover {
  background-color: rgb(38, 52, 69) !important;
}
</style>
