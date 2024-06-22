<template>
  <div class="bg">
  <el-card shadow="always" style="width: 80%; margin-left: 10%;background-color: #f5f9f6">
    <div class="block" style="margin-left: 10%">
      <div class="radio" style="margin: 5px;">
        时间排序：
        <el-radio-group v-model="reverse">
          <el-radio :label="true">倒 序</el-radio>
          <el-radio :label="false">正 序</el-radio>
        </el-radio-group>
      </div>
      <el-timeline :reverse="reverse">
        <template v-for="timestamp in sortedTimestamps" >
          <el-timeline-item placement="top" :timestamp="timestamp" >
            <template v-for="activity in groupedActivities[timestamp]">
              <el-card style="width: 90%;height: 26rem;padding: 1%;border: 3px solid #57782d"
                       @click.native="$router.push('/front/result?id=' + activity.id)"
                       shadow="hover">
                <div style="width: 500px;float: left">
                  <el-card style="min-width: 300px;min-height:  360px;box-shadow: none;border: 0px solid #47dc8f" >
                    <el-image style="width: 200px;max-width: 200px; max-height: 300px;"
                              :src="activity.segImg"
                              :fit="scale-down" v-if="activity.segImg"></el-image>
                    <el-image style="width: 200px;max-width: 200px; max-height: 250px;"
                              :src="activity.clsImg"
                              :fit="scale-down" v-if="activity.clsImg"></el-image>
                    <div style="width: 200px;margin-top: 3%">
                      <h3 style="margin-top: 1%">分割模型：{{activity.segModel}}</h3>
                      <h3 style="margin-top: 1%">分类模型：{{activity.clsModel}}</h3>
                    </div>
                  </el-card>

                </div>
                <div style="float: right;width: 450px">
                  <el-card style="min-width: 300px;box-shadow: none;border: 0 solid #67C23A " >
                    <h3 style="float: left;margin-top: 1%">病人姓名：{{activity.patientName}}</h3>
                    <h3 style="float: right;margin-top: 1%;margin-right: 125px">病人性别：{{activity.patientSex}}</h3>
                    <h3 style="margin-top: 50px;">病史:</h3>
                    <el-card style=";margin-top: 5px;min-height: 240px;box-shadow: none;border: 2px solid #000000">
                      <h5>{{activity.patientInfo}}</h5>
                    </el-card>

                  </el-card>

                </div>
              </el-card>
            </template>
          </el-timeline-item>
        </template>
      </el-timeline>
    </div>
  </el-card>
  </div>
</template>

<script>
export default {
  data() {
    return {
      reverse: true,
      user: sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {},
      activities: []
    };
  },
  created() {
    this.init()
  },
  computed: {
    groupedActivities() {
      const grouped = {};
      this.activities.forEach(activity => {
        const timestamp = activity.timestamp;
        if (!grouped[timestamp]) {
          grouped[timestamp] = [];
        }
        grouped[timestamp].push(activity);
      });

      return grouped;
    },
    sortedTimestamps() {
      return Object.keys(this.groupedActivities).sort();
    }
  },
  methods:{
    init(){
      this.request.get("/history/list/"+this.user.id).then(res=>{
        if (res.code === "200"){
          for (let i = 0; i < res.data.length; i++) {
            this.activities.push({
              id:res.data[i].id,
              patientName :res.data[i].patientName,
              patientSex :res.data[i].patientSex,
              patientInfo :res.data[i].patientInfo,
              segModel :res.data[i].segModel,
              clsModel :res.data[i].clsModel,
              timestamp:res.data[i].time,
              segImg:res.data[i].segImg,
              clsImg:res.data[i].clsImg,

            })
          }

        }
      })
    }
  }
};
</script>

<style>
.bg{
  background-color: #f5f9f6;
  width: 100%;
  height: 100%;
  display: flex;
}
</style>
