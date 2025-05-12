<script setup>
import {get,post} from "@/net/index.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const logout=()=>{
  get('/api/auth/logout',(message)=>{// springsecurity中，大多版本疑似僅用post來註銷
    ElMessage.success(message)
    router.push('/')
  })

}
</script>

<template>
  <div>歡迎來到管理平台</div>
  <div>
    <el-button @click="logout()" type="danger" plain>退出登出</el-button>
  </div>

</template>

<style scoped>

</style>