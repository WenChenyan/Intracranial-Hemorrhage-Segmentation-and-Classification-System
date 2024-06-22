<template>

  <div style="display: flex;">

    <el-card class="box-card" style="width: 70%;flex: 1">
      <h1 style="margin-left: 20px">法律咨询评价</h1>
      <div style="margin-top: 20px;display: flex;">
        <el-card class="box-card" style="width: 40%;margin-left: 7%;flex: 1;text-align: center;background-color: #cccccc">
          <div style="width: 100%">
            <span style="font-size: 18px">已有</span>
            <span style="color: #39cd83;font-size: 20px">{{total}}</span>
            <span style="font-size: 18px">人进行评价</span>
          </div>
        </el-card>
        <el-card class="box-card" style="width: 40%;margin-left: 6%;margin-right: 7%;text-align: center;background-color: #cccccc">
          <div style="width: 100%">
            <span style="color: #39cd83;font-size: 20px">{{praiseNum }}</span>
            <span style="font-size: 18px">人给予好评</span>
          </div>
        </el-card>
      </div>
      <div style="margin: 30px 0">
        <el-row :gutter="24" >
          <el-col :span="24" v-for="item in evaluations" :key="item.username" style="margin-bottom: 10px;margin-top: 20px">
            <div >
              <div style="display: flex">
                <div style="width: 100px; text-align: center">
                  <el-image :src="circleUrl" style="width: 80px; height: 80px; border-radius: 50%;margin-bottom: 10px" ></el-image>
                  <b >{{ item.username }}</b>
                </div> <!--  头像-->
                <div style="flex: 1; font-size: 14px; padding: 5px 0; line-height: 25px;margin-left: 20px">
                  <div style="color: #666; margin-left: 10px;margin-bottom: 10px">
                    <el-rate v-model="item.score" disabled show-score :colors="colors"></el-rate>
                  </div>
                  <div style="height: 60px">
                    <span style="font-size: medium">{{ item.comment }}</span>
                  </div>

                  <div style="display: flex; line-height: 20px; margin-top: 5px">
                    <div style="width: 200px;">
                      <i class="el-icon-time"></i><span style="margin-left: 5px">{{ item.time }}</span>
                    </div>

                  </div>
                  <el-divider></el-divider>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>


      <div style="padding: 10px 0">
        <el-pagination
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-size="pageSize"
            layout="total, prev, pager, next, jumper"
            :total="total">
        </el-pagination>
      </div>

    </el-card>

    <el-card style="margin-left: 20px">
      <h2 style="margin: 0 20px 20px 0">律师信息</h2>
      <div style="display: flex">
        <div style="margin-left: 20px; margin-top: 50px;flex: 2">
          <el-descriptions class="margin-top" size="larger"  :column="1" border>
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

  </div>

</template>

<script>
export default {
  name: "EvaluateDetail",
  data() {
    return {
      circleUrl: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      user: sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {},
      information:{},
      evaluations :[],
      total: 0,
      pageNum: 1,
      pageSize: 8,
      colors: ['#99A9BF', '#F7BA2A', '#FF9900'],
      username: this.$route.query.username,
      praiseNum : 0
    }
  },
  created() {
    this.load()
  },
  methods: {
    load(){

      this.information = {}
      this.request.get("/user/lawyerDetail/"+this.username).then(res =>{
        if(res.code === "200"){
          this.information = res.data;
        }else {
          this.$message.error("信息获取失败，请稍后重试")
        }
      })

      this.request.get("/evaluate/page",{
        params:{
          pageNum : this.pageNum,
          pageSize : this.pageSize,
          lawyer : this.username,
        }
      }).then(res => {
        this.evaluations = res.data.records
        this.total = res.data.total
      })

      this.request.get("/evaluate/praise/"+this.username).then(res =>{
        if(res.code === "200"){
          this.praiseNum = res.data
        }
      })

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
  }
}
</script>

<style>

</style>
