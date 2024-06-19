<template>
    <div class="carousel">
        <div class="carousel-container">
    <el-carousel  direction="vertical" :autoplay="true">
        <el-carousel-item v-for="item in urlList" :key="item.url">
            <h3 class="medium"><img :src="item.url" alt=''/></h3>
        </el-carousel-item>
    </el-carousel>
        </div>
    </div>
</template>

<script>
export default {
    name: 'AppCarousel',
    data(){
        return{
            urlList: [
                {url:"http://5b0988e595225.cdn.sohucs.com/images/20181023/bff9680896c44ccea379162c9eec1483.jpeg"},
                {url:'https://c01.gaitubao.net/FqEn34L2gM8GooPFyG1JaK8dBnho.jpg'},
                {url:'https://pics7.baidu.com/feed/9345d688d43f8794fe8261f36713abfe1ad53a84.jpeg'}
            ]
        }

    }

};
</script>

<style scoped>
.carousel {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    width: 100%;
    height: 7%;
    background: #ffffff;
    display: flex;
    justify-content: center;
    border-bottom: #eeeeee solid ;
    z-index: 1000;
    margin: 0;
}

.carousel-container {
    width: auto;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin: 0;
}
</style>
