<template>
  <div class="wrapper" style="overflow: hidden; height: 100vh;text-align: center;">
    <div>
      <h1 class="shadow-text" style="  font-size: 150px; color: #ff7272;margin-top: 10%">404</h1>
    </div>
    <div>
      <h1 class="shadow-text" style="font-size: 85px; color: #333333;margin-top: 20px">PAGE NOT FOUND</h1>
    </div>
    <div class="button-container" style="margin-top: 50px;">
      <button class="shadow-button " @click="backHome">返回首页</button>
    </div>
  </div>
</template>

<script>
export default {
  name: "NotFound",
  data(){
    return{
      user: sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {},
    }
  },
  methods:{
    backHome(){
      let role = this.user.role
      if (this.user && (role !== "ROLE_ADMIN")){
        this.$router.push("/front/home")
      } else {
        this.$router.push("/")
      }
    }
  }
}
</script>

<style>


.wrapper {
  height: 100vh;
  background-image: linear-gradient(to bottom right, #b3dffc, #b8ffd6);
  overflow: hidden;
}
.shadow-text {

  text-shadow: 8px 8px 8px rgba(0, 0, 0, 0.8); /* 设置更清晰的文字阴影 */
}

.button-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.shadow-button {
  padding: 20px 50px; /* 调整按钮的内边距以增加宽度和高度 */
  font-size: 15px;
  background-color: #e9fff5;
  cursor: pointer;
  color: #333333;
  font-weight: normal;
  border: 2px solid black; /* 添加黑色边框 */
  border-radius: 5px;
  box-shadow: 4px 4px 6px rgba(0, 0, 0, 0.1); /* 设置右边和下方的阴影 */
  transition: box-shadow 0.3s ease; /* 添加过渡效果 */
}
.shadow-button {
  box-shadow: 8px 8px 12px rgba(0, 128, 0, 0.5); /* 鼠标悬停时增加阴影效果 */
}
.shadow-button:hover {
  font-weight: bold;
  padding: 22px 52px; /* 调整按钮的内边距以增加宽度和高度 */
  box-shadow: 10px 10px 14px rgba(1, 130, 1, 0.5); /* 鼠标悬停时增加阴影效果 */
}
.shadow-button:active {
  background-color: rgba(190, 255, 190, 0.5); /* 设置按钮点击时的颜色变化 */
}
</style>
