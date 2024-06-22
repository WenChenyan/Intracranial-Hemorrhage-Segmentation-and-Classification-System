<template>
  <div>
    <el-row :gutter="10" style="margin-bottom: 60px">
      <el-col :span="6">
        <el-card style="color: #409EFF">
          <div><i class="el-icon-user-solid" /> 用户总数</div>
          <div style="padding: 10px 0; text-align: center; font-weight: bold">
          {{sysUserNum}}
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card style="color: #F56C6C">
          <div><i class="el-icon-s-custom" /> 律师总数</div>
          <div style="padding: 10px 0; text-align: center; font-weight: bold">
          {{lawyerNum}}
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card style="color: #67C23A">
          <div><i class="el-icon-user" /> 委托人总数</div>
          <div style="padding: 10px 0; text-align: center; font-weight: bold">
          {{userNum}}
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card style="color: #E6A23C">
          <div><i class="el-icon-document" /> 文章总数</div>
          <div style="padding: 10px 0; text-align: center; font-weight: bold">
          {{articleNum}}
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="8">
        <div id="main" style="width: 500px; height: 450px"></div>
      </el-col>
      <el-col :span="8">
        <div id="article" style="width: 500px; height: 450px"></div>
      </el-col>
      <el-col :span="8">
        <div id="pie" style="width: 500px; height: 400px"></div>
      </el-col>
    </el-row>

  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: "Dashbord",
  data() {
    return {
      sysUserNum : 0,
      lawyerNum : 0,
      userNum : 0,
      articleNum : 0
    }
  },
  mounted() {  // 页面元素渲染之后再触发
    var option = {
      title: {
        text: '各季度用户数量统计',
        subtext: '趋势图',
        left: 'center'
      },
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      xAxis: {
        type: 'category',
        data: ["第一季度", "第二季度", "第三季度", "第四季度"]
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: "所有用户",
          data: [],
          type: 'bar'
        },
        {
          name: "所有用户",
          data: [],
          type: 'line'
        },
        {
          name: "律师",
          data: [],
          type: 'bar'
        },
        {
          name: "律师",
          data: [],
          type: 'line'
        },
        {
          name: "委托人",
          data: [],
          type: 'bar'
        },
        {
          name: "委托人",
          data: [],
          type: 'line'
        }
      ]
    };


    var articleOption = {
      title: {
        text: '文章数量统计',
        subtext: '趋势图',
        left: 'center'
      },
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      xAxis: {
        type: 'category',
        data: ["一月", "二月","三月", "四月","五月", "六月","七月", "八月","九月", "十月","十一月", "十二月"]
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: "文章",
          data: [],
          type: 'bar'
        },
        {
          name: "文章",
          data: [],
          type: 'line'
        }
      ]
    };

    // 饼图

    var pieOption = {
      title: {
        text: '各季度用户数量统计',
        subtext: '比例图',
        left: 'center'
      },
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b} : {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: "律师",
          type: 'pie',
          radius: '55%',
          center: ['25%', '70%'],
          label:{            //饼图图形上的文本标签
            normal:{
              show:true,
              position:'inner', //标签的位置
              textStyle : {
                fontWeight : 300 ,
                fontSize : 14,    //文字的字体大小
                color: "#fff"
              },
              formatter:'{c}({d}%)'
            }
          },
          data: [

          ],  // 填空
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        },
        {
          name: "委托人",
          type: 'pie',
          radius: '50%',
          center: ['75%', '50%'],
          label:{            //饼图图形上的文本标签
            normal:{
              show:true,
              position:'inner', //标签的位置
              textStyle : {
                fontWeight : 300 ,
                fontSize : 14,    //文字的字体大小
                color: "#fff"
              },
              formatter:'{d}%'
            }
          },
          data: [],  // 填空
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ],

    };

    var chartDom = document.getElementById('main');
    var myChart = echarts.init(chartDom);

    var articleChartDom = document.getElementById('article');
    var articleChart = echarts.init(articleChartDom);

    var pieDom = document.getElementById('pie');
    var pieChart = echarts.init(pieDom);


    this.request.get("/echarts/members").then(res => {
      // 填空
      console.log(res.data[0])
      // option.xAxis.data = res.data.x
      option.series[0].data = res.data[0]
      option.series[1].data = res.data[0]
      option.series[2].data = res.data[1]
      option.series[3].data = res.data[1]
      option.series[4].data = res.data[2]
      option.series[5].data = res.data[2]
      // 数据准备完毕之后再set
      myChart.setOption(option);
      this.lawyerNum = res.data[1][0] + res.data[1][1] + res.data[1][2] + res.data[1][3]
      this.userNum = res.data[2][0] + res.data[2][1] + res.data[2][2] + res.data[2][3]
      this.sysUserNum = this.lawyerNum + this.userNum
      pieOption.series[0].data = [
        {name: "第一季度", value: res.data[1][0]},
        {name: "第二季度", value: res.data[1][1]},
        {name: "第三季度", value: res.data[1][2]},
        {name: "第四季度", value: res.data[1][3]},
      ]

      pieOption.series[1].data = [
        {name: "第一季度", value: res.data[2][0]},
        {name: "第二季度", value: res.data[2][1]},
        {name: "第三季度", value: res.data[2][2]},
        {name: "第四季度", value: res.data[2][3]},
      ]
      pieChart.setOption(pieOption)
    })

    this.request.get("/echarts/article/count").then(res =>{
      this.articleNum = res.data
    })

    this.request.get("/echarts/article/month").then(res =>{
      console.log(res.data)
      articleOption.series[0].data = res.data
      articleOption.series[1].data = res.data
      articleChart.setOption(articleOption)
    })
  }
}
</script>

<style scoped>

</style>
