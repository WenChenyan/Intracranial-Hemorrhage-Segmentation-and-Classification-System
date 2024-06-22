<template>
  <div style="width: 80%;height: 90%;position: absolute;margin-left: 10%">
    <div style="width: 100%;height: 100%;position: absolute">
      <!-- 结果展示 -->
      <el-card shadow="always" style="width:100%;height: 100%">
        <div style="padding: 1%;margin-top: 2%"><el-divider ><h2>分割&分类结果</h2></el-divider></div>
        <el-container style="height: 68vh;">
          <el-container style="height: 68vh;margin-top: 3%">
            <!--    分割结果      -->
            <el-aside style="width:55%;height: 95%;">
              <el-tabs type="border-card" style="height: 100%;">
                <el-tab-pane label="3D分割结果" :disabled="!existModel">
                  <div style="height: 50vh;">
                    <div id="inset" style="width: 100px;float: left;z-index: 4;position: relative"></div>
                    <div id="gui_list" style="position: relative;float:right;z-index: 3;"> </div>
                    <div id="container" style="background-color: #d8e1f0;margin-right: 10px; z-index: 2;width: 100%;height: 60vh;position: absolute" ></div>
                  </div>
                </el-tab-pane>
                <el-tab-pane label="2D分割结果" style="max-height: 100%" align="center">
                  <div style="height: 50vh;width: 100%;background-color: #0d0d0d">
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
            <el-button type="success" plain size="large" style="float: right;"> 保存</el-button>
          </el-footer>
        </el-container>

      </el-card>

    </div>
  </div>

</template>

<script>
import Plotly from 'plotly.js-dist';

import * as THREE from '/public/static/build/three.module.js';
import { GUI } from '/public/static/build/jsm/libs/dat.gui.module.js';
import { TrackballControls } from '/public/static/build/jsm/controls/TrackballControls.js';
import { VTKLoader } from '/public/static/build/jsm/loaders/VTKLoader.js';

let camera, controls, scene, renderer, stats, container2, renderer2, camera2, axes2, scene2;
export default {
  name: "Result",
  data() {
    return {
      id: this.$route.query.id,
      user: sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {},
      existModel:false,
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
  created() {
    this.load();
  },
  mounted(){
    setTimeout(()=>{
      this.drawChart(this.chartIndex, this.chartLayout);
      this.init();
      this.animate();
    },100)
  },
  computed: {
    currentImage() {
      return "http://localhost:9090/file/"+this.segImgs[this.chartIndex];
    }
  },
  methods: {
    load(){
      this.request.get("/history/"+this.id).then(res=>{
        if (res.code === "200"){
          this.existModel = res.data.existModel
          this.VtkUrl = res.data.modelUrl
          this.segImgs = res.data.segUrl

          for (let i = 0; i < res.data.clsProb.length; i++) {
            let prob=[]
            for (let j = 0;j < 5;j++){
              prob.push(1 - res.data.clsProb[i][j]);
            }
            this.chartDataList.push([
              {x:['Intraventricular', 'Intraparenchymal', 'Subarachnoid','Epidural','Subdural'],y:res.data.clsProb[i],name: '阳性', type: 'bar' },
              {x:['Intraventricular', 'Intraparenchymal', 'Subarachnoid','Epidural','Subdural'],y:prob,name: '阳性', type: 'bar' },
            ]);
          }
        }
      })
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
      console.log("http://localhost:9090/file/"+this.VtkUrl[1])
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
</style>