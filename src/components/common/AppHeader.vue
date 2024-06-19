<template>
    <div class="header">
        <div class="header-container">
            <div class="app-name">
                <router-link to="/">
                    <img :src="logoPath" style='width: 60px; height: 60px; display: inline-block; vertical-align: middle;
                                margin-right: 20px'/>
                </router-link>

            </div>
            <div class="search-container">
                <!-- 按回车触发函数  @keyup.enter.native -->
                <el-input placeholder="搜闲置..." v-model="searchValue" @keyup.enter.native="searchIdle">
                    <el-button slot="append" icon="el-icon-search" @click="searchIdle"></el-button>
                </el-input>
            </div>
            <el-button  type="primary" icon="el-icon-plus"  @click="toRelease">发布闲置/公告</el-button>
            <el-button type="primary" icon="el-icon-chat-dot-round" @click="toMessage">消息</el-button>
<!--            <el-button type="primary" icon="el-icon-shopping-cart-1" @click="toShopCart">我的收藏</el-button>-->
            <router-link v-if="!isLogin" class="user-name-text" to="/login">登录</router-link>
            <el-dropdown trigger="click" v-else>
                <div style="cursor:pointer;display: flex;align-items: center; margin-left: 20px">
                    <div style="font-size: 16px;color: #409EFF;padding-right: 5px;">{{nickname}}</div>
                    <el-avatar :src="avatar"></el-avatar>
                </div>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item><div @click="toMe">个人中心</div></el-dropdown-item>
                    <el-dropdown-item divided style="color: red;"><div @click="loginOut">退出登录</div></el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </div>
    </div>
</template>
<script>

    import Mixin from '@/utils/mixin';
    export default {
        mixins: [Mixin],
        name: 'Header',
        props: ['searchInput','nicknameValue','avatarValue'],
        data() {
            return {
                logoPath: "/logo.png",
                searchValue: this.searchInput,
                nickname:'登录',
                avatar:'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
                isLogin:false,
                unreadMessagesCount:3
            };
        },
        created(){
            // console.log("header");
            if(! this.$globalData.userInfo.nickname){
                this.$api.getUserInfo().then(res=>{
                    console.log('Header getUserInfo:',res);
                    if(res.status_code===1){
                        this.nickname=res.data.nickname;
                        this.avatar=res.data.avatar;
                        res.data.signInTime=res.data.signInTime.substring(0,10);
                        this.$globalData.userInfo=res.data;
                        this.isLogin=true;
                    }
                })
            }else {
                this.nickname=this.$globalData.userInfo.nickname;
                this.avatar=this.$globalData.userInfo.avatar;
                this.isLogin=true;
            }
        },
        methods: {
            searchIdle() {
                if ('/search' !== this.$route.path) {
                    this.$router.push({path: '/search', query: {searchValue: this.searchValue}});
                } else {
                    this.$router.replace({path: '/search', query: {searchValue: this.searchValue}});
                    this.$router.go(0);
                }

            },
            toMe() {
                if ('/me' !== this.$route.path) {
                    this.$router.push({path: '/me'});
                }
            },
            toMessage(){
                if ('/message' !== this.$route.path) {
                    this.$router.push({path: '/message'});
                }
            },
            toRelease(){
                if ('/release' !== this.$route.path) {
                    this.$router.push({path: '/release'});
                }
            },
            // toShopCart(){
            //     if ('/shopCart' !== this.$route.path) {
            //         this.$router.push({path: '/shopCart'});
            //     }
            // },
            /*这里的logout 只是用前端来删除掉浏览器中之前登录过的用户信息，并没有发送请求*/
            loginOut(){
                this.$api.logout().then(res=>{
                    if(res.status_code===1){
                        this.$globalData.userInfo={};
                        console.log("login out");
                        if ('/index' === this.$route.path) {
                            this.$router.go(0);
                        }else {
                            this.$router.push({path: '/index'});
                        }
                    }else {
                        this.$message.error('网络或系统异常，退出登录失败！');
                    }
                });

            }
        }
    };
</script>
<style scoped>
    .header {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        width: 100%;
        height: 60px;
        background: #BCDDDB;
        display: flex;
        justify-content: center;
        border-bottom: #eeeeee solid ;
        z-index: 1000;
        margin: 0;
    }

    .header-container {
        width: auto;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin: 0;
    }

    .app-name a {
        color: #409EFF;
        font-size: 24px;
        text-decoration: none;
    }

    .search-container {
        width: 700px;
    }
    .user-name-text{
        font-size: 20px;
        font-weight: 600;
        color: #409EFF;
        cursor: pointer;
        text-decoration: none;
        left: 20px;
        position: relative;
    }
    .message-badge {
        position: relative;
        top: -5px; /* 调整红点相对于按钮的位置 */
        left: 5px; /* 调整红点的水平位置 */
        display: inline-block;
        width: 20px;
        height: 20px;
        border-radius: 50%;
        background-color: red;
        color: white;
        text-align: center;
        line-height: 20px; /* 使数字垂直居中 */
        font-size: 9px;
        font-weight: bold;
    }

</style>
