<template>
  <div style="color: #666;background-color: #f5f9f6;min-height:  calc(100vh - 60px);padding-top: 80px">
<!--搜索框-->
    <div style="margin: 10px 0;display: flex;position: relative;left: 20%;width: 80%;">
      <div style="margin: 2vh 0;width: 50%;">
        <div style=" position:relative;float: left">
          <el-button class="ml-5" type="primary" @click="load" size="small" icon="el-icon-search" >搜 索</el-button>
          <el-button type="warning" @click="reset" size="small">重 置</el-button>
          <div style="float: left"><el-input size="small" style="width: 300px" placeholder="请输入标题"v-model="title"></el-input>
          </div>
        </div>
        <div  style="padding-top: 5px;float: left;width: 500px">
          <el-date-picker
              size="small"
              v-model="datetime"
              type="datetimerange"
              format="yyyy-MM-dd HH:mm:ss"
              value-format="yyyy-MM-dd HH:mm:ss"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期">
          </el-date-picker>
          <el-select v-model="type" placeholder="博客" style="float: right;margin-left:5px;margin-top:1px;width: 90px;" >
            <el-option label="博客" value="0"></el-option>
            <el-option label="论文" value="1"></el-option>
            <el-option label="数据集" value="2"></el-option>
          </el-select>
        </div>
      </div>
      <div style="margin: 20px 50px">
        <el-button type="primary" @click="handleAdd" size="medium">新增 <i class="el-icon-edit"></i></el-button>
      </div>
    </div>


    <!--Card 信息-->
      <el-container v-for="item in tableData" :key="item.id" >
        <el-card class="box-card" style="width: 70%;height: 35%;margin: 5% 5% 0 15%;box-shadow: 0 0 20px 3px rgba(0, 0, 0, 0.2); " shadow="never">
          <el-container>
        <el-aside  width="30%">
          <el-image :src="item.img" style="height: 26vh;overflow: hidden;">
            <div slot="placeholder" class="image-slot">
              加载中<span class="dot">...</span>
            </div>
          </el-image>
        </el-aside>
        <el-container >
          <el-header  >
            <span  class="title-link" style="cursor: pointer;" @click="$router.push('/front/articleDetail?id=' + item.id)">
              <h3>{{ item.title }}</h3></span>
          </el-header>
          <el-main>
            <div style="max-width: 100%; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
              {{ item.content }}
            </div>
          </el-main>
          <el-footer>
            <i class="el-icon-user-solid"></i><span v-if="item.type==='1'">上传者：</span> <span>{{ item.nickname }}</span>
            <i class="el-icon-time" style="margin-left: 10px"></i> <span>{{ item.time }}</span>
            <i class="el-icon-news" style="margin-left: 10px" v-if="item.type==='0'"> <span>博 客</span></i>
            <i class="el-icon-document" style="margin-left: 10px" v-if="item.type==='1'"><span>论 文</span></i>
            <div style="text-align: right;float: right;width:25% " v-if="user.username === item.user">
              <el-button style="margin-top:4vh;margin-left: 5px;font-size: small" size="small" type="info" @click="handleEdit(item)">修 改</el-button>
              <el-button type="danger" style="margin-top:4vh;font-size: small" size="small" @click="del(item.id)" >删 除</el-button>
            </div>
          </el-footer>
      </el-container>
      </el-container>
        </el-card>
      </el-container>


    <!--翻页-->
    <div style="padding: 10px 0">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[2, 5, 10, 20]"
          :page-size="pageSize"
          layout="total, prev, pager, next"
          :total="total">
      </el-pagination>
    </div>
<!--新增内容-->
    <el-dialog title="发表文章|上传数据集" :visible.sync="dialogFormVisible" width="60%" height="70%" >
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane label="文章" name="first">
          <el-form label-width="80px" size="small"  >
            <el-form-item label="文章类型">
              <el-select v-model="form.type" placeholder="请选择文章类型">
                <el-option label="博客" value="0"></el-option>
                <el-option label="论文" value="1"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="文章标题">
              <el-input v-model="form.title"></el-input>
            </el-form-item>
            <el-form-item label="文章内容">
              <mavon-editor ref="md" v-model="form.content" :ishljs="true" @imgAdd="imgAdd"/>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="数据集" name="second" style="min-height:350px">
          <el-container>
            <el-aside width="50%">
              <el-upload
                  class="upload-demo"
                  drag
                  :http-request="httpRequest"
                  multiple
                  style="margin-top: 7%;margin-left: 10%"
              >
                <i class="el-icon-upload" style="margin-top: 70px"></i>
                <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
              </el-upload>
            </el-aside>
            <el-main>
              <el-form ref="form" :model="form" label-width="100px" size="large" style="margin-top: 50px">
                <el-form-item label="数据集名称"  >
                  <el-input v-model="form.title" style="width: 300px;"  ></el-input>
                </el-form-item>
                <el-form-item label="数据集介绍" style="width: 400px;" >
                  <el-input type="textarea" v-model="form.content" :rows="3"></el-input>
                </el-form-item>
              </el-form>
            </el-main>
          </el-container>
        </el-tab-pane>
      </el-tabs>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>

  </div>

</template>

<script>

import axios from "axios";

export default {
  name: "Article",
  data() {
    return {
      username: this.$route.query.username ? this.$route.query.username : "",
      form: {},
      activeName: 'second',
      tableData: [],
      title: '',
      type:"",
      datetime:null,
      multipleSelection: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      dialogFormVisible: false,
      teachers: [],
      user: sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {},
      content: '',
      viewDialogVis: false,
      src: 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg'
    }
  },
  created() {
    this.load()
  },
  methods: {
    httpRequest(param) {

      let fileObj = param.file // 相当于input里取得的files
      let fd = new FormData()// FormData 对象
      fd.append('file', fileObj)// 文件对象

      let url ='http://localhost:9090/file/upload'
      let config = {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }
      axios.post(url, fd, config).then(res=>{
        console.log(res.data.data)
        this.form.img = res.data.data
        this.form.type = 2
      })

    },
    view(content) {
      this.content = content
      this.viewDialogVis = true
    },
    // 绑定@imgAdd event
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
        $vm.$img2Url(pos, res.data);
      })
    },
    load() {
      let start = ""
      let end = ""
      if (this.datetime && this.datetime.length >= 2) {
        start = this.datetime[0]
        end = this.datetime[1]
      }
      this.request.get("/article/page", {
        params: {
          start: start,
          end: end,
          type: this.type,
          title: this.title,
          name: this.username,
          pageNum: this.pageNum,
          pageSize: this.pageSize
        }
      }).then(res => {
          if(res.code === '200'){
            console.log(res)
            this.tableData = res.data.records
            this.total = res.data.total
          }
          else {
            console.log(res.msg)
          }
      })
    },
    del(id) {
      this.request.delete("/article/" + id).then(res => {
        if (res.code === '200') {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      })
    },
    changeEnable(row) {
      this.request.post("/article/update", row).then(res => {
        if (res.code === '200') {
          this.$message.success("操作成功")
        }
      })
    },
    handleAdd() {
      /*if(this.user.role  === "ROLE_USER"){
        this.$message.error("请先进行律师认证")
        this.dialogFormVisible = false
      }
      else {

      }*/
      this.dialogFormVisible = true
      this.form = {}
    },
    handleEdit(row) {
      this.form = row
      this.dialogFormVisible = true
    },
    handleSelectionChange(val) {
      console.log(val)
      this.multipleSelection = val
    },
    save() {
      this.request.post("/article", this.form).then(res => {
        if (res.code === '200') {
          this.$message.success("保存成功")
          this.dialogFormVisible = false
          this.load()
        } else {
          this.$message.error("保存失败")
        }
      })

    },
    reset() {
      this.name = ""
      this.datetime = null
      this.type=0
      this.load()
    },
    handleSizeChange(pageSize) {
      console.log(pageSize)
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
      console.log(pageNum)
      this.pageNum = pageNum
      this.load()
    },
    download(url) {
      window.open(url)
    },
  }
}
</script>

<style>
.el-tabs--border-card>.el-tabs__content {
  padding: 0;
}
.el-upload-dragger {
  background-color: #fff;
  border: 4px dashed #d9d9d9;
  border-radius: 6px;
  box-sizing: border-box;
  width: 420px;
  height: 240px;
  text-align: center;
  position: relative;
  overflow: hidden;
}
.title-link {
  cursor: pointer;
  color: black; /* Initial color */
}

.title-link:hover {
  color: #409eff; /* Color on hover */
}
.box-card:hover {
  transform: scale(1.02); /* 在鼠标悬停时放大 5% */
}

</style>
