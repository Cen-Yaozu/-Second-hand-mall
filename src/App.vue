<template>
    <div id="app">
        <router-view></router-view>
    </div>
</template>
<style>
    html, body, #app{
        overflow: visible;
        width: 100%;
        height: 100%;
        margin: 0;
        padding: 0;
        font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", Arial, sans-serif;
    }
    
    #app {
        min-height: 100vh;
        background-color: #BCDDDB;
    }
</style>
