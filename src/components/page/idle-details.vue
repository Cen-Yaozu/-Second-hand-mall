<template>
    <div>
        <app-head></app-head>
        <app-body>
            <div class="idle-details-container">
                <div class="details-header">
                    <div class="details-header-user-info">
                        <el-image
                                style="width: 80px; height: 80px;border-radius: 5px;"
                                :src="idleItemInfo.user.avatar"
                                fit="contain"></el-image>
                        <div style="margin-left: 10px;">
                            <div class="details-header-user-info-nickname">{{idleItemInfo.user.nickname}}</div>
                            <div class="details-header-user-info-time">{{idleItemInfo.user.signInTime.substring(0,10)}} 加入平台</div>
                        </div>
                    </div>
                    <div class="details-header-buy" :style="'width:'+(isMaster?'250px;':'280px;')">
                        <div v-show="idleItemInfo.idlePrice !== 0" style="color: red;font-size: 18px;font-weight: 600;">￥{{idleItemInfo.idlePrice}}</div>
                        <div v-if="!isMaster&&idleItemInfo.idleStatus!==1" style="color: red;font-size: 16px;">闲置已下架或删除</div>
                        <el-button v-show="idleItemInfo.idlePrice > 0" v-if="!isMaster&&idleItemInfo.idleStatus===1" type="danger" plain @click="buyButton(idleItemInfo)">立即购买</el-button>
                        <el-button v-show="idleItemInfo.idlePrice > 0" v-if="!isMaster&&idleItemInfo.idleStatus===1" type="primary" plain @click="favoriteButton(idleItemInfo)">{{isFavorite?'取消收藏':'加入收藏'}}</el-button>
                        <el-button
                            v-show='idleItemInfo.idlePrice >= 0'
                            v-if="isMaster&&idleItemInfo.idleStatus===1"
                            type='primary'
                            plain
                            @click="isEditing ? saveChanges(idleItemInfo) : editButton()">{{ buttonText }}</el-button>
                        <el-button v-if="isMaster&&idleItemInfo.idleStatus===1" type="danger" @click="changeStatus(idleItemInfo,2)" plain>下架</el-button>
                        <el-button v-if="isMaster&&idleItemInfo.idleStatus===2" type="primary" @click="changeStatus(idleItemInfo,1)" plain>重新上架</el-button>
                    </div>
                </div>


                <div class="details-info">

                    <div v-if="isEditing">
                        <label class="input-label">价格</label>
                        <el-input v-model="idleItemInfo.idlePrice" placeholder="请输入商品价格"></el-input>
                    </div>
                    <div class="details-info-title" v-if="isEditing">
                        <label class="input-label">名称</label>
                        <el-input v-model="idleItemInfo.idleName" placeholder="请输入商品名称"></el-input>
                    </div>
                    <div class="details-info-title" v-else>
                        {{idleItemInfo.idleName}}
                    </div>
                    <div v-if="isEditing">
                        <label class="input-label">详情</label>
                        <el-input type="textarea" autosize placeholder="请输入商品详情..." v-model="idleItemInfo.idleDetails"></el-input>
                    </div>
                    <div class="details-info-main" v-else v-html="idleItemInfo.idleDetails"></div>
                    <div class="details-picture">
                        <template v-if="isEditing">
                            <!-- 编辑模式下的上传控件 -->
                            <el-upload
                                :on-remove="fileHandleRemove"
                                :on-success="fileHandleSuccess"
                                :on-change="imgChange"
                                action="http://localhost:8082/file"
                                list-type="picture-card"
                                :file-list="fileList"
                                :before-upload="beforeUpload"
                            >
                                <i class="el-icon-plus"></i>
                            </el-upload>
                        </template>
                        <template v-else>
                            <!-- 显示模式下的图片列表 -->
                            <div v-loading="imageLoading" class="image-container">
                                <el-image
                                    v-for="(imgUrl, i) in processedPictureList"
                                    :key="i"
                                    style="width: 90%; margin-bottom: 2px;"
                                    :src="getImageUrl(imgUrl)"
                                    fit="contain"
                                    @load="handleImageLoad"
                                    @error="handleImageError"
                                >
                                    <div slot="error" class="image-slot">
                                        <i class="el-icon-picture-outline"></i>
                                        <p>图片加载失败</p>
                                    </div>
                                </el-image>
                            </div>
                            <div v-if="!processedPictureList.length" class="no-image">
                                <i class="el-icon-picture-outline"></i>
                                <p>暂无图片</p>
                            </div>
                        </template>
                    </div>
                </div>

                <div class="message-container" id="replyMessageLocation">
                    <div class="message-title">全部留言</div>
                    <div class="message-send">
                        <div v-if="isReply" style="padding-bottom: 10px;">
                            <el-button type="info" @click="cancelReply">回复：{{replyData.toMessage}} @{{replyData.toUserNickname}} <i class="el-icon-close el-icon--right"></i></el-button>
                        </div>
                        <el-input
                                type="textarea"
                                autosize
                                placeholder="留言提问..."
                                v-model="messageContent"
                                maxlength="200"
                                show-word-limit>
                        </el-input>
                        <div class="message-send-button">
                            <el-button plain @click="sendMessage">发送留言</el-button>
                        </div>
                    </div>
                    <div>
                        <div v-for="(mes,index) in messageList" class="message-container-list">
                            <div class="message-container-list-left">
                                <el-image
                                        style="width: 55px; height: 55px;border-radius: 5px;"
                                        :src="mes.fromU.avatar"
                                        fit="contain"></el-image>
                                <div class="message-container-list-text">
                                    <div class="message-nickname">{{mes.fromU.nickname}}
                                        {{mes.toU.nickname?' @'+mes.toU.nickname+'：'+
                                        mes.toM.content.substring(0,10)+
                                        (mes.toM.content.length>10?'...':''):''}}</div>
                                    <div class="message-content" v-html="mes.content"></div>
                                    <div class="message-time">{{mes.createTime}}</div>
                                </div>
                            </div>
                            <div class="message-container-list-right">
                                <el-button style="float: right;"  plain @click="replyMessage(index)">回复</el-button>
                            </div>
                        </div>
                    </div>
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

    export default {
        name: "idle-details",
        components: {
            AppHead,
            AppBody,
            AppFoot
        },
        data() {
            return {
                buttonText:'编辑',
                isEditing:false,
                messageContent:'',
                toUser:null,
                toMessage:null,
                isReply:false,
                replyData:{
                    toUserNickname:'',
                    toMessage:''
                },
                messageList:[],
                idleItemInfo:{
                    id:'',
                    idleName:'',
                    idleDetails:'',
                    pictureList:[],
                    idlePrice:0,
                    idlePlace:'',
                    idleLabel:'',
                    idleStatus:-1,
                    userId:'',
                    user:{
                        avatar:'',
                        nickname:'',
                        signInTime:''
                    },
                },
                editingIdleItem:null,
                isMaster:false,
                isFavorite:true,
                favoriteId:0,
                imageLoading: true,
                loadedImages: 0,
                totalImages: 0
            };
        },
        computed: {
            processedPictureList() {
                if (!this.idleItemInfo.pictureList) return [];
                if (typeof this.idleItemInfo.pictureList === 'string') {
                    try {
                        return JSON.parse(this.idleItemInfo.pictureList);
                    } catch (e) {
                        console.error('解析图片列表失败:', e);
                        return [];
                    }
                }
                return Array.isArray(this.idleItemInfo.pictureList) 
                    ? this.idleItemInfo.pictureList 
                    : [this.idleItemInfo.pictureList];
            },
            fileList() {
                return this.processedPictureList.map(img => ({
                    name: img,
                    url: this.getImageUrl(img)
                }));
            }
        },
        created(){
            this.getMyIdleById();
        },
        methods: {
            getMyIdleById(){
                this.imageLoading = true;
                let id = this.$route.query.id;
                this.$api.getIdleItem({
                    id: id
                }).then(res => {
                    console.log('商品详情原始数据:', res);
                    if (res.data) {
                        // 处理商品详情的换行
                        let list = res.data.idleDetails.split(/\r?\n/);
                        let str = '';
                        for (let i = 0; i < list.length; i++) {
                            str += list[i];
                        }
                        res.data.idleDetails = str;

                        // 处理图片列表
                        try {
                            if (typeof res.data.pictureList === 'string') {
                                const pictureList = JSON.parse(res.data.pictureList);
                                res.data.pictureList = Array.isArray(pictureList) ? pictureList : [pictureList];
                            }
                            console.log('处理后的图片列表:', res.data.pictureList);
                        } catch (e) {
                            console.error('解析图片列表失败:', e);
                            res.data.pictureList = [];
                        }

                        this.idleItemInfo = res.data;
                        this.totalImages = this.processedPictureList.length;
                        this.loadedImages = 0;

                        // 预加载图片
                        this.processedPictureList.forEach(imgUrl => {
                            const img = new Image();
                            img.onload = () => {
                                this.loadedImages++;
                                if (this.loadedImages >= this.totalImages) {
                                    this.imageLoading = false;
                                }
                            };
                            img.onerror = (e) => {
                                console.error('图片预加载失败:', imgUrl, e);
                                this.loadedImages++;
                                if (this.loadedImages >= this.totalImages) {
                                    this.imageLoading = false;
                                }
                            };
                            img.src = this.getImageUrl(imgUrl);
                        });

                        let userId = this.getCookie('shUserId');
                        if (userId == this.idleItemInfo.userId) {
                            this.isMaster = true;
                        }
                        this.checkFavorite();
                        this.checkMyIdle();
                        this.getAllIdleMessage();
                    }
                }).catch(error => {
                    console.error('获取商品详情失败:', error);
                    this.$message.error('获取商品详情失败，请重试');
                    this.imageLoading = false;
                });
            },
            saveChanges(idleItemInfo) {
                const updatedInfo = {
                    ...idleItemInfo,
                    pictureList: JSON.stringify(this.idleItemInfo.pictureList),
                };
                // 这里添加调用API更新商品信息的逻辑
                this.$api.updateIdleItem(updatedInfo).then(() => {
                    // this.getMyIdleById();
                    this.$message.success('商品信息更新成功');
                    this.isEditing = false; // 保存后关闭编辑模式
                    this.buttonText = '编辑商品'
                }).catch(() => {
                    this.$message.error('更新失败，请重试');
                });
            },
            imgChange(file, fileList){
                this.noneBtnImg = fileList.length >= this.limitCountImg;
            },
            fileHandleRemove(file,fileList) {
                console.log(file, fileList);
                // 修改：现在需要找到对应的本地路径
                const urlToRemove = file.localPath || file.url; 
                // 在pictureList中查找对应项
                const index = this.idleItemInfo.pictureList.indexOf(urlToRemove);
                console.log(this)

                if (index > -1) {
                    // 找到了匹配的项，从idleItemInfo.pictureList中移除
                    this.idleItemInfo.pictureList.splice(index, 1);
                    this.$forceUpdate();
                } else {
                    console.warn('Path not found in the list to remove.');
                }
            },
            fileHandlePreview(file) {
                console.log(file);
                // 修改：如果response.data是对象，则使用accessUrl
                if(file.response && file.response.data) {
                    const fileData = file.response.data;
                    this.dialogImageUrl = fileData.accessUrl || fileData;
                } else {
                    this.dialogImageUrl = file.url;
                }
                this.imgDialogVisible=true;
            },
            fileHandleSuccess(response, file, fileList) {
                console.log("file:", response, file, fileList);
                // 修改：现在response.data是一个包含localPath和accessUrl的对象
                const fileData = response.data;
                // 将本地路径添加到pictureList
                this.idleItemInfo.pictureList.push(fileData.localPath);
                // 但在界面展示时使用accessUrl
                file.url = fileData.accessUrl;
                file.localPath = fileData.localPath; // 保存本地路径便于后续操作
            },
            editButton(){
                this.isEditing = !this.isEditing;
                this.buttonText = '保存修改'
            },
            getAllIdleMessage(){
                this.$api.getAllIdleMessage({
                    idleId:this.idleItemInfo.id
                }).then(res=>{
                    console.log('getAllIdleMessage',res.data);
                    if(res.status_code===1){
                        this.messageList=res.data;
                    }
                }).catch(()=>{
                })
            },
            checkFavorite(){
                this.$api.checkFavorite({
                    idleId:this.idleItemInfo.id
                }).then(res=>{
                    if(!res.data){
                        this.isFavorite=false;
                    }else {
                        this.favoriteId=res.data;
                    }
                })
            },
            checkMyIdle(){
                this.$api.checkMyIdle({
                    idleId:this.idleItemInfo.id
                }).then(res=>{
                    if (res.data){
                        this.isMaster = true;
                    }else {
                        this.isMaster = false;
                    }
                })
            },
            getCookie(cname){
                var name = cname + "=";
                var ca = document.cookie.split(';');
                for(var i=0; i<ca.length; i++)
                {
                    var c = ca[i].trim();
                    if (c.indexOf(name)===0) return c.substring(name.length,c.length);
                }
                return "";
            },
            replyMessage(index){
                $('html,body').animate({
                    scrollTop: $("#replyMessageLocation").offset().top-600
                }, {duration: 500, easing: "swing"});
                this.isReply=true;
                this.replyData.toUserNickname=this.messageList[index].fromU.nickname;
                this.replyData.toMessage=this.messageList[index].content.substring(0,10)+(this.messageList[index].content.length>10?'...':'');
                this.toUser=this.messageList[index].userId;
                this.toMessage=this.messageList[index].id;
            },
            changeStatus(idle,status){
                this.$api.updateIdleItem({
                    id:idle.id,
                    idleStatus:status
                }).then(res=>{
                    console.log(res);
                    if(res.status_code===1){
                        this.idleItemInfo.idleStatus=status;
                    }else {
                        this.$message.error(res.msg)
                    }
                });
            },
            buyButton(idleItemInfo){
                this.$api.addOrder({
                    idleId:idleItemInfo.id,
                    orderPrice:idleItemInfo.idlePrice,
                }).then(res=>{
                    console.log(res);
                    if(res.status_code===1){
                        this.$router.push({path: '/order', query: {id: res.data.id}});
                    }else {
                        this.$message.error(res.msg)
                    }
                }).catch(e=>{

                })
            },
            favoriteButton(idleItemInfo){
                if(this.isFavorite){
                    this.$api.deleteFavorite({
                        id: this.favoriteId
                    }).then(res=>{
                        console.log(res);
                        if(res.status_code===1){
                            this.$message({
                                message: '已取消收藏！',
                                type: 'success'
                            });
                            this.isFavorite=false;
                        }else {
                            this.$message.error(res.msg)
                        }
                    }).catch(e=>{
                    })
                }else {
                    this.$api.addFavorite({
                        idleId:idleItemInfo.id
                    }).then(res=>{
                        console.log(res);
                        if(res.status_code===1){
                            this.$message({
                                message: '已加入收藏！',
                                type: 'success'
                            });
                            this.isFavorite=true;
                            this.favoriteId=res.data;
                        }else {
                            this.$message.error(res.msg)
                        }
                    }).catch(e=>{
                    })
                }
            },
            cancelReply(){
                this.isReply=false;
                this.toUser=this.idleItemInfo.userId;
                this.toMessage=null;
                this.replyData.toUserNickname='';
                this.replyData.toMessage='';
            },

            sendMessage(){
                let content=this.messageContent.trim();
                if(this.toUser==null){
                    this.toUser=this.idleItemInfo.userId;
                }
                if(content){
                    let contentList=content.split(/\r?\n/);
                    let contenHtml=contentList[0];
                    for(let i=1;i<contentList.length;i++){
                        contenHtml+='<br>'+contentList[i];
                    }
                    this.$api.sendMessage({
                        idleId:this.idleItemInfo.id,
                        content:contenHtml,
                        toUser:this.toUser,
                        toMessage:this.toMessage
                    }).then(res=>{
                        if(res.status_code===1){
                            this.$message({
                                message: '留言成功！',
                                type: 'success'
                            });
                            this.messageContent='';
                            this.cancelReply();
                            this.getAllIdleMessage();
                        }else {
                            this.$message.error("留言失败！"+res.msg);
                        }
                    }).catch(()=>{
                        this.$message.error("留言失败！");
                    });

                }else{
                    this.$message.error("留言为空！");
                }
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
                    // 构建访问URL
                    return `http://localhost:8082/image?imageName=${fileName}`;
                }
                
                // 如果是简单的文件名，直接构建访问URL
                if (url && url.trim() !== '') {
                    return `http://localhost:8082/image?imageName=${url}`;
                }
                
                // 其他情况直接返回
                return url;
            },
            handleImageLoad() {
                this.loadedImages++;
                if (this.loadedImages >= this.totalImages) {
                    this.imageLoading = false;
                }
            },
            handleImageError(e) {
                console.error('图片加载失败:', e);
                this.loadedImages++;
                if (this.loadedImages >= this.totalImages) {
                    this.imageLoading = false;
                }
            },
            beforeUpload(file) {
                const isImage = file.type.startsWith('image/');
                const isLt2M = file.size / 1024 / 1024 < 2;

                if (!isImage) {
                    this.$message.error('只能上传图片文件!');
                    return false;
                }
                if (!isLt2M) {
                    this.$message.error('图片大小不能超过 2MB!');
                    return false;
                }
                return true;
            },
        },
    }
</script>

<style scoped>
    .idle-details-container {
        min-height: 85vh;
    }

    .details-header {
        height: 80px;
        border-bottom: 10px solid #f6f6f6;
        display: flex;
        justify-content: space-between;
        padding: 20px;
        align-items: center;
    }

    .details-header-user-info {
        display: flex;
    }

    .details-header-user-info-nickname {
        font-weight: 600;
        font-size: 18px;
        margin-bottom: 10px;
    }

    .details-header-user-info-time {
        font-size: 12px;
        color: #555555;
    }

    .details-header-buy {
        display: flex;
        align-items: center;
        justify-content: space-between;
        height: 50px;
        width: 280px;
    }

    .details-info {
        padding: 20px 50px;
    }

    .details-info-title {
        font-size: 22px;
        font-weight: 600;
        margin-bottom: 20px;

    }

    .details-info-main {
        font-size: 17px;
        color: #121212;
        line-height: 160%;
    }

    .details-picture {
        margin: 20px 0;
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    .message-container {
        min-height: 100px;
        border-top: 10px solid #f6f6f6;
        padding: 20px;
    }

    .message-title {
        font-size: 20px;
        font-weight: 600;
        margin-bottom: 20px;
    }
    .message-send{
        min-height: 60px;
    }
    .message-send-button{
        margin-top: 10px;
        display: flex;
        justify-content: flex-end;
    }
    .message-container-list{
        min-height: 60px;
        border-top: 1px solid #eeeeee;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 15px 0;
    }
    .message-container-list:first-child{
        border-top:none;
    }
    .message-container-list-left{
        width: 850px;
        display: flex;
    }
    .message-container-list-right{
        width: 100px;
    }
    .message-container-list-text{
        margin-left: 10px;
    }
    .message-nickname{
        font-weight: 600;
        font-size: 18px;
        padding-bottom: 5px;
    }
    .message-content{
        font-size: 16px;
        padding-bottom: 15px;
        color: #555555;
        width: 770px;
    }
    .message-time{
        font-size: 13px;
        color: #555555;
    }
    .image-container {
        width: 100%;
        min-height: 200px;
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    .no-image {
        text-align: center;
        padding: 40px;
        color: #909399;
    }

    .no-image i {
        font-size: 48px;
        margin-bottom: 10px;
    }

    .image-slot {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        width: 100%;
        height: 100%;
        background: #f5f7fa;
        color: #909399;
    }

    .image-slot i {
        font-size: 24px;
        margin-bottom: 10px;
    }
</style>
