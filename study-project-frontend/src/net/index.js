import axios from "axios";
import {ElMessage} from "element-plus";

const defaultError=()=>ElMessage.error("發生錯誤，請聯繫管理員")
const defaultFailure=(message)=>ElMessage.warning(message)

function post(url,data,success, failure=defaultFailure, error=defaultError){
    axios.post(url,data,{
       headers:{
           'Content-Type':'application/x-www-form-urlencoded'
       },
        withCredentials:true// 發起請求，是否攜帶cookie

    }).then(({data})=>{
        if(data.success){
            success(data.message,data.status)
        }else {
            failure(data.message,data.status)
        }

    }).catch(error)

}

function get(url,success, failure=defaultFailure, error=defaultError){
    axios.get(url,{

        withCredentials:true// 發起請求，是否攜帶cookie

    }).then( ({data})=>{
        if(data.success){
            success(data.message,data.status)
        }else {
            failure(data.message,data.status)
        }

    }).catch(error)

}

export {get,post}