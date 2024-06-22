<template>
  <div class="bg" style=" display: flex; flex-direction: column;">
<!--    图片-->
      <div style="height: 100%;width: 100%">
      <div class="h1_bg" >
        <div class="titled">
          <p>脑影智析</p>
          <p style="font-size: 20px">——颅内出血分割与分型系统</p>
        </div>
        <div class="time-table-section" style="width: 90%;height:20%;position: absolute;margin-top: 37%;margin-left: 5%">
          <el-row>
            <el-col :span="6" >
              <el-card class="service-time"
                       style="background-image: linear-gradient(120deg,#7fc3fd,#4794e8);border: 2px solid #7fc3fd"
                       v-if="show">
                <span class="info-icon"><i class="el-icon-chat-dot-round" style="color: #4e5afe;float: left"></i></span>
                <h3 style="color: #0d0d0d">论坛交流</h3>
              </el-card>
            </el-col>
            <el-col :span="6" offset="3">
              <el-card class="service-time "
                       style="background-image: linear-gradient(120deg,#3498db,#8e44ad);border: 2px solid #3498db"
                       v-if="show">
                <span class="info-icon"><i class="el-icon-menu" style="color: #00d9a5;float: left;"></i></span>
                <h3 style="color: #0d0d0d">AI 诊 断</h3>
              </el-card>
            </el-col>
            <el-col :span="6" offset="3">
              <el-card class="service-time"
                       style="background-image: linear-gradient(120deg,#8e44ad,#61fadb);border: 2px solid #8e44ad"
                       v-if="show">
                <span class="info-icon"><i class="el-icon-folder-checked" style="color: #ff3d85;float: left"></i></span>
                <h3 style="color: #0d0d0d">结果存档</h3>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </div>
    </div>

    <!--    介绍-->
    <div  style="background-image: linear-gradient(to bottom, #000C2A, #f5f9f6);height: 52rem;padding-top: 1%;">
      <div style="margin: 2% 5% 5% 5%">
        <div style="padding: 1vh"><el-divider ><h2 style="color: #5193bb">颅内出血诊断难点</h2></el-divider></div>
        <el-container>
          <el-aside width="30%" style=" font-family: monospace;margin-left: 27%;margin-top: 5%;"> <!---->
            <h2 style="color: #f4fcf0;font-size: 30px;">出血区域边界模糊</h2>
            <ul style="color: #b1cdbc;padding: 5px;">
              <li style="font-size: 20px;padding-top: 5px;">出血区域与正常组织灰度值</li>
              <li style="font-size: 20px;padding-top: 5px;">相近，对比度较低使边界难</li>
              <li style="font-size: 20px;padding-top: 5px;">以界定。</li>
            </ul>
          </el-aside>
          <el-main>
            <img src="../../assets/27.png" style="height: 256px">
          </el-main>
        </el-container>
        <el-container style="margin-left: 26%;">
          <el-main >
            <img src="../../assets/24.png" style="height: 256px;font-family: monospace;margin-top: 5%;">
          </el-main>
          <el-aside width="57%" style=" font-family: monospace;margin-left: 10%;margin-top: 5%;"> <!---->
            <h2 style="color: #343531;font-size: 30px;">同时存在多种出血亚型</h2>
            <ul style="padding: 5px;">
              <li style="color: #c8ead9;font-size: 20px;padding-top: 5px;">同一CT扫描切片存在多种出</li>
              <li style="color: #c8ead9;font-size: 20px;padding-top: 5px;">血，不同出血亚型位置、特</li>
              <li style="color: #c8ead9;font-size: 20px;padding-top: 5px;">征均存在一定差异。</li>
            </ul>
          </el-aside>
        </el-container>
        <el-container>
          <el-aside width="30%" style=" font-family: monospace;margin-left: 27%;margin-top: 5%;"> <!---->
            <h2 style="color: #343531;font-size: 30px;">颅内出血中混杂低密度出血</h2>
            <ul style="color: #6e807a;padding: 5px;">
              <li style="font-size: 20px;padding-top: 5px;">在颅内出血中，可看到混合</li>
              <li style="font-size: 20px;padding-top: 5px;">着活动性出血或新鲜外渗血</li>
              <li style="font-size: 20px;padding-top: 5px;">液的等密度或低密度区域。</li>
            </ul>
          </el-aside>
          <el-main>
            <img src="../../assets/20.png" style="height: 256px">
          </el-main>
        </el-container>
      </div>
    </div>

    <div style="background-color: #f5f9f6;height: 95rem;margin-top: 10%;">
      <div style="margin: 5%">
        <div ><el-divider ><h2>系统优势</h2></el-divider></div>
        <el-container>
          <el-header width="40%" style=" font-family: monospace;margin-left: 40%;margin-top: 2%;"> <!---->
            <h2 style="color: #343531;font-size: 30px;">出血区域精准分割与分型</h2>
            <ul style="color: #6e807a;padding: 5px;">
              <li style="font-size: 20px;padding-top: 5px;">系统提供最先进的颅内出血分割算法执行分割任务</li>
              <li style="font-size: 20px;padding-top: 5px;">系统利用分割结果指导先进的分类网络进行出血分型</li>
            </ul>
          </el-header>
          <el-main>
            <div style="width: 100%;padding-left: 23%;margin-top: 3%">
              <el-row>
                <el-col :span="6">
                  <img src="../../assets/aad6621b-d824-4c34-87c0-62b813c7bc45-27.png" style="height: 256px">
                </el-col>
                <el-col :span="6">
                  <img src="../../assets/aad6621b-d824-4c34-87c0-62b813c7bc45-24.png" style="height: 256px">
                </el-col>
                <el-col :span="6">
                  <img src="../../assets/fbd15c74-9d6c-4a93-9286-d87a9e7038f1-0.png" style="height: 256px">
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="6">
                  <img src="../../assets/newplot (4).png" style="height: 256px">
                </el-col>
                <el-col :span="6">
                  <img src="../../assets/newplot (3).png" style="height: 256px">
                </el-col>
                <el-col :span="6">
                  <img src="../../assets/newplot (2).png" style="height: 256px">
                </el-col>
              </el-row>
            </div>
          </el-main>
        </el-container>
        <el-container>
          <el-header id="listen" width="40%" style=" font-family: monospace;margin-left: 40%;margin-top: 2%;"> <!---->
            <h2 style="color: #343531;font-size: 30px;">颅骨与出血区域3D建模</h2>
            <ul style="color: #6e807a;padding: 5px;">
              <li style="font-size: 20px;padding-top: 5px;">利用算法得到的分割结果对出血区域进行3D建模</li>
            </ul>
          </el-header>
          <el-main>
            <div style="width: 40%;margin-left: 35%">
              <div id="inset" style="width: 100px;float: left;z-index: 4;position: relative"></div>
              <!--              <div id="gui_list" style="position: relative;float:right;z-index: 3;"> </div>-->
              <div id="container" style="background-color:  #f5f9f6;position: absolute" ></div>

            </div>
          </el-main>
        </el-container>
      </div>

    </div>


  </div>


</template>

<script>
import Plotly from 'plotly.js-dist';

import * as THREE from '/public/static/build/three.module.js';
import Stats from '/public/static/build/jsm/libs/stats.module.js';
import { TrackballControls } from '/public/static/build/jsm/controls/TrackballControls.js';
import { NRRDLoader } from '/public/static/build/jsm/loaders/NRRDLoader.js';
import { VTKLoader } from '/public/static/build/jsm/loaders/VTKLoader.js';
let camera, controls, scene, renderer, stats, container2, renderer2, camera2, axes2, scene2;
export default {
  name: "FrontHome",
  data() {
    return {
     show:false,
     showed:false,
    }
  },
  mounted() {
    // 延迟显示，以便触发动画效果
    setTimeout(() => {
      this.show = true;
    }, 100);
    this.init();
    this.animate();
  },
  methods: {
    init() {


      camera = new THREE.PerspectiveCamera(60, (window.innerWidth*0.3) / (window.innerHeight*0.6), 0.01, 1e10);
      //定义相机的位置
      camera.position.y = 250;

      scene = new THREE.Scene();
      scene.add(camera);

      // Light
      const dirLight = new THREE.DirectionalLight(0xffffff);
      dirLight.position.set(200, 200, 1000).normalize();
      camera.add(dirLight);
      camera.add(dirLight.target);

      // VTK Loader
      const vtkloader = new VTKLoader();
      vtkloader.load( "http://localhost:9090/file/skull.vtk", function ( geometry ) {

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

        /*gui.add( visibilityControl, "visible" ).name( "head Visible" ).onChange( function () {

          mesh.visible = visibilityControl.visible;
          renderer.render( scene, camera );

        } );*/

      } );
      vtkloader.load( "http://localhost:9090/file/bleed.vtk", function ( geometry ) {

        geometry.computeVertexNormals();
        var material = new THREE.MeshPhongMaterial( {
          color: 0xff0000});

        var mesh = new THREE.Mesh( geometry, material ); // mesh 物体对象
        mesh.position.set( -100, -100, -125 );
        scene.add( mesh );
        var visibilityControl = {
          visible: true
        };

        /*gui.add( visibilityControl, "visible" ).name( "bleed Visible" ).onChange( function () {

          mesh.visible = visibilityControl.visible;
          renderer.render( scene, camera );

        } );
        const gui_list = document.getElementById('gui_list');
        gui_list.width=300;
        gui_list.height=200;
        gui_list.appendChild(gui.domElement);*/

      } );

      // Renderer
      renderer = new THREE.WebGLRenderer({ alpha: true });
      renderer.setPixelRatio(window.devicePixelRatio);
      renderer.setSize(window.innerWidth*0.3, window.innerHeight*0.6);
      renderer.setClearColor(0xf5f9f6, 1);
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
      //const gui = new GUI();

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

      //stats.update();
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
    },

  }
}
</script>

<style>
.bg{
  background-color: #f5f9f6;
  width: 100%;
  height: 200%;
  display: flex;
}
.h1_bg {
  position: relative;
  width: 100%;
  height: 56rem;
  background-image: url("../../assets/R-C.jpg");
  background-size: cover;
  background-position: center;
}

.h1_bg::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.1); /* 半透明的黑色遮罩层 */
}
.titled {
  font-family: monospace;
  font-size: 50px;
  text-align:center;
  position: absolute;
  margin-top: 10%;
  width: 100%;
  color: #ffffff;
  animation: scaleUp 1s ease;
}


@keyframes scaleUp {
  0% {
    transform: scale(0);
  }
  100% {
    transform: scale(1);
  }
}

/* 应用动画到元素，并延长动画持续时间 */

.service-time {
  min-height: 5rem;
  min-width: 15rem;
  padding: 20px;
  color: #0d0d0d;
  position: relative;
  animation: scaleUp 1s ease;
}
.info-icon {
  text-align: center;
  font-size: 75px;
  line-height: normal;
  margin-bottom: 20px;
}
.service-time h3 {
  text-align: center;
  color: #fff;
  margin: 20px 0 15px 0;
  padding: 0;
  font-size: 20px;
  font-weight: 600;
  text-transform: none;
}
.service-time p {
  text-align: center;
  margin: 0;
  font-size: 14px;
}
.service-time ul {
  margin: 0;
  list-style: none;
  padding: 0;
}
.service-time ul li {
  font-size: 14px;
  border-bottom: dashed 1px #fff;
  padding-bottom: 10px;
  padding-top: 10px;
}
.left {
  float: left;
}
.right {
  float: right;
}
.el-header {
  padding: 0;
  box-sizing: border-box;
  flex-shrink: 0;
}

</style>
