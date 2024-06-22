<template>
  <div style="width: 100%;height: 100%;position: absolute;background-color: #f5f9f6">
  <div style="width: 100%;height: 90%;position: absolute">
    <el-steps :active="active" finish-status="success" align-center
              style="background-color: #f5f9f6;margin-top: 2%;margin-left: 25%;margin-right: 25%">
      <el-step title="上传数据" icon="el-icon-upload" ></el-step>
      <el-step title="选择模型/填写病人信息" icon="el-icon-menu" ></el-step>
      <el-step title="下载/保存结果" icon="el-icon-download" ></el-step>
    </el-steps>
    <div style="margin-top: 2%;margin-left: 15%;margin-right: 15%;width: 70%;height: 85%">
      <!-- 上传图片 -->
      <el-card v-if="active === 0" shadow="always" style="margin-left: 8%;margin-right: 8%;width:84%;height: 100%;border:1px solid #00a593">
        <div style="margin-bottom: 1%">
          <div style="margin-bottom: 1%"><el-divider ><h2>选择对应数据格式</h2></el-divider></div>
        </div>
        <el-tabs type="border-card" style="height: 60vh;" @tab-click="handleClick">
        <!--   jpg/png文件       -->
          <el-tab-pane label="jpg/png文件" style="margin: 10px;height: 100%">
            <el-upload
                ref="upload"
                :http-request="httpRequest"
                :before-upload="beforePngUpload"
                list-type="picture-card"
                :on-preview="handlePictureCardPreview"
                :on-remove="handleRemove"
                :limit="50"
                on-exceed="handleExceed">
              <i slot="default" class="el-icon-plus"></i>
              <div slot="file" slot-scope="{ file }">
                <img class="el-upload-list__item-thumbnail" :src="file.url" alt="">
                <span class="el-upload-list__item-actions">
          <span class="el-upload-list__item-preview" @click="handlePictureCardPreview(file)">
            <i class="el-icon-zoom-in"></i>
          </span>
          <span class="el-upload-list__item-delete" @click="handleRemove(file)">
            <i class="el-icon-delete"></i>
          </span>
        </span>
              </div>
            </el-upload>
            <el-dialog :visible.sync="dialogVisible" style="z-index: 999;position: absolute;">
              <img width="100%" :src="dialogImageUrl" alt="">
            </el-dialog>
          </el-tab-pane>
          <!--   nii文件       -->
          <el-tab-pane label="nii文件">
            <el-upload
                align="center"
                class="upload-demo"
                drag
                :http-request="httpRequest"
                :before-upload="beforeNiiUpload"
                multiple
                :limit="1"
                :on-exceed="handleExceed"
                style="height: 100%;width: 100%;margin-top: 10%">
              <i class="el-icon-upload"></i>
              <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            </el-upload>
          </el-tab-pane>
        </el-tabs>

        <el-button type="primary" plain  size="medium" style="position: relative;float:right;margin-top: 1%" @click="next">
          下一步
          <i class="el-icon-arrow-right el-icon--right"></i>
        </el-button>
      </el-card>


      <!-- 模型选择 -->
      <el-card v-else-if="active === 1" shadow="always" style="margin-left: 8%;margin-right: 8%;width:84%;height: 100%;border:1px solid #00a593">
        <div style="height: 67vh">
          <div style="padding: 1vh;">
            <div style="padding: 1vh"><el-divider ><h2>选择模型</h2></el-divider></div>
          </div>
          <el-form ref="ruleForm" label-width="100px"  class="demo-ruleForm"
                   style="margin-top: 1%;width: 60%;margin-left: 20%;margin-right: 20%">
            <el-row>
              <el-col :span="12">
                <el-form-item label="分割模型" prop="model1" >
                  <el-select v-model="detectRequest.segModelName" placeholder="请选择模型" size="medium" style="width:120px "  >
                    <el-option v-for="item in segOptions" :key="item" :value="item">
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="分类模型" prop="model2">
                  <el-select v-model="detectRequest.clsModelName" placeholder="请选择模型" size="medium" style="width:120px "  >
                    <el-option v-for="item in clsOptions" :key="item" :value="item">
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <div style="padding: 1vh;">
            <div style="padding: 1vh"><el-divider ><h2>填写病人信息</h2></el-divider></div>
          </div>
          <el-form style="margin-top: 1%;width: 60%;margin-left: 20%;margin-right: 20%" size="medium">
            <el-form-item label="病人姓名:" >
              <el-input v-model="detectRequest.patientName" autocomplete="off" style="width: 50%"></el-input>
            </el-form-item>
            <el-form-item label="性别:">
              <el-radio-group v-model="detectRequest.patientSex" style="margin-left: 5%">
                <el-radio label="男性" border  size="medium">男性</el-radio>
                <el-radio label="女性" border  size="medium">女性</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="病史:">
              <el-input v-model="detectRequest.patientInfo"
                        autocomplete="off"
                        type="textarea"
                        :rows="3">
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <el-button type="warning" plain size="medium" icon="el-icon-arrow-left" style="margin-left: 0;margin-top: 1%" @click="before">上一步</el-button>
        <el-button type="primary" plain size="medium" style="float: right;margin-top: 1%" @click="next"> 检测<i class="el-icon-arrow-right el-icon--right"></i></el-button>
      </el-card>

      <!-- 结果展示 -->
      <el-card  v-else-if="active === 2 " shadow="always" style="width:100%;height: 100%;border:1px solid #00a593">
        <div style="padding: 1%"><el-divider ><h2>分割&分类结果</h2></el-divider></div>
        <el-container style="height: 68vh;">
        <el-container style="height: 68vh;">
          <!--    分割结果      -->
          <el-aside style="width:53%;height: 95%;">
            <el-tabs type="border-card" style="height: 100%;">
              <el-tab-pane label="3D分割结果" :disabled="!detectRequest.existNii">
                <div style="height: 54vh;">
                  <div id="inset" style="width: 100px;float: left;z-index: 4;position: relative"></div>
                  <div id="gui_list" style="position: relative;float:right;z-index: 3;"> </div>
                  <div id="container" style="background-color: #d8e1f0;margin-right: 10px; z-index: 2;width: 100%;height: 60vh;position: absolute" ></div>
                </div>
              </el-tab-pane>
              <el-tab-pane label="2D分割结果" style="max-height: 100%" align="center">
                <div style="height: 54vh;width: 100%;background-color: #0d0d0d">
                  <el-image id="segout" :src="currentImage" style="height: 54vh" ></el-image>
                </div>
              </el-tab-pane>

            </el-tabs>
          <!-- 分类结果           -->
          </el-aside>
          <el-main style="padding: 0;margin-left: 2vh;">
            <div id="chart" style="height: 94%;"></div>
            <el-button  @click="prevChart">上一张</el-button>
            <el-button  @click="nextChart">下一张</el-button>
          </el-main>
        </el-container>
          <el-footer>
            <el-button type="warning" plain size="medium" icon="el-icon-arrow-left" style="margin-left: 0;" @click="before">上一步</el-button>
            <el-button type="success" plain size="medium" style="float: right;" @click="next"> 保存</el-button>
          </el-footer>
        </el-container>

      </el-card>

      <!-- 提示去历史记录或者文件正在下载 -->
      <el-card v-else-if="active === 3 " shadow="always" style="height: 85vh;width: 150vh;">

        <h2>Default Content</h2>
        <p>This is default content.</p>
      </el-card>
    </div>

    <div style="opacity: 1;position: absolute;margin-top: -30%;margin-left: 45%" v-if="isLoading">
      <div class="animbox" style="">
        <div></div>
        <div></div>
        <div></div>
        <div></div>
        <div></div>
      </div>
      <div style="position: relative;color: #279fcf ;width: 200px;text-align: center"><p>正在处理数据中,请稍等</p></div>
    </div>

  </div>
  </div>

</template>

<script>
import Plotly from 'plotly.js-dist';

import * as THREE from '/public/static/build/three.module.js';
import { GUI } from '/public/static/build/jsm/libs/dat.gui.module.js';
import { TrackballControls } from '/public/static/build/jsm/controls/TrackballControls.js';
import { VTKLoader } from '/public/static/build/jsm/loaders/VTKLoader.js';
import axios from "axios";
let camera, controls, scene, renderer, stats, container2, renderer2, camera2, axes2, scene2;
export default {
  name: "Detail",
  data() {
    return {
      active: 0,
      dialogImageUrl: '',
      dialogVisible: false,
      disabled: false,
      segOptions:["DMANet","SWin-unet","CamoFormer","TransFuse","DconnNet"],
      clsOptions:["MQFormer","Mobilevit","C-Tran","ViT","Resnet101"],
      isLoading:false,
      detectRequest:{
        existNii:false,
        segModelName:"",
        clsModelName:"",
        filesUrl:[],
        patientName:"",
        patientSex:"",
        patientInfo:""
      },
      VtkUrl:[],
      segImgs:[],
      chartIndex:0,
      chartDataList: [ ],//分类结果
      chartLayout: {  //柱状图
        barmode: 'stack',
        yaxis: {
          title: 'Probability',
          titlefont: {
            size: 16,
            color: 'rgb(107, 107, 107)'
          },
          tickfont: {
            size: 14,
            color: 'rgb(107, 107, 107)',
        }},
      },
    };
  },

  computed: {
    currentImage() {
      return "http://localhost:9090/file/"+this.segImgs[this.chartIndex];
    }
  },
  watch: {
    active(newActive, oldActive) {
      // active 值变化时检查是否需要重新绘制图表
      if (newActive === 2) {
        this.$nextTick(() => {
          this.drawChart(this.chartIndex, this.chartLayout);
          this.init();
          this.animate();
        });
      }
    }
  },
  methods: {
    handleClick(tab){
      this.detectRequest.existNii = tab.$el.id !== "pane-0";
      this.detectRequest.filesUrl = [];
    },
    next() {

      // console.log(this.active)
      console.log(this.detectRequest)
      if (this.active === 1) {
        this.isLoading = true
        this.request.post("/detectImage/OutWithCache",this.detectRequest).then(res=>{
          if(res.code === "200"){
            console.log(res.data)
            this.VtkUrl = res.data.modelUrl
            this.segImgs = res.data.segUrl

            for (let i = 0; i < res.data.clsProb.length; i++) {
              let prob=[]
              for (let j = 0;j < 5;j++){
                prob.push(1 - res.data.clsProb[i][j]);
              }
              this.chartDataList.push([
                {x:['Intraventricular', 'Intraparenchymal', 'Subarachnoid','Epidural','Subdural'],y:res.data.clsProb[i],name: '阳性', type: 'bar' },
                {x:['Intraventricular', 'Intraparenchymal', 'Subarachnoid','Epidural','Subdural'],y:prob,name: '阴性', type: 'bar' },
              ]);
            }
            console.log(this.VtkUrl)
            console.log(this.segImgs)
            console.log(this.chartDataList)
            setTimeout(()=>{
              this.active++;
              this.isLoading = false
            },10)
          }
        })
      }
      else if (this.active++ > 2) this.active = 2;

    },
    before() {
      if (this.active-- <= 0) this.active = 0;
    },
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
        if (res.data.code === "200"){
          this.detectRequest.filesUrl.push(res.data.data)
        }
      })

    },
    beforeNiiUpload(file) {
      const isNii =  file.name.endsWith('.nii');
      if (!isNii) {
        this.$message.error('只能上传nii文件');
      }
      return isNii;
    },
    beforePngUpload(file) {
      const isPng =  file.name.endsWith('.png')|| file.name.endsWith('.jpg');
      if (!isPng) {
        this.$message.error('只能上传jpg/png文件');
      }
      return isPng;
    },
    handleRemove(file, fileList) {
      console.log(file, fileList);
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    },
    handleExceed(files, fileList) {
      this.$message.warning(`超出可上传文件个数`);
    },
    drawChart(data, layout) {
      // 绘制图表
      layout.title = `第 ${data + 1} 张分类结果`;
      Plotly.newPlot('chart', this.chartDataList[data], layout);
    },
    prevChart() {
      this.chartIndex = (this.chartIndex - 1 + this.chartDataList.length) % this.chartDataList.length;
      this.drawChart(this.chartIndex,this.chartLayout);
    },
    nextChart() {
      this.chartIndex = (this.chartIndex + 1) % this.chartDataList.length;
      this.drawChart(this.chartIndex,this.chartLayout);
    },
    init() {


      camera = new THREE.PerspectiveCamera(60, (window.innerWidth*0.3) / (window.innerHeight*0.6), 0.01, 1e10);
      //定义相机的位置
      camera.position.z = -350;


      camera.up.x = 0;
      camera.up.y = -1;
      camera.up.z = 0;

      scene = new THREE.Scene();
      scene.add(camera);

      // Light
      const dirLight = new THREE.DirectionalLight(0xffffff);
      dirLight.position.set(200, 200, 1000).normalize();
      camera.add(dirLight);
      camera.add(dirLight.target);

      // VTK Loader
      const vtkloader = new VTKLoader();
      vtkloader.load( "http://localhost:9090/file/"+this.VtkUrl[1], function ( geometry ) {

        geometry.computeVertexNormals();
        var material = new THREE.MeshPhongMaterial( {
          color: 0x0000ff,//模型颜色
          opacity: 0.3,//模型透明度
          transparent: true,
          side: THREE.BackSide,//FrontSid外侧，DoubleSide内外两测
          depthTest: false});

        var mesh = new THREE.Mesh( geometry, material ); // mesh 物体对象
        mesh.position.set( -100, -100, -125 );
        scene.add( mesh );
        var visibilityControl = {
          visible: true
        };

        gui.add( visibilityControl, "visible" ).name( "head Visible" ).onChange( function () {

          mesh.visible = visibilityControl.visible;
          renderer.render( scene, camera );

        } );

      } );
      vtkloader.load( "http://localhost:9090/file/"+this.VtkUrl[0], function ( geometry ) {

        geometry.computeVertexNormals();
        var material = new THREE.MeshPhongMaterial( {
          color: 0xff0000});

        var mesh = new THREE.Mesh( geometry, material ); // mesh 物体对象
        mesh.position.set( -100, -100, -125 );
        scene.add( mesh );
        var visibilityControl = {
          visible: true
        };

        gui.add( visibilityControl, "visible" ).name( "bleed Visible" ).onChange( function () {

          mesh.visible = visibilityControl.visible;
          renderer.render( scene, camera );

        } );
        const gui_list = document.getElementById('gui_list');
        gui_list.width=300;
        gui_list.height=200;
        gui_list.appendChild(gui.domElement);

      } );

      // Renderer
      renderer = new THREE.WebGLRenderer({ alpha: true });
      renderer.setPixelRatio(window.devicePixelRatio);
      renderer.setSize(window.innerWidth*0.4, window.innerHeight*0.7);
      renderer.setClearColor(0xD8E1F0, 1);
      const container = document.getElementById('container')
      // container.width = 200;
      // container.height = 250;
      container.appendChild(renderer.domElement);

      // Controls
      controls = new TrackballControls(camera, renderer.domElement);
      controls.rotateSpeed = 5.0;
      controls.zoomSpeed = 5;
      controls.panSpeed = 2;
      controls.staticMoving = true;

      // Stats
      // stats = new Stats();
      // container.appendChild(stats.dom);

      // GUI
      const gui = new GUI();

      this.setupInset();

      window.addEventListener('resize', this.onWindowResize, false);
    },
    animate() {
      requestAnimationFrame(this.animate);
      controls.update();

      // Copy position of the camera into inset
      camera2.position.copy(camera.position);
      camera2.position.sub(controls.target);
      camera2.position.setLength(300);
      camera2.lookAt(scene2.position);

      renderer.render(scene, camera);
      renderer2.render(scene2, camera2);

    },
    onWindowResize() {
      camera.aspect = (window.innerWidth*0.3) / (window.innerHeight*0.6);
      camera.updateProjectionMatrix();
      renderer.setSize(window.innerWidth*0.3, window.innerHeight*0.6);
      controls.handleResize();
    },
    setupInset() {
      const insetWidth = 80;
      const insetHeight = 80;
      container2 = document.getElementById('inset');
      container2.width = insetWidth;
      container2.height = insetHeight;


      // Renderer
      renderer2 = new THREE.WebGLRenderer({ alpha: true });
      //renderer2.setClearColor(0x000000);
      renderer2.setSize(insetWidth, insetHeight);
      container2.appendChild(renderer2.domElement);

      // Scene
      scene2 = new THREE.Scene();

      // Camera
      camera2 = new THREE.PerspectiveCamera(50, insetWidth / insetHeight, 1, 1000);
      camera2.up = camera.up;

      // Axes
      axes2 = new THREE.AxesHelper(100);
      scene2.add(axes2);
    }
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
  width: 360px;
  height: 180px;
  text-align: center;
  position: relative;
  overflow: hidden;
}
.animbox {
  margin: 25px auto;
  width: 200px;
  text-align: center;
}
/*设置各竖条的共有样式*/
.animbox > div {
  background-color: #279fcf;
  width: 4px;
  height: 35px;
  border-radius: 2px;
  margin: 2px;
  animation-fill-mode: both;
  display: inline-block;
  animation: anim 0.9s 0s infinite cubic-bezier(.11, .49, .38, .78);
}
/*设置动画延迟*/
.animbox > :nth-child(2), .animbox > :nth-child(4) {
  animation-delay: 0.25s !important;
}

.animbox > :nth-child(1), .animbox > :nth-child(5) {
  animation-delay: 0.5s !important;
}
/*定义动画*/
@keyframes anim {
  0% {  transform: scaley(1); }
  80% {  transform: scaley(0.3); }
  90% {  transform: scaley(1);   }
}

</style>
