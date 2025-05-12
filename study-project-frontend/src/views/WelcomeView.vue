<script setup>
import {User,Lock} from '@element-plus/icons-vue'
</script>

<template>
  <div class="login-wrapper">
    <div class="bg-image"></div>

    <div class="login-panel">
<!--      <transition name="el-fade-in-linear">-->
<!--        <router-view></router-view>-->
<!--      </transition>-->

      <router-view v-slot="{ Component }">
        <transition name="el-fade-in-linear" mode="out-in">
          <component :is="Component"   />
        </transition>
      </router-view>

    </div>

    <div class="welcome-title">
          <div class="wel-tit">歡迎來到管理平台</div>
          <div>測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試</div>
        </div>


  </div>
</template>

<style >
/* 全局重置，去除默认空白并隐藏滚动条 */
*, *::before, *::after, html, body {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  width: 100%;
  height: 100%;
  overflow: hidden; /* 禁用滚动 */
}

.login-wrapper {
  display: flex;
  width: 100vw;
  height: 100vh;
}

.bg-image {
  flex: 1;
  /* 移除 fixed，保持背景根据容器调整 */
  background: url(https://d1hjkbq40fs2x4.cloudfront.net/2019-12-23/files/canon-landscape-photography-preparation-tips_1968-2.jpg) center center / cover no-repeat;
}

.login-panel {
  width: 400px;
  background-color: #fff;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 0 20px;
}

.content {
  margin-top: 150px;
  text-align: center;
  width: 100%;
}

.title {
  font-size: 25px;
  font-weight: bold;
}

.subtitle {
  font-size: 14px;
  color: grey;
  margin-top: 8px;
}

.input-wrapper {
  margin-top: 50px;
  .pw{
    margin-top:10px;
  }
}

.sub-mit{
  margin-top:40px;

  .sub-button{
    width: 270px;
  }

}

.sub-reg{
  margin-top:10px;
  .sub-button{
    width: 270px;
  }
}

.men-no{
  margin-top: 10px;
  .men{
    text-align: left;
  }

  .forget{
    text-align: right;
  }
}
.welcome-title{

  position: absolute;
  bottom: 30px;
  left: 30px;
  color: white;
  text-shadow:0 10px black;

  .wel-tit{
    font-size: 30px;
    font-weight: bold;
  }
}
</style>