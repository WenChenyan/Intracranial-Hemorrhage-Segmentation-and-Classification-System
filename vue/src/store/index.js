import Vue from 'vue'
import Vuex from 'vuex'
import router, {resetRouter} from "@/router";

Vue.use(Vuex)

const store = new Vuex.Store({
    state: {
        currentPathName: '',
        currentPath: '',
    },
    mutations: {
        setPath (state) {
            state.currentPathName = sessionStorage.getItem("currentPathName")
        },
        logout() {
            // 清空缓存
            sessionStorage.removeItem("user")
            sessionStorage.removeItem("menus")
            sessionStorage.removeItem("role")
            router.push("/login")

            // 重置路由
            resetRouter()
        }
    }
})

export default store
