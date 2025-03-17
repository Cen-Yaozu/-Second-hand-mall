<template>
    <div class="carousel-container">
        <div class="carousel">
            <el-carousel height="200px" :interval="4000" arrow="always">
                <el-carousel-item v-for="(url, index) in urlList" :key="index">
                    <div class="carousel-image">
                        <img :src="url" alt="carousel image" />
                    </div>
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
                "http://toutiao.io/assets/reading-9c322ab470b1d7c38c0e17ea24e5ecb7.png",
                "http://toutiao.io/assets/reading-9c322ab470b1d7c38c0e17ea24e5ecb7.png",
                "http://toutiao.io/assets/reading-9c322ab470b1d7c38c0e17ea24e5ecb7.png",
            ]
        }
    }
};
</script>

<style scoped>
.carousel-container {
    width: 100%;
}

.carousel {
    margin-bottom: 10px;
}

.carousel-image {
    width: 100%;
    height: 100%;
}

.carousel-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}
</style>
