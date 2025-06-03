<script setup>

import {Lock, User} from "@element-plus/icons-vue";

import {reactive} from "vue";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";
import {get, post} from "@/net";
import {useStore} from "@/stores/index.js";


const form= reactive({
  username:'',
  password:'',
  remember:false
})

const store = useStore()

const login =()=>{
  if(!form.username || !form.password){
    ElMessage.warning("請輸入帳號密碼!")
  }else{
      post('/api/auth/login',{
            username:form.username,
            password:form.password,
            remember:form.remember
      },(message)=>{
          ElMessage.success(message)
          get('api/user/me', (message) => {
            store.auth.user=message
            router.push('/index')
          }, () => {
            store.auth.user=null
          })

      })
  }

}

</script>

<template>
  <div class="login-wrapper">
    <div class="bg-image"></div>

    <div class="login-panel">
      <div class="content">
        <div class="title">登入</div>
        <div class="subtitle">在進入系統前請先登入</div>
        <div class="input-wrapper">
          <el-input v-model="form.username" type="text" placeholder="帳號名稱/e-mail">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
          <el-input class="pw" v-model="form.password" type="password" placeholder="密碼">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </div>

        <div class="men-no" style="margin-top: 10px">
          <el-row>
            <el-col :span="12" class="men">
              <el-checkbox v-model="form.remember" label="記住我"/>
            </el-col>

            <el-col :span="12" class="forget">
              <el-link @click="router.push('/forget')">忘記密碼?</el-link>
            </el-col>

          </el-row>
        </div>

        <div class="sub-mit">
          <el-button @click="login()" class="sub-button" type="success" plain>登入</el-button>
        </div>

        <el-divider class="noPw">
          <span style="color: grey; font-size: 12px; " @click="router.push('/register')">沒有帳號</span>
        </el-divider>

        <div class="sub-reg">
          <el-button class="sub-button" type="warning" @click="router.push('/register')" plain>註冊帳號</el-button>
        </div>

      </div>
    </div>

  </div>
</template>

<style scoped>
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

.noPw{

  margin-top: 20px;
  margin-bottom: 20px;
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