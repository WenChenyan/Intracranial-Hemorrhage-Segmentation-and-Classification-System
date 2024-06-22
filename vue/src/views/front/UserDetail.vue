<template>
  <div>

    <el-card style="padding: 0 20px">

      <el-card>
        <h2 style="margin: 0 20px 20px 0">医生信息</h2>
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
                  就职医院
                </template>
                {{information.office}}
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </div>
      </el-card>

      <el-descriptions direction="vertical" :column="1" border size="larger">
        <el-descriptions-item label="简介">
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
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card>
      <div >
        <div style="display: flex">
          <h2 style="margin-left: 20px;flex: 1">发表文章</h2>
          <span style="color: #39cd83;margin: 5px 20px 0 0 ; cursor: pointer" @click="$router.push('/front/article?username=' + username)">查看更多 ></span>
        </div>
        <div style="margin: 10px 20px">
          <el-row :gutter="24">
            <el-col :span="12" v-for="item in articles" :key="item.id" style="margin-bottom: 10px" align="center">
              <div style="border: 1px solid #ccc; padding-bottom: 10px;
               background-color: white;border-radius: 5px; box-shadow: 0 0 10px #ccc;width: 80%;height: 500px" >
                <div style="margin: 10px 5px;overflow: hidden ;width: 90%;height: 10%; cursor: pointer ;"
                     class="hover-div"
                     @click="$router.push('/front/articleDetail?id=' + item.id)">
                  <h3 >{{item.title}}</h3>
                </div>
                <div style="margin: 10px 5px;overflow-y: scroll ;width: 90%;height: 85%">
                  <el-descriptions direction="vertical" :column="1" border>
                    <el-descriptions-item>
                      <mavon-editor
                          class="md"
                          :value="item.content"
                          :subfield="false"
                          :defaultOpen="'preview'"
                          :toolbarsFlag="false"
                          :editable="false"
                          :scrollStyle="true"
                          :ishljs="true"
                      />
                    </el-descriptions-item>

                  </el-descriptions>
                </div>
              </div>
            </el-col>

          </el-row>
        </div>
      </div>
    </el-card>

    <el-card>
      <div >
        <div style="display: flex">
          <h2 style="margin-left: 20px;flex: 1">患者评价</h2>
          <span style="color: #39cd83;margin: 5px 20px 0 0; "
                class="hover-div"
                @click="$router.push('/front/evaluateDetail?username='+username)"
          >查看更多 ></span>
        </div>
        <div style="margin: 10px 20px">
          <el-row :gutter="24">
            <el-col :span="8" v-for="item in evaluations" :key="item.id" style="margin-bottom: 10px" align="center">
              <div style="border: 1px solid #ccc; padding-bottom: 10px;
               background-color: white;border-radius: 5px; box-shadow: 0 0 10px #ccc;width: 90%;height: 110px" >
                <div style="background: linear-gradient(#e3ffe3, #ffffff);">
                  <div style="padding: 10px 5px 0 5px;overflow: hidden ;
                              width: 95%;height: 30%; cursor: pointer">
                    <el-rate
                        v-model="item.score"
                        show-score
                        disabled=""
                        text-color="#ff9900"
                        :colors="colors">
                    </el-rate>
                  </div>
                </div>

                <div style="margin: 0 5px 0 5px;width: 90%;height: 65%;overflow:hidden"
                >
                  <span>{{item.comment}}</span>
                </div>
              </div>
            </el-col>

          </el-row>
        </div>
      </div>
    </el-card>

  </div>
</template>

<script>
export default {
  name: "LawyerDetail",
  data(){
    return {
      username: this.$route.query.username,
      information:{},
      articles: [],
      evaluations:[],
      colors: ['#99A9BF', '#F7BA2A', '#FF9900']
    }
  },
  created() {
    this.load()
  },
  methods:{
    load(){
      this.information = {}
      this.request.get("/user/lawyerDetail/"+this.username).then(res =>{
        if(res.code === "200"){
          this.information = res.data;
          this.dialogFormVisible = true
        }else {
          this.$message.error("信息获取失败，请稍后重试")
        }
      })

      this.request.get("/article/page",{
        params:{
          name: this.username,
          pageNum: 1,
          pageSize: 4
        }
      }).then(res =>{
        if (res.code === "200"){
          this.articles = res.data.records
        }
      })

      this.request.get("/evaluate/page",{
        params:{
          username: "",
          lawyer:this.username,
          pageNum: 1,
          pageSize: 3
        }
      }).then(res =>{
        if (res.code === "200"){
          this.evaluations = res.data.records
        }
      })
    },
  }
}
</script>

<style >

.hover-div {
  cursor: pointer; /* 鼠标悬停时显示手型光标 */
}

.hover-div h3 {
  color: black; /* 初始文字颜色 */
  transition: color 0.3s; /* 添加过渡效果 */
}

.hover-div:hover h3 {
  color: #6ecdb6; /* 鼠标悬停时的文字颜色 */
}

</style>