<script setup>

import router from "@/router/index.js";
import {Lock,Message,EditPen} from "@element-plus/icons-vue";
import {reactive,ref} from "vue";
import {post} from "@/net/index.js";
import {ElMessage} from "element-plus";


const form= reactive({
  email:'',
  code:'',
  password:'',
  password_repeat:'',
})

const active=ref(0)

const formRef = ref()
const isEmailValid = ref(false)
const coldTime=ref(0)


const onValidate=(prop,isValid)=>{
  if(prop==='email'){
    isEmailValid.value=isValid
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


const validateEmail=()=>{
  coldTime.value=60
  post('/api/auth/valid-reset-email',{
    email: form.email
  },(message,status) => {
    console.log('狀態碼', status)
    ElMessage.success(message)
    setInterval(()=>coldTime.value--,1000)
  },(message)=>{
      ElMessage.warning(message)
      coldTime.value=0
  })
}


const startReset=()=>{
    formRef.value.validate((isValid) => {
      if(isValid) {
          post('/api/auth/start-reset', {
              email: form.email,
              code: form.code
           }, () => {
             active.value++
           })
        }else {
          ElMessage.warning('請填寫email和驗證碼!!')
        }
    })
}

const doReset=()=>{
  formRef.value.validate((isValid) => {
      if(isValid) {
        post('/api/auth/do-reset', {
          password: form.password
        }, (message) => {
           ElMessage.success(message)
           router.push('/')
        })
      }else {
        ElMessage.warning('請填寫email和驗證碼!!')
      }
    })

}


const rules={
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

        <div class="step-flow">
          <el-steps :active="active" finish-status="success" align-center>
            <el-step title="驗證e-mail" />
            <el-step title="重新設置密碼" />
          </el-steps>
        </div>
        <div class="title">重置密碼</div>
          <div v-if="active===0">
              <div class="subtitle">請輸入需要重置密碼的帳號的e-mail</div>

              <div class="input-wrapper">

                <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">


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
                          <el-input class="inputCerti" :maxlength="6" v-model="form.code" type="text" placeholder="請輸入驗證碼">
                            <template #prefix>
                              <el-icon><EditPen /></el-icon>
                            </template>
                          </el-input>
                        </el-col>

                        <el-col :span="6">
                          <el-button class="getCerti" type="success" plain @click="validateEmail"  :disabled="!isEmailValid||coldTime>0">
                            {{coldTime>0 ?'請稍後'+coldTime+'秒':'獲得驗證碼' }}</el-button>
                        </el-col>

                      </el-row>

                    </div>

                  </el-form-item>

                </el-form>


                <div class="registerButton"  type="text" placeholder="立即重置密碼">
                  <el-button type="danger" style="width:270px" plain @click="startReset" >立即重置密碼</el-button>
                </div>

                <div class="haveLink">
                  <el-link :underline="false" type="primary" style="translate: 0 -2px" @click="router.push('/')">返回登入頁面</el-link>
                </div>




              </div>

              </div>
          <div v-if="active===1">
            <div class="subtitle">請填寫新的密碼</div>
            <div class="input-wrapper">

              <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">


                <el-form-item prop="password">
                  <el-input  v-model="form.password" :maxlength="16" class="pw"  type="password" placeholder="密碼">
                    <template #prefix>
                      <el-icon><Lock /></el-icon>
                    </template>
                  </el-input>

                </el-form-item>

                <el-form-item prop="password_repeat">
                  <el-input  v-model="form.password_repeat" :maxlength="16" class="pw"  type="password" placeholder="重複輸入密碼">
                    <template #prefix>
                      <el-icon><Lock /></el-icon>
                    </template>
                  </el-input>

                </el-form-item>


              </el-form>


              <div class="registerButton"  type="text" placeholder="立即重置密碼">
                <el-button type="danger" style="width:270px" plain @click="doReset" >立即重置密碼</el-button>
              </div>

              <div class="haveLink">
                <el-link :underline="false" type="primary" style="translate: 0 -2px" @click="router.push('/')">返回登入頁面</el-link>
              </div>




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

.step-flow{
  margin-top: -30px;
  margin-bottom: 60px;
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