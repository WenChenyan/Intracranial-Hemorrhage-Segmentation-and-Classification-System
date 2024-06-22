<template>
  <div class="wrapper">
    <vue-particles class="wrapper-bg"
                   color="#39AFFD"
                   :particleOpacity="0.7"
                   :particlesNumber="100"
                   shapeType="circle"
                   :particleSize="4"
                   linesColor="#8DD1FE"
                   :linesWidth="1"
                   :lineLinked="true"
                   :lineOpacity="0.4"
                   :linesDistance="150"
                   :moveSpeed="3"
                   :hoverEffect="true"
                   hoverMode="grab"
                   :clickEffect="true"
                   clickMode="push"
    >
    </vue-particles>
    <!--    3D脑出血动态显示-->
    <div style="height: 100%;width: 55%;position: absolute">
      <div id="inset"></div>
      <div id="container" style="background-color: black;margin-left: 20px; z-index: 2;width: 40%;position: relative" ></div>
      <div id="gui_list" style="position: relative;z-index: 3;margin-top: 20px"> </div>
    </div>
    <div id="info">
      <h3>上图为脑出血3D可视化实例</h3>
    </div>

    <div class="login_card">
      <div style="color: #5acce6;margin: 20px 0; text-align: center; font-size: 24px"><b>注 册</b></div>
      <el-form :model="user" :rules="rules" ref="userForm">
        <el-form-item prop="username">
          <el-input placeholder="请输入账号" size="medium" prefix-icon="el-icon-user" v-model="user.username"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input placeholder="请输入密码" size="medium" prefix-icon="el-icon-lock" show-password v-model="user.password"></el-input>
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input placeholder="请确认密码" size="medium" prefix-icon="el-icon-lock" show-password v-model="user.confirmPassword"></el-input>
        </el-form-item>
        <el-form-item style="margin: 5px 0; text-align: right">
          <el-button type="primary" size="small"  autocomplete="off" @click="login">注册</el-button>
          <el-button type="warning" size="small"  autocomplete="off" @click="$router.push('/login')">返回登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import * as THREE from '/public/static/build/three.module.js';
import Stats from '/public/static/build/jsm/libs/stats.module.js';
import { GUI } from '/public/static/build/jsm/libs/dat.gui.module.js';
import { TrackballControls } from '/public/static/build/jsm/controls/TrackballControls.js';
import { NRRDLoader } from '/public/static/build/jsm/loaders/NRRDLoader.js';
import { VTKLoader } from '/public/static/build/jsm/loaders/VTKLoader.js';
let camera, controls, scene, renderer, stats, container2, renderer2, camera2, axes2, scene2;
export default {
  name: "Login",
  data() {
    return {
      user: {},
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 10, message: '长度在 3 到 5 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
        ],
      }
    }
  },
  mounted() {
    this.init();
    this.animate();
  },
  methods: {
    login() {
      this.$refs['userForm'].validate((valid) => {
        if (valid) {  // 表单校验合法
          if (this.user.password !== this.user.confirmPassword) {
            this.$message.error("两次输入的密码不一致")
            return false
          }
          this.request.post("/user/register", this.user).then(res => {
            if(res.code === '200') {
              this.$message.success("注册成功")
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      });
    },

    init() {


      camera = new THREE.PerspectiveCamera(60, (window.innerWidth*0.4) / (window.innerHeight*0.6), 0.01, 1e10);

      //定义相机的位置
      camera.position.z = -250;


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

        gui.add( visibilityControl, "visible" ).name( "head Visible" ).onChange( function () {

          mesh.visible = visibilityControl.visible;
          renderer.render( scene, camera );

        } );

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
      renderer.setSize(window.innerWidth*0.4, window.innerHeight*0.6);
      renderer.setClearColor(0xD8E1F0, 1);
      const container = document.getElementById('container')
      document.body.appendChild(container);
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

      // stats.update();
    },
    onWindowResize() {
      camera.aspect = (window.innerWidth*0.4) / (window.innerHeight*0.6);
      camera.updateProjectionMatrix();
      renderer.setSize(window.innerWidth*0.4, window.innerHeight*0.6);
      controls.handleResize();
    },
    setupInset() {
      const insetWidth = 150;
      const insetHeight = 150;
      container2 = document.getElementById('inset');
      container2.width = insetWidth;
      container2.height = insetHeight;

      // Renderer
      renderer2 = new THREE.WebGLRenderer({ alpha: true });
      renderer2.setClearColor(0x000000);
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
.wrapper{
  width: 100%;
  height: 100%;
}
.wrapper-bg{
  position: absolute;
  width: 100%;
  height: 100%;
  background-color: #0d0d0d;
  z-index: 1;
}

.login_card {
  position: relative;
  float: right;
  background-color:  #090c29;
  width: 30%;
  height: 50%;
  padding: 10px;
  margin: 100px;
  border-radius: 15px;
  z-index: 2;
}
#inset {
  width: 150px;
  height: 150px;
  background-color: black; /* or transparent; will show through only if renderer alpha: true */
  border: none; /* or none; */
  margin: 0;
  padding: 0px;
  position: absolute;
  left: 20px;
  bottom: 20px;
  z-index: 100;
}
#info {
  position: absolute;
  background-color: rgba(255, 255, 255, 0.5);
  color:#4fc3f7;
  margin-top:400px;
  margin-left:200px;
  z-index: 2;
  width: 240px;
  height: 30px;
  border-radius: 15px;
}
</style>
