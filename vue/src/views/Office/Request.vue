<template>
  <div>
    <div style="margin: 10px 0">
      <el-input style="width: 200px" placeholder="请输入用户名" suffix-icon="el-icon-search" v-model="username"></el-input>
      <el-input style="width: 200px"
                placeholder="请输入事务所"
                suffix-icon="el-icon-position"
                class="ml-5"
                v-model="office" v-if="user.role === 'ROLE_ADMIN'"></el-input>
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>

    <div style="margin: 10px 0">
      <el-button type="primary" @click="approvalBatch">批量同意 <i class="el-icon-circle-plus-outline"></i></el-button>
      <el-popconfirm
          class="ml-5"
          confirm-button-text='确定'
          cancel-button-text='我再想想'
          icon="el-icon-info"
          icon-color="red"
          title="您确定批量拒绝这些请求吗？"
          @confirm="delBatch"
      >
        <el-button type="danger" slot="reference">批量拒绝 <i class="el-icon-remove-outline"></i></el-button>
      </el-popconfirm>
    </div>

    <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'"  @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="55"></el-table-column>
      <el-table-column prop="username" label="用户名" width="250"></el-table-column>
      <el-table-column prop="office" label="事务所" width="250"></el-table-column>
      <el-table-column label="申请人详情" width="250">
        <template slot-scope="scope" >
          <el-button @click="handleDetail(scope.row.username)" type="primary">查看内容</el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作"  width="" align="center">
        <template slot-scope="scope">
          <el-button type="primary" @click="approval(scope.row)" >同意 <i class="el-icon-circle-plus-outline"></i></el-button>
          <el-button type="danger" @click="del(scope.row)" >拒绝 <i class="el-icon-remove-outline"></i></el-button>
        </template>
      </el-table-column>
    </el-table>
    <div style="padding: 10px 0">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[2, 5, 10, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>
    </div>

    <el-dialog title="申请人详情" :visible.sync="dialogFormVisible" width="75%" >

      <el-card>
        <div style="display: flex">
          <div style="width: 25%">
            <el-image :src="information.avatarUrl" v-if="information.avatarUrl"></el-image>
            <el-empty :image-size="150" v-else description=" " ></el-empty>
          </div>
          <div style="margin-left: 20px; margin-top: 50px;flex: 2">
            <el-descriptions class="margin-top" size="larger"  :column="2" border>
              <el-descriptions-item>
                <template slot="label">
                  <i class="el-icon-user"></i>
                  姓名
                </template>
                {{information.nickname}}
              </el-descriptions-item>
              <el-descriptions-item>
                <template slot="label">
                  <i class="el-icon-mobile-phone"></i>
                  手机号
                </template>
                {{information.phone}}
              </el-descriptions-item>
              <el-descriptions-item>
                <template slot="label">
                  <i class="el-icon-message"></i>
                  E-mail
                </template>
                {{information.email}}
              </el-descriptions-item>
              <el-descriptions-item>
                <template slot="label">
                  <i class="el-icon-location-outline"></i>
                  所在地
                </template>
                {{information.address}}
              </el-descriptions-item>
              <el-descriptions-item>
                <template slot="label">
                  <i class="el-icon-tickets"></i>
                  擅长领域
                </template>
                <el-tag size="small" v-if="information.domain">{{information.domain}}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item>
                <template slot="label">
                  <i class="el-icon-office-building"></i>
                  律师事务所
                </template>
                {{information.office}}
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </div>
      </el-card>



      <el-descriptions direction="vertical" :column="1" border>
        <el-descriptions-item label="简介">
          <el-card>
            <div>
              <mavon-editor
                  class="md"
                  :value="information.intro"
                  :subfield="false"
                  :defaultOpen="'preview'"
                  :toolbarsFlag="false"
                  :editable="false"
                  :scrollStyle="true"
                  :ishljs="true"
              />
            </div>
          </el-card>
        </el-descriptions-item>

      </el-descriptions>
    </el-dialog>


  </div>
</template>

<script>
import {serverIp} from "../../../public/config";

export default {
  name: "Request",
  data() {
    return {
      user: sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {},
      serverIp: serverIp,
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      username: "",
      office: "",
      form: {},
      information:{},
      dialogFormVisible:false,
      multipleSelection: [],
      roles: [{name:"全部" ,flag:""},{name:"律师" ,flag:"ROLE_LAWYER"},{name:"事务所负责人" ,flag:"ROLE_OFFICE_ADMIN"}],
      courses: [],
      vis: false,
      stuCourses: [],
      stuVis: false
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.request.get("/request/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          username: this.username,
          office: this.office,
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })

    },
    approval(row) {
      this.request.post("/user/member/add_member", row).then(res => {
        if (res.code === '200') {
          this.load()
        } else {
          this.$message.error(res.msg)
        }
      })
      this.del(row)
    },
    approvalBatch(){
      this.request.post("/user/member/add_member/batch", this.multipleSelection).then(res => {
        if (res.code === '200') {
          this.load()
        } else {
          this.$message.error(res.msg)
        }
      })
      this.delBatch()
    },
    del(row){
      this.request.delete("/request/"+row.id).then(res => {
        if(res.code === "200"){
          this.$message.success("成功")
          this.load()
        }
      })
    },
    handleSelectionChange(val) {
      console.log(val)
      this.multipleSelection = val
    },
    delBatch() {
      let ids = this.multipleSelection.map(v => v.id)  // [{}, {}, {}] => [1,2,3]
      this.request.post("/request/del/batch", ids).then(res => {
        if (res.code === '200') {
          this.$message.success("批量移除成功")
          this.load()
        } else {
          this.$message.error("批量移除失败")
        }
      })
    },
    reset() {
      this.username = ""
      this.office = ""
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
    handleDetail(username){
      this.information = {}
      this.request.get("/user/lawyerDetail/"+username).then(res =>{
        if(res.code === "200"){
          this.information = res.data;
          this.dialogFormVisible = true
        }else {
          this.$message.error("信息获取失败，请稍后重试")
        }
      })
    }

  }
}
</script>


<style>
.headerBg {
  background: #eee!important;
}
</style>
