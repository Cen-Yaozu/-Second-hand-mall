<template>
    <div class="header">
        <div class="header-container">
            <div class="app-name">
                <router-link to="/">
                    <img :src="logoPath" class="logo-image"/>
                    <span class="logo-text">校易购</span>
                </router-link>
            </div>
            <div class="search-container">
                <el-input 
                    placeholder="搜闲置..." 
                    v-model="searchValue" 
                    @keyup.enter.native="searchIdle"
                    class="search-input"
                    prefix-icon="el-icon-search">
                    <el-button slot="append" icon="el-icon-search" @click="searchIdle"></el-button>
                </el-input>
            </div>
            <div class="action-buttons">
                <el-button type="primary" icon="el-icon-plus" @click="toRelease">发布闲置/公告</el-button>
                <el-button type="primary" icon="el-icon-chat-dot-round" @click="toMessage">
                    消息
                    <el-badge v-if="unreadMessagesCount > 0" :value="unreadMessagesCount" class="message-badge"></el-badge>
                </el-button>
                <router-link v-if="!isLogin" class="user-name-text" to="/login">登录</router-link>
                <el-dropdown trigger="click" v-else>
                    <div class="user-profile">
                        <div class="user-nickname">{{nickname}}</div>
                        <el-avatar :src="avatar" :size="32"></el-avatar>
                    </div>
                    <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item><div @click="toMe">个人中心</div></el-dropdown-item>
                        <el-dropdown-item divided class="logout-item"><div @click="loginOut">退出登录</div></el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
            </div>
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
        background-color: var(--el-bg-color);
        box-shadow: var(--el-box-shadow-light);
        display: flex;
        justify-content: center;
        z-index: 1000;
        margin: 0;
        border-bottom: 1px solid var(--el-border-color-lighter);
    }

    .header-container {
        width: 100%;
        max-width: 1200px;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 0 var(--el-spacing-lg);
        margin: 0;
    }

    .app-name a {
        display: flex;
        align-items: center;
        color: var(--el-color-primary);
        font-size: var(--el-font-size-large);
        text-decoration: none;
        font-weight: 600;
    }

    .logo-image {
        width: 40px; 
        height: 40px; 
        display: inline-block; 
        vertical-align: middle;
        margin-right: var(--el-spacing-md);
    }

    .logo-text {
        font-size: var(--el-font-size-extra-large);
    }

    .search-container {
        flex: 1;
        max-width: 500px;
        margin: 0 var(--el-spacing-xl);
    }

    .search-input >>> .el-input__inner {
        border-radius: var(--el-border-radius-round);
    }

    .action-buttons {
        display: flex;
        align-items: center;
        gap: var(--el-spacing-md);
    }

    .user-name-text {
        font-size: var(--el-font-size-medium);
        font-weight: 500;
        color: var(--el-color-primary);
        cursor: pointer;
        text-decoration: none;
        margin-left: var(--el-spacing-md);
    }

    .user-profile {
        display: flex;
        align-items: center;
        cursor: pointer;
    }

    .user-nickname {
        font-size: var(--el-font-size-base);
        color: var(--el-color-primary);
        margin-right: var(--el-spacing-sm);
    }

    .message-badge {
        margin-top: -10px;
    }

    .logout-item {
        color: var(--el-color-danger);
    }
</style>
