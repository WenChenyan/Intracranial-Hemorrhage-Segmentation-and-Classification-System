<template>
  <div style="background-color: #f5f9f6;min-height:  calc(100vh - 60px);padding-top: 80px">
    <el-card style="width: 600px;margin-left: 33vw" >
      <el-form label-width="80px" size="large" >
        <el-upload
            class="avatar-uploader"
            :action="'http://' + serverIp +':9090/file/upload'"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
        >
          <img v-if="form.avatarUrl" :src="form.avatarUrl" class="avatar">
          <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </el-upload>

        <el-form-item label="用户名" >
          <el-input v-model="form.username" disabled autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.nickname" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="电话">
          <el-input v-model="form.phone" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="擅长领域">
          <el-input v-model="form.domain" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-button style="width: 100% ;height: 40px;text-align: center;font-size: medium"
                     @click="handleEmail">
            <span  v-if="form.email">{{form.email}}</span>
            <span  v-else>点击认证</span>
          </el-button>
        </el-form-item>
        <el-form-item label="地址">
          <el-input type="textarea" v-model="form.address" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="个人简介">
          <el-button style="width: 100% ;height: 40px;font-size: medium" @click="viewDialogVis=true">点击输入</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save">确 定</el-button>
        </el-form-item>
      </el-form>
      <el-dialog title="个人简介" :visible.sync="viewDialogVis" width="60%" >
        <mavon-editor ref="md" v-model="form.intro" :ishljs="true" @imgAdd="imgAdd"/>
        <div slot="footer" class="dialog-footer">
          <el-button @click="viewDialogVis = false;">确 定</el-button>
          <el-button @click="viewDialogVis = false;form.intro=''" type="warning">取 消</el-button>
        </div>
      </el-dialog >

      <el-dialog title="邮箱认证" :visible.sync="viewEmailDialogVis" width="400px" style="margin-top: 10%"  >
        <el-form :model="emailDTO" :rules="emailRules" ref="emailForm" >
          <el-form-item prop="email">
            <el-input style="margin-left: 10%;width: 80%"
                      size="medium"
                      prefix-icon="el-icon-user"
                      v-model="emailDTO.email"
                      placeholder="请输入邮箱"
            ></el-input>
          </el-form-item>
          <el-form-item prop="code">
            <el-input size="medium" prefix-icon="el-icon-lock" placeholder="请输入验证码"
                      style="width: 48%;margin-left: 10%" v-model="emailDTO.code"></el-input>
            <el-button type="primary" class="ml-5" size="small" style="margin-left: 20px" @click="sendEmailCode">发送验证码</el-button>
          </el-form-item>
          <el-form-item style="margin: 10px 0; text-align: right">
            <el-button type="primary" size="small" autocomplete="off" @click="authenticateEmail">设置邮箱</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>

      <el-dialog title="律师认证" :visible.sync="viewOfficeDialogVis" width="400px" style="margin-top: 10%"  >
        <el-form :model="officeDTO" :rules="emailRules" ref="emailForm" >
          <el-form-item prop="office">
            <el-select clearable v-model="officeDTO.office" placeholder="请选择" style="width: 100%">
              <el-option v-for="item in options" :key="item.name" :label="item.name" :value="item.name">
                {{ item.name }}
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item style="margin: 10px 0; text-align: right">
            <el-button type="primary" size="small" autocomplete="off" @click="sendRequest">提交申请</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>

    </el-card>
  </div>

</template>

<script>
import {serverIp} from "../../../public/config";
import axios from "axios";

export default {
  name: "Person",
  data() {
    const checkEmail = (rule, value, callback) => {
      if (value && !/^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.(com|cn|net)$/.test(value))  {
        callback(new Error('请输入正确邮箱'));
      }
      callback();
    };
    return {
      serverIp: serverIp,
      form: {},
      emailDTO:{},
      user: sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {},
      officeDTO: {},
      options:[],
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
      officeRules: {
        office: [
          {required: true, message: '请输入邮箱', trigger: 'blur'},
          {validator:checkEmail, trigger: 'blur'}
        ]
      },
      viewDialogVis: false,
      viewEmailDialogVis: false,
      viewOfficeDialogVis: false

    }
  },
  created() {
    this.getUser().then(res => {
      this.form = res
      this.office = res.office
    })
    this.loadOption()
  },
  methods: {
    async getUser() {
      return (await this.request.get("/user/username/" + this.user.username)).data
    },
    imgAdd(pos, $file) {
      let $vm = this.$refs.md
      // 第一步.将图片上传到服务器.
      const formData = new FormData();
      formData.append('file', $file);
      axios({
        url: 'http://localhost:9090/file/upload',
        method: 'post',
        data: formData,
        headers: {'Content-Type': 'multipart/form-data'},
      }).then((res) => {
        // 第二步.将返回的url替换到文本原位置![...](./0) -> ![...](url)
        console.log(res)
        $vm.$img2Url(pos, res.data);
      })
    },
    save() {
      this.request.post("/user", this.form).then(res => {
        if (res.code === '200') {
          this.$message.success("保存成功")

          // 触发父级更新User的方法
          this.$emit("refreshUser")

          // 更新浏览器存储的用户信息
          this.getUser().then(res => {
            res.token = JSON.parse(sessionStorage.getItem("user")).token
            sessionStorage.setItem("user", JSON.stringify(res))
          })

        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleAvatarSuccess(res) {
      this.form.avatarUrl = res.data
    },
    loadOption(){
      this.request.get("office").then(res =>{
        if(res.code === "200"){
          this.options = res.data
        }
      })
    },
    handleEmail(){
      this.viewEmailDialogVis=true
      this.emailDTO.email
    },
    sendEmailCode(){
      this.request.get("user/email/"+this.emailDTO.email+"/"+305).then(res =>{
        if(res.code === "200"){
          this.$message.success("发送成功")
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    authenticateEmail(){
      this.request.post("user/authenticateEmail",this.emailDTO).then(res =>{
        if(res.code === "200"){
          this.form.email = this.emailDTO.email
          this.emailDTO = {}
          this.viewEmailDialogVis = false
          this.$message.success("验证通过")
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    sendRequest(){
      if(this.user.email){
        this.officeDTO.username=this.user.username
        this.request.post("request" ,this.officeDTO).then(res => {
          if (res.code === "200"){
            this.viewOfficeDialogVis = false
            this.$message.success("请等待事务所负责人核验信息")
          } else {
            this.$message.error(res.msg)
          }
        })
      }else {
        this.$message.warning("请先绑定邮箱")
      }
    }
  }
}
</script>

<style>
.avatar-uploader {
  text-align: center;
  padding-bottom: 10px;
}
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 138px;
  height: 138px;
  line-height: 138px;
  text-align: center;
}
.avatar {
  width: 138px;
  height: 138px;
  display: block;
}
</style>
