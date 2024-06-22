<template>
  <div>
    <div style="margin: 10px 0">
      <el-input style="width: 200px" placeholder="请输入用户名" suffix-icon="el-icon-search" v-model="username"></el-input>
      <el-input style="width: 200px" placeholder="请输入姓名" suffix-icon="el-icon-search" v-model="nickname"></el-input>
      <el-select clearable v-model="role" placeholder="请选择角色" style="width: 200px;padding-left: 5px">
        <el-option v-for="item in roles" :key="item.name" :label="item.name" :value="item.flag"></el-option>
      </el-select>
      <el-input style="width: 200px"
                placeholder="请输入事务所"
                suffix-icon="el-icon-position"
                class="ml-5"
                v-model="office" v-if="user.role === 'ROLE_ADMIN'"></el-input>
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>

    <div style="margin: 10px 0">
      <el-button type="primary" @click="handleAdd">新增 <i class="el-icon-circle-plus-outline"></i></el-button>
      <el-popconfirm
          class="ml-5"
          confirm-button-text='确定'
          cancel-button-text='我再想想'
          icon="el-icon-info"
          icon-color="red"
          title="您确定批量删除这些数据吗？"
          @confirm="delBatch"
      >
        <el-button type="danger" slot="reference">批量删除 <i class="el-icon-remove-outline"></i></el-button>
      </el-popconfirm>
    </div>

    <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'"  @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="username" label="用户名" width="140"></el-table-column>
      <el-table-column prop="role" label="角色" align="center" width="100px">
        <template slot-scope="scope">
          <el-tag type="primary" v-if="scope.row.role === 'ROLE_OFFICE_ADMIN'">负责人</el-tag>
          <el-tag type="warning" v-if="scope.row.role === 'ROLE_LAWYER'">律师</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="nickname" label="姓名" width="120"></el-table-column>
      <el-table-column prop="email" label="邮箱"></el-table-column>
      <el-table-column prop="phone" label="电话"></el-table-column>
      <el-table-column prop="address" label="地址"></el-table-column>
      <el-table-column prop="domain" label="擅长领域"></el-table-column>
      <el-table-column prop="office" label="事务所" v-if="user.role === 'ROLE_ADMIN'"></el-table-column>

      <el-table-column label="操作"  width="300" align="center">
        <template slot-scope="scope">
          <el-button type="primary" @click="revokeAdmin(scope.row.id)" v-if="scope.row.role === 'ROLE_OFFICE_ADMIN'">撤销管理员 <i class="el-icon-document"></i></el-button>
          <el-button type="warning" @click="setAdmin(scope.row.id)" v-if="scope.row.role === 'ROLE_LAWYER'">设为管理员 <i class="el-icon-document"></i></el-button>
          <el-popconfirm
              class="ml-5"
              confirm-button-text='确定'
              cancel-button-text='我再想想'
              icon="el-icon-info"
              icon-color="red"
              title="您确定删除吗？"
              @confirm="del(scope.row.id)"
          >
            <el-button type="danger" slot="reference" v-if="scope.id !== user.id">删除 <i class="el-icon-remove-outline"></i></el-button>
          </el-popconfirm>
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

    <el-dialog title="用户信息" :visible.sync="dialogFormVisible" width="30%" >
      <el-form label-width="80px" size="small">
        <el-form-item label="用户名">
          <el-input v-model="form.username" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="事务所">
          <el-input v-model="form.office" autocomplete="off" disabled=""></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import {serverIp} from "../../../public/config";

export default {
  name: "Member",
  data() {
    return {
      user: sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {},
      serverIp: serverIp,
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      username: "",
      nickname:"",
      office: "",
      role: "",
      form: {},
      dialogFormVisible: false,
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
      this.request.get("/user/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          username: this.username,
          nickname: this.nickname,
          role: this.role,
          office: this.office,
        }
      }).then(res => {

        this.tableData = res.data.records
        this.total = res.data.total

      })

    },
    save() {
      console.log(this.form)
      this.request.post("/user/member/add_member", this.form).then(res => {
        if (res.code === '200') {
          this.$message.success("保存成功")
          this.dialogFormVisible = false
          this.load()
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleAdd() {
      this.dialogFormVisible = true
      this.form = {office: this.user.office}
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
    },
    del(id) {
      this.request.delete("/user/member/del/" + id).then(res => {
        if (res.code === '200') {
          this.$message.success("移除成功")
          this.load()
        } else {
          this.$message.error("移除失败")
        }
      })
    },
    handleSelectionChange(val) {
      console.log(val)
      this.multipleSelection = val
    },
    delBatch() {
      let ids = this.multipleSelection.map(v => v.id)  // [{}, {}, {}] => [1,2,3]
      this.request.post("/user/member/del/batch", ids).then(res => {
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
      this.nickname = ""
      this.role = ""
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
    exp() {
      window.open(`http://${serverIp}:9090/user/export`)
    },
    handleExcelImportSuccess() {
      this.$message.success("导入成功")
      this.load()
    },
    revokeAdmin(id) {
      this.request.post("/user/member/remove_admin/" + id).then(res => {
        if (res.code === "200") {
          this.$message.success("修改成功")
          this.load()
        } else {
          this.$message.error("修改失败")
        }
      })
    },
    setAdmin(id){
      this.request.post("/user/member/add_admin/"+id).then(res => {
        if (res.code === "200"){
          this.$message.success("修改成功")
          this.load()
        } else{
          this.$message.error("修改失败")
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
