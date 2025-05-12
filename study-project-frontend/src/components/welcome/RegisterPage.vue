<script setup>

import {Lock, User,Message,EditPen} from "@element-plus/icons-vue";
import router from "@/router/index.js";
import {reactive,ref} from "vue";
import {ElMessage} from "element-plus";
import {post} from "@/net/index.js";




const form= reactive({
  username:'',
  password:'',
  password_repeat:'',
  email:'',
  code:''
})



const validateUsername= (rule, value, callback) => {
  if(value === '') {
    callback(new Error('請輸入帳號名稱'))
  }else if(!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
    callback(new Error('帳號名稱不能包含特殊符號，只能是中文或英文'))
  }else{
    callback()
  }
}

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('請再次輸入密碼'))
  } else if (value !== form.password) {
    callback(new Error("兩次輸入密碼不一致"))
  } else {
    callback()
  }
}

const formRef = ref()
const isEmailValid = ref(false)

const onValidate=(prop,isValid)=>{
  if(prop==='email'){
    isEmailValid.value=isValid
  }
}

const register=()=>{
  formRef.value.validate((isValid) => {
    if(isValid) {

    }else {
      ElMessage.warning('請完整填寫內容!!')
    }
  })
}

const validateEmail=()=>{
  post('/api/auth/valid-email',{
    email: form.email
  },(message,status) => {
    console.log('狀態碼', status)
    ElMessage.success(message)
  })

}

const rules={
    username:[

        { validator:validateUsername, trigger: ['blur','change'] },
        {min: 2, max: 8, message: '帳號名稱長度必須在2~8之間',trigger: ['blur','change'] },

    ],
    password:[
        {required:true,message: '請輸入密碼',trigger: 'blur'},
        {min: 6, max: 16,message:'密碼長度必須在6~16之間',trigger: ['blur','change']}
    ],
    password_repeat:[
        { validator:validatePassword, trigger: ['blur','change'] },
    ],
    email:[
        {required: true, message: '請輸入e-mail', trigger: 'blur',},
        { type: 'email', message: '請輸入正確e-mail', trigger: ['blur', 'change'],},
    ],
    code:[
      {requested:true, message:'請輸入獲得的驗證碼', trigger: 'blur',}
    ]


}



</script>

<template>

  <div class="login-wrapper">
    <div class="bg-image"></div>

    <div class="login-panel">
      <div class="content">
        <div class="title">註冊新帳號</div>
        <div class="subtitle">歡迎註冊，請於下方填寫註冊資料</div>

        <div class="input-wrapper">

          <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">

            <el-form-item prop="username">
              <el-input  v-model="form.username" class="pw"  type="text" placeholder="帳號名稱">
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="password">
              <el-input  v-model="form.password" class="pw"  type="password" placeholder="密碼">
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>

            </el-form-item>

            <el-form-item prop="password_repeat">
              <el-input  v-model="form.password_repeat" class="pw"  type="password" placeholder="重複輸入密碼">
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>

            </el-form-item>

            <el-form-item prop="email">
              <el-input  v-model="form.email" class="pw" type="text" placeholder="e-mail">
                <template #prefix>
                  <el-icon><Message /></el-icon>
                </template>
              </el-input>

            </el-form-item>

            <el-form-item prop="code">
              <div class="certiButton">
                <el-row :gutter="10">

                  <el-col :span="18">
                    <el-input class="inputCerti" v-model="form.code" type="text" placeholder="請輸入驗證碼">
                      <template #prefix>
                        <el-icon><EditPen /></el-icon>
                      </template>
                    </el-input>
                  </el-col>

                  <el-col :span="6">
                    <el-button class="getCerti" type="success" plain @click="validateEmail"  :disabled="!isEmailValid">獲得驗證碼</el-button>
                  </el-col>

                </el-row>

              </div>

            </el-form-item>

          </el-form>


          <div class="registerButton"  type="text" placeholder="立即註冊">
            <el-button type="warning" style="width:270px" plain @click="register">立即註冊</el-button>
          </div>

          <div class="haveLink">
            <span >已有帳號？</span>
            <el-link :underline="false" type="primary" style="translate: 0 -2px" @click="router.push('/')">立即登入</el-link>
          </div>




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
  margin-top: 30px;
  .pw{
    margin-top:20px;
  }
}

.certiButton{
  margin-top:20px;

  .inputCerti{
    margin-top: 0px;
  }

  .getCerti{
    width:100px;
  }
}


.registerButton{
  margin-top:40px;
}

.haveLink{

  margin-top:20px;
  span{

    color:gray;
    font-size: 14px;
    line-height: 15px;

  }
}
</style>