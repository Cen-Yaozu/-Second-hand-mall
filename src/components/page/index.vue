<template>
    <div class="page-wrapper">
        <app-head></app-head>
<!--        <app-carousel></app-carousel>-->

        <app-body>
            <div class="home-container">
                <div class="home-header">
                    <h1 class="main-title">淘易阁</h1>
                    <p class="sub-title">发现校园里的闲置宝藏</p>
                </div>

                <el-tabs v-model="labelName" class="category-tabs" @tab-click="handleClick">
                    <el-tab-pane label="全部" name="0"></el-tab-pane>
                    <el-tab-pane label="数码科技" name="1"></el-tab-pane>
                    <el-tab-pane label="生活用品" name="2"></el-tab-pane>
                    <el-tab-pane label="运动相关" name="3"></el-tab-pane>
                    <el-tab-pane label="图书笔记" name="4"></el-tab-pane>
                    <el-tab-pane label="社区帖子" name="5"></el-tab-pane>
<!--                <el-tab-pane label="已被购买商品" name="6"></el-tab-pane>-->
                </el-tabs>

                <div class="product-grid">
                    <el-card 
                        v-for="(idle, index) in idleList" 
                        :key="index" 
                        class="product-card" 
                        shadow="hover"
                        @click.native="toDetails(idle)">
                        <div class="product-image">
                            <el-image
                                :src="getImageUrl(idle.imgUrl)"
                                fit="cover">
                                <div slot="error" class="image-slot">
                                    <i class="el-icon-picture-outline">无图</i>
                                </div>
                            </el-image>
                            <div v-if="idle.idleStatus === 2" class="product-tag sold">已售出</div>
                            <div v-else class="product-tag">出售中</div>
                        </div>
                        <div class="product-info">
                            <h3 class="product-title">{{ idle.idleName }}</h3>
                            <div class="product-meta">
                                <div class="product-price" v-if="idle.idlePrice !== 0">¥{{ idle.idlePrice }}</div>
                                <div class="product-price" v-else>免费</div>
                                <div class="product-location">{{ idle.idlePlace }}</div>
                            </div>
                            <div class="product-time">{{ idle.timeStr }}</div>
                            <div class="product-user">
                                <el-avatar :size="24" :src="getAvatarUrl(idle.user.avatar)"></el-avatar>
                                <span class="user-name">{{ idle.user.nickname }}</span>
                            </div>
                        </div>
                    </el-card>
                </div>

                <div class="pagination-container">
                    <el-pagination
                        background
                        @current-change="handleCurrentChange"
                        :current-page.sync="currentPage"
                        :page-size="8"
                        layout="prev, pager, next, jumper"
                        :total="totalItem">
                    </el-pagination>
                </div>
            </div>
            <app-foot></app-foot>
        </app-body>
    </div>
</template>

<script>
    import AppHead from '../common/AppHeader.vue';
    import AppBody from '../common/AppPageBody.vue'
    import AppFoot from '../common/AppFoot.vue'
    import AppCarousel from '@/components/common/AppCarousel';

    export default {
        name: "index",
        components: {
            AppCarousel,
            AppHead,
            AppBody,
            AppFoot
        },
        data() {
            return {
                labelName: '0',
                idleList: [],
                currentPage: 1,
                totalItem: 1,
            };
        },
        created() {
            this.findIdleTiem(1)
        },
        mounted() {

        },
        watch:{
            $route(to,from){
                this.labelName=to.query.labelName;
                let val=parseInt(to.query.page)?parseInt(to.query.page):1;
                // let totalPage=parseInt(this.totalItem/8)+1;
                // val=parseInt(val%totalPage);
                // val=val===0?totalPage:val;
                this.currentPage=parseInt(to.query.page)?parseInt(to.query.page):1;
                this.findIdleTiem(val);
            }
        },
        methods: {
            findIdleTiem(page){
                const loading = this.$loading({
                    lock: true,
                    text: '加载数据中',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0)'
                });
                if(this.labelName>0){
                    this.$api.findIdleTiemByLable({
                        idleLabel:this.labelName,
                        page: page,
                        nums: 8
                    }).then(res => {
                        console.log('获取分类商品列表:', res);
                        if (res.status_code === 1 && res.data && res.data.list) {
                            let list = res.data.list;
                            for (let i = 0; i < list.length; i++) {
                                list[i].timeStr = list[i].releaseTime.substring(0, 10) + " " + list[i].releaseTime.substring(11, 19);
                                try {
                                    let pictureList = list[i].pictureList;
                                    if (typeof pictureList === 'string') {
                                        try {
                                            // 尝试解析JSON
                                            pictureList = JSON.parse(pictureList);
                                        } catch (e) {
                                            // 如果JSON解析失败，尝试按逗号分割
                                            if (pictureList.includes(',')) {
                                                pictureList = pictureList.split(',');
                                            } else {
                                                pictureList = [pictureList];
                                            }
                                        }
                                    }
                                    list[i].imgUrl = pictureList && pictureList.length > 0 ? pictureList[0].trim() : '';
                                } catch (error) {
                                    console.error('处理图片列表出错:', error, list[i]);
                                    list[i].imgUrl = '';
                                }
                            }
                            this.idleList = list;
                            this.totalItem = res.data.count;
                        } else {
                            this.idleList = [];
                            this.totalItem = 0;
                            console.error('获取商品列表失败:', res);
                        }
                    }).catch(e => {
                        console.error('请求失败:', e);
                        this.$message.error('获取商品列表失败');
                    }).finally(()=>{
                        loading.close();
                    })
                }else{
                    this.$api.findIdleTiem({
                        page: page,
                        nums: 8
                    }).then(res => {
                        console.log('获取全部商品列表:', res);
                        if (res.status_code === 1 && res.data && res.data.list) {
                            let list = res.data.list;
                            for (let i = 0; i < list.length; i++) {
                                list[i].timeStr = list[i].releaseTime.substring(0, 10) + " " + list[i].releaseTime.substring(11, 19);
                                try {
                                    let pictureList = list[i].pictureList;
                                    if (typeof pictureList === 'string') {
                                        try {
                                            // 尝试解析JSON
                                            pictureList = JSON.parse(pictureList);
                                        } catch (e) {
                                            // 如果JSON解析失败，尝试按逗号分割
                                            if (pictureList.includes(',')) {
                                                pictureList = pictureList.split(',');
                                            } else {
                                                pictureList = [pictureList];
                                            }
                                        }
                                    }
                                    list[i].imgUrl = pictureList && pictureList.length > 0 ? pictureList[0].trim() : '';
                                } catch (error) {
                                    console.error('处理图片列表出错:', error, list[i]);
                                    list[i].imgUrl = '';
                                }
                            }
                            this.idleList = list;
                            this.totalItem = res.data.count;
                        } else {
                            this.idleList = [];
                            this.totalItem = 0;
                            console.error('获取商品列表失败:', res);
                        }
                    }).catch(e => {
                        console.error('请求失败:', e);
                        this.$message.error('获取商品列表失败');
                    }).finally(()=>{
                        loading.close();
                    })
                }
            },
            handleClick(tab, event) {
                // console.log(tab,event);
                console.log(this.labelName);
                this.$router.replace({query: {page: 1,labelName:this.labelName}});
            },
            handleCurrentChange(val) {
                console.log(`当前页: ${val}`);
                this.$router.replace({query: {page: val,labelName:this.labelName}});
            },
            toDetails(idle) {
                this.$router.push({path: '/details', query: {id: idle.id}});
            },
            getImageUrl(url) {
                // 检查url是否已经是HTTP URL
                if (url && (url.startsWith('http://') || url.startsWith('https://'))) {
                    return url;
                }
                
                // 检查url是否是一个本地文件路径
                if (url && (url.startsWith('/') || url.startsWith('D:') || url.includes('\\') || url.includes('/'))) {
                    // 如果是本地路径，则转换为服务器URL
                    // 这里假设文件名是路径最后的部分
                    const fileName = url.split(/[/\\]/).pop();
                    // 构建访问URL，端口改为8082
                    return `http://localhost:8082/image?imageName=${fileName}`;
                }
                
                // 如果是简单的文件名，直接构建访问URL
                if (url && url.trim() !== '') {
                    return `http://localhost:8082/image?imageName=${url}`;
                }
                
                // 其他情况直接返回
                return url;
            },
            getAvatarUrl(url) {
                // 检查url是否已经是HTTP URL
                if (url && (url.startsWith('http://') || url.startsWith('https://'))) {
                    // 用本地默认头像替代外部URL
                    return `http://localhost:8082/image?imageName=default_avatar.webp`;
                }
                
                // 如果是简单的文件名，直接构建访问URL
                if (url && url.trim() !== '') {
                    return `http://localhost:8082/image?imageName=${url}`;
                }
                
                // 其他情况使用默认头像
                return `http://localhost:8082/image?imageName=default_avatar.webp`;
            }
        }
    }
</script>

<style scoped>
    .page-wrapper {
        min-height: 100vh;
    }

    .home-container {
        padding: var(--el-spacing-xl);
        min-height: 85vh;
    }

    .home-header {
        margin-bottom: var(--el-spacing-xl);
        text-align: center;
    }

    .main-title {
        font-size: 32px;
        color: var(--el-color-primary);
        margin-bottom: var(--el-spacing-sm);
    }

    .sub-title {
        font-size: var(--el-font-size-medium);
        color: var(--el-text-color-secondary);
    }

    .category-tabs {
        margin-bottom: var(--el-spacing-xl);
    }

    .product-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
        gap: var(--el-spacing-lg);
        margin-bottom: var(--el-spacing-xl);
    }

    .product-card {
        height: 100%;
        cursor: pointer;
        transition: transform 0.3s;
    }

    .product-card:hover {
        transform: translateY(-5px);
    }

    .product-image {
        position: relative;
        height: 200px;
        overflow: hidden;
    }

    .product-image .el-image {
        width: 100%;
        height: 100%;
    }

    .product-tag {
        position: absolute;
        top: 10px;
        right: 10px;
        background-color: var(--el-color-success);
        color: white;
        padding: 2px 8px;
        border-radius: var(--el-border-radius-small);
        font-size: var(--el-font-size-extra-small);
    }

    .product-tag.sold {
        background-color: var(--el-color-info);
    }

    .product-info {
        padding: var(--el-spacing-md);
    }

    .product-title {
        font-size: var(--el-font-size-medium);
        margin-bottom: var(--el-spacing-sm);
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    .product-meta {
        display: flex;
        justify-content: space-between;
        margin-bottom: var(--el-spacing-xs);
    }

    .product-price {
        color: var(--el-color-danger);
        font-weight: 500;
        font-size: var(--el-font-size-medium);
    }

    .product-location {
        color: var(--el-text-color-secondary);
        font-size: var(--el-font-size-small);
    }

    .product-time {
        color: var(--el-text-color-secondary);
        font-size: var(--el-font-size-extra-small);
        margin-bottom: var(--el-spacing-sm);
    }

    .product-user {
        display: flex;
        align-items: center;
    }

    .user-name {
        margin-left: var(--el-spacing-sm);
        font-size: var(--el-font-size-small);
        color: var(--el-text-color-regular);
    }

    .pagination-container {
        display: flex;
        justify-content: center;
        padding: var(--el-spacing-lg) 0;
    }

    img{
        width: 100%;
        height: 100%;
        object-fit: fill;
    }
    /*走马灯 css*/
    .el-carousel__item h3 {
        color: #475669;
        font-size: 14px;
        opacity: 0.75;
        line-height: 200px;
        margin: 0;

    }

    .el-carousel__item:nth-child(2n) {
        background-color: #99a9bf;
    }

    .el-carousel__item:nth-child(2n+1) {
        background-color: #d3dce6;
    }



    .idle-card {
        height: 300px;
        border: #eeeeee solid 1px;
        margin-bottom: 15px;
        cursor: pointer;
    }

    .fenye {
        display: flex;
        justify-content: center;
        height: 60px;
        align-items: center;
    }

    .idle-title {
        font-size: 18px;
        font-weight: 600;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        margin: 10px;
    }

    .idle-prive {
        font-size: 16px;
        color: red;
    }

    .idle-place {
        font-size: 13px;
        color: #666666;
        float: right;
        padding-right: 20px;

    }

    .idle-time {
        color: #666666;
        font-size: 12px;
        margin: 0 10px;
    }

    .user-nickname {
        color: #999999;
        font-size: 12px;
        display: flex;
        align-items: center;
        height: 30px;
        padding-left: 10px;
    }

    .user-info {
        padding: 5px 10px;
        height: 30px;
        display: flex;
    }
</style>
