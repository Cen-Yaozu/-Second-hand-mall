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
                    <div class="details-header-buy">
                        <div v-show="idleItemInfo.idlePrice !== 0" style="color: red;font-size: 18px;font-weight: 600; margin-right: 10px;">￥{{idleItemInfo.idlePrice}}</div>
                        <div v-if="!isMaster&&idleItemInfo.idleStatus!==1" style="color: red;font-size: 16px;">闲置已下架或删除</div>
                        <el-button v-show="idleItemInfo.idlePrice > 0" v-if="!isMaster&&idleItemInfo.idleStatus===1" type="danger" plain @click="buyButton(idleItemInfo)">立即购买</el-button>
                        <el-button v-show="idleItemInfo.idlePrice > 0" v-if="!isMaster&&idleItemInfo.idleStatus===1" type="primary" plain @click="favoriteButton(idleItemInfo)">{{isFavorite?'取消收藏':'加入收藏'}}</el-button>
                        <el-button v-if="!isMaster&&idleItemInfo.idleStatus===1" type="warning" plain @click="exchangeButton(idleItemInfo)">发起易物交换</el-button>
                        <el-button v-if="!isMaster" type="info" plain @click="reportButton(idleItemInfo)">举报</el-button>
                        <el-button
                            v-show='idleItemInfo.idlePrice >= 0'
                            v-if="isMaster&&idleItemInfo.idleStatus===1"
                            type='primary'
                            plain
                            @click="isEditing ? saveChanges(idleItemInfo) : editButton()">{{ buttonText }}</el-button>
                        <el-button v-if="isMaster&&idleItemInfo.idleStatus===1" type="danger" @click="changeStatus(idleItemInfo,2)" plain>下架</el-button>
                        <el-button v-if="isMaster&&idleItemInfo.idleStatus===2" type="primary" @click="changeStatus(idleItemInfo,1)" plain>重新上架</el-button>
                        <el-button v-if="isMaster&&idleItemInfo.idleStatus===1" type="info" plain @click="viewReceivedRequests">查看交换请求</el-button>
                        <el-button v-if="isMaster&&idleItemInfo.idleStatus===1" type="success" plain @click="donationButton(idleItemInfo)">爱心捐赠</el-button>
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

            <!-- 添加易物交换弹窗 -->
            <el-dialog
                title="发起易物交换"
                :visible.sync="exchangeDialogVisible"
                width="60%">
                <el-form :model="exchangeForm" :rules="exchangeRules" ref="exchangeForm" label-width="120px">
                    <el-form-item label="交换对象物品" prop="requestItemId">
                        <div class="exchange-preview-item">
                            <div v-if="idleItemInfo">
                                <div class="item-preview">
                                    <img :src="getImageUrl(processedPictureList[0])" class="preview-img">
                                    <div class="item-info">
                                        <div class="item-name">{{ idleItemInfo.idleName }}</div>
                                        <div class="item-price">¥{{ idleItemInfo.idlePrice }}</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </el-form-item>
                    
                    <el-form-item label="物品所有者">
                        <div class="owner-info" v-if="idleItemInfo && idleItemInfo.user">
                            <img :src="idleItemInfo.user.avatar" class="owner-avatar">
                            <span class="owner-name">{{ idleItemInfo.user.nickname }}</span>
                        </div>
                    </el-form-item>
                    
                    <el-form-item label="我的交换物品" prop="offerItemId">
                        <el-select v-model="exchangeForm.offerItemId" placeholder="选择您提供交换的物品">
                            <el-option
                                v-for="item in myItems"
                                :key="item.id"
                                :label="item.idleName"
                                :value="item.id">
                                <div class="item-option">
                                    <img :src="getFirstImage(item.pictureList)" class="item-img">
                                    <div class="item-info">
                                        <div class="item-name">{{ item.idleName }}</div>
                                        <div class="item-price">¥{{ item.idlePrice }}</div>
                                    </div>
                                </div>
                            </el-option>
                        </el-select>
                    </el-form-item>
                    
                    <el-form-item label="交换理由" prop="exchangeReason">
                        <el-input
                            type="textarea"
                            :rows="4"
                            placeholder="请输入交换理由，向对方说明为什么想要交换这个物品"
                            v-model="exchangeForm.exchangeReason">
                        </el-input>
                    </el-form-item>
                </el-form>
                
                <div class="exchange-preview" v-if="exchangeForm.offerItemId">
                    <h4>交换预览</h4>
                    <el-divider></el-divider>
                    <div class="exchange-preview-content">
                        <div class="preview-item">
                            <h4>我将获得</h4>
                            <div class="item-card" v-if="idleItemInfo">
                                <img :src="getImageUrl(processedPictureList[0])" class="preview-img">
                                <div class="preview-info">
                                    <div class="preview-name">{{ idleItemInfo.idleName }}</div>
                                    <div class="preview-price">¥{{ idleItemInfo.idlePrice }}</div>
                                </div>
                            </div>
                        </div>
                        <div class="exchange-arrow">
                            <i class="el-icon-right"></i>
                        </div>
                        <div class="preview-item">
                            <h4>我将付出</h4>
                            <div class="item-card" v-if="selectedMyItem">
                                <img :src="getFirstImage(selectedMyItem.pictureList)" class="preview-img">
                                <div class="preview-info">
                                    <div class="preview-name">{{ selectedMyItem.idleName }}</div>
                                    <div class="preview-price">¥{{ selectedMyItem.idlePrice }}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <span slot="footer" class="dialog-footer">
                    <el-button @click="exchangeDialogVisible = false">取 消</el-button>
                    <el-button type="primary" @click="submitExchangeRequest" :loading="submitLoading">提交交换请求</el-button>
                </span>
            </el-dialog>
            
            <!-- 添加查看交换请求弹窗 -->
            <el-dialog
                title="交换请求管理"
                :visible.sync="exchangeRequestsDialogVisible"
                width="70%">
                <el-tabs v-model="activeRequestTab">
                    <el-tab-pane label="收到的请求" name="received">
                        <div v-loading="requestsLoading">
                            <div v-if="receivedRequests.length === 0" class="empty-requests">
                                <i class="el-icon-info"></i>
                                <p>暂无收到的交换请求</p>
                            </div>
                            <el-card v-for="request in receivedRequests" :key="request.id" class="request-card">
                                <div class="request-header">
                                    <div class="request-user">
                                        <img :src="request.requestUser.avatar" class="user-avatar">
                                        <span>{{ request.requestUser.nickname }}</span>
                                    </div>
                                    <div class="request-time">{{ request.createTime }}</div>
                                </div>
                                <div class="request-content">
                                    <div class="exchange-items">
                                        <div class="item-card">
                                            <div class="item-title">对方想要的物品</div>
                                            <img :src="getFirstImage(request.requestItem.pictureList)" class="item-img">
                                            <div class="item-info">
                                                <div class="item-name">{{ request.requestItem.idleName }}</div>
                                                <div class="item-price">¥{{ request.requestItem.idlePrice }}</div>
                                            </div>
                                        </div>
                                        <div class="exchange-arrow">
                                            <i class="el-icon-right"></i>
                                        </div>
                                        <div class="item-card">
                                            <div class="item-title">对方提供的物品</div>
                                            <img :src="getFirstImage(request.offerItem.pictureList)" class="item-img">
                                            <div class="item-info">
                                                <div class="item-name">{{ request.offerItem.idleName }}</div>
                                                <div class="item-price">¥{{ request.offerItem.idlePrice }}</div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="request-reason">
                                        <div class="reason-title">交换理由:</div>
                                        <div class="reason-content">{{ request.exchangeReason }}</div>
                                    </div>
                                </div>
                                <div class="request-actions">
                                    <el-tag :type="getStatusTag(request.status).type">{{ getStatusTag(request.status).text }}</el-tag>
                                    <div class="action-buttons" v-if="request.status === 0">
                                        <el-button type="success" size="small" @click="acceptRequest(request.id)">接受</el-button>
                                        <el-button type="danger" size="small" @click="rejectRequest(request.id)">拒绝</el-button>
                                    </div>
                                    <div class="action-buttons" v-if="request.status === 1">
                                        <el-button type="primary" size="small" @click="completeExchange(request.id)">完成交换</el-button>
                                    </div>
                                </div>
                            </el-card>
                        </div>
                    </el-tab-pane>
                    <el-tab-pane label="我发送的请求" name="sent">
                        <div v-loading="requestsLoading">
                            <div v-if="sentRequests.length === 0" class="empty-requests">
                                <i class="el-icon-info"></i>
                                <p>暂无发送的交换请求</p>
                            </div>
                            <el-card v-for="request in sentRequests" :key="request.id" class="request-card">
                                <div class="request-header">
                                    <div class="request-user">
                                        <img :src="request.itemOwner.avatar" class="user-avatar">
                                        <span>发送给: {{ request.itemOwner.nickname }}</span>
                                    </div>
                                    <div class="request-time">{{ request.createTime }}</div>
                                </div>
                                <div class="request-content">
                                    <div class="exchange-items">
                                        <div class="item-card">
                                            <div class="item-title">我想要的物品</div>
                                            <img :src="getFirstImage(request.requestItem.pictureList)" class="item-img">
                                            <div class="item-info">
                                                <div class="item-name">{{ request.requestItem.idleName }}</div>
                                                <div class="item-price">¥{{ request.requestItem.idlePrice }}</div>
                                            </div>
                                        </div>
                                        <div class="exchange-arrow">
                                            <i class="el-icon-right"></i>
                                        </div>
                                        <div class="item-card">
                                            <div class="item-title">我提供的物品</div>
                                            <img :src="getFirstImage(request.offerItem.pictureList)" class="item-img">
                                            <div class="item-info">
                                                <div class="item-name">{{ request.offerItem.idleName }}</div>
                                                <div class="item-price">¥{{ request.offerItem.idlePrice }}</div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="request-reason">
                                        <div class="reason-title">交换理由:</div>
                                        <div class="reason-content">{{ request.exchangeReason }}</div>
                                    </div>
                                </div>
                                <div class="request-actions">
                                    <el-tag :type="getStatusTag(request.status).type">{{ getStatusTag(request.status).text }}</el-tag>
                                </div>
                            </el-card>
                        </div>
                    </el-tab-pane>
                </el-tabs>
            </el-dialog>

            <!-- 添加捐赠弹窗 -->
            <el-dialog
                title="爱心捐赠"
                :visible.sync="donationDialogVisible"
                width="50%">
                <el-form :model="donationForm" :rules="donationRules" ref="donationForm" label-width="120px">
                    <el-form-item label="捐赠物品" prop="itemId">
                        <div class="donation-preview-item">
                            <div v-if="idleItemInfo">
                                <div class="item-preview">
                                    <img :src="getImageUrl(processedPictureList[0])" class="preview-img">
                                    <div class="item-info">
                                        <div class="item-name">{{ idleItemInfo.idleName }}</div>
                                        <div class="item-price">¥{{ idleItemInfo.idlePrice }}</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </el-form-item>
                    
                    <el-form-item label="捐赠类型" prop="donationType">
                        <el-radio-group v-model="donationForm.donationType">
                            <el-radio :label="0">公益捐赠</el-radio>
                            <el-radio :label="1">环保回收</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    
                    <el-form-item label="捐赠理由" prop="donationReason">
                        <el-input
                            type="textarea"
                            :rows="4"
                            placeholder="请输入捐赠理由，说明为什么想要捐赠这个物品"
                            v-model="donationForm.donationReason">
                        </el-input>
                    </el-form-item>
                </el-form>
                
                <div class="donation-notes">
                    <p><i class="el-icon-info"></i> 捐赠说明：</p>
                    <p>1. 公益捐赠：您的物品将捐赠给有需要的人或公益组织</p>
                    <p>2. 环保回收：闲置物品将被环保回收，减少资源浪费</p>
                    <p>3. 捐赠成功后，物品将自动下架</p>
                    <p>4. 捐赠后可能有工作人员与您联系，请保持联系方式畅通</p>
                </div>
                
                <span slot="footer" class="dialog-footer">
                    <el-button @click="donationDialogVisible = false">取 消</el-button>
                    <el-button type="primary" @click="submitDonationRequest" :loading="submitLoading">提交捐赠</el-button>
                </span>
            </el-dialog>

            <!-- 添加举报弹窗 -->
            <el-dialog
                title="举报"
                :visible.sync="reportDialogVisible"
                width="50%">
                <report-form 
                    :reported-user-id="idleItemInfo.userId"
                    :reported-item-id="idleItemInfo.id"
                    @report-submitted="reportDialogVisible = false"
                    @cancel="reportDialogVisible = false">
                </report-form>
            </el-dialog>
        </app-body>
    </div>
</template>

<script>
    import AppHead from '../common/AppHeader.vue';
    import AppBody from '../common/AppPageBody.vue'
    import AppFoot from '../common/AppFoot.vue'
    import ReportForm from '../common/ReportForm.vue';

    export default {
        name: "idle-details",
        components: {
            AppHead,
            AppBody,
            AppFoot,
            ReportForm
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
                totalImages: 0,
                exchangeDialogVisible: false,
                exchangeForm: {
                    requestItemId: '',
                    offerItemId: '',
                    itemOwnerId: '',
                    exchangeReason: ''
                },
                exchangeRules: {
                    offerItemId: [
                        { required: true, message: '请选择您提供交换的物品', trigger: 'change' }
                    ],
                    exchangeReason: [
                        { required: true, message: '请输入交换理由', trigger: 'blur' },
                        { min: 5, max: 200, message: '长度在 5 到 200 个字符', trigger: 'blur' }
                    ]
                },
                submitLoading: false,
                myItems: [],
                selectedMyItem: null,
                exchangeRequestsDialogVisible: false,
                activeRequestTab: 'received',
                receivedRequests: [],
                sentRequests: [],
                requestsLoading: false,
                // 捐赠相关数据
                donationDialogVisible: false,
                donationForm: {
                    itemId: '',
                    donationType: 0,
                    donationReason: ''
                },
                donationRules: {
                    donationType: [
                        { required: true, message: '请选择捐赠类型', trigger: 'change' }
                    ],
                    donationReason: [
                        { required: true, message: '请输入捐赠理由', trigger: 'blur' },
                        { min: 5, max: 200, message: '长度在 5 到 200 个字符', trigger: 'blur' }
                    ]
                },
                reportDialogVisible: false
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
            },
            selectedMyItem() {
                return this.myItems.find(item => item.id === this.exchangeForm.offerItemId);
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
            exchangeButton(idleItemInfo) {
                // 设置表单初始值
                this.exchangeForm.requestItemId = idleItemInfo.id;
                this.exchangeForm.itemOwnerId = idleItemInfo.userId;
                
                // 加载我的可交换物品
                this.loadMyItems();
                
                // 显示对话框
                this.exchangeDialogVisible = true;
            },
            loadMyItems() {
                this.$api.checkMyIdle().then(res => {
                    if (res.data) {
                        // 过滤出在售状态的物品
                        this.myItems = res.data.filter(item => item.idleStatus === 1);
                    }
                }).catch(err => {
                    console.error('获取我的物品失败', err);
                    this.$message.error('获取我的物品失败');
                });
            },
            submitExchangeRequest() {
                this.$refs.exchangeForm.validate((valid) => {
                    if (valid) {
                        this.submitLoading = true;
                        this.$api.createExchangeRequest({
                            requestItemId: this.exchangeForm.requestItemId,
                            offerItemId: this.exchangeForm.offerItemId,
                            itemOwnerId: this.exchangeForm.itemOwnerId,
                            exchangeReason: this.exchangeForm.exchangeReason
                        }).then(res => {
                            if (res.status_code === 1) {
                                this.$message.success('交换请求已发送');
                                this.exchangeDialogVisible = false;
                                
                                // 清空表单
                                this.exchangeForm.offerItemId = '';
                                this.exchangeForm.exchangeReason = '';
                            } else {
                                this.$message.error(res.msg || '发送交换请求失败');
                            }
                        }).catch(err => {
                            console.error('发送交换请求失败', err);
                            this.$message.error('发送交换请求失败');
                        }).finally(() => {
                            this.submitLoading = false;
                        });
                    } else {
                        return false;
                    }
                });
            },
            viewReceivedRequests() {
                this.exchangeRequestsDialogVisible = true;
                this.loadExchangeRequests();
            },
            loadExchangeRequests() {
                this.requestsLoading = true;
                
                // 加载收到的请求
                this.$api.getReceivedExchangeRequests().then(res => {
                    if (res.data) {
                        this.receivedRequests = res.data;
                    } else {
                        this.receivedRequests = [];
                    }
                }).catch(err => {
                    console.error('获取收到的交换请求失败', err);
                    this.$message.error('获取收到的交换请求失败');
                });
                
                // 加载发送的请求
                this.$api.getMyExchangeRequests().then(res => {
                    if (res.data) {
                        this.sentRequests = res.data;
                    } else {
                        this.sentRequests = [];
                    }
                }).catch(err => {
                    console.error('获取发送的交换请求失败', err);
                    this.$message.error('获取发送的交换请求失败');
                }).finally(() => {
                    this.requestsLoading = false;
                });
            },
            getStatusTag(status) {
                const statusMap = {
                    0: { type: 'info', text: '等待审核' },
                    1: { type: 'success', text: '已接受' },
                    2: { type: 'danger', text: '已拒绝' },
                    3: { type: 'primary', text: '已完成' }
                };
                return statusMap[status] || { type: 'info', text: '未知状态' };
            },
            acceptRequest(requestId) {
                this.$confirm('确定接受此交换请求吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$api.acceptExchangeRequest(requestId).then(res => {
                        if (res.status_code === 1) {
                            this.$message.success('已接受交换请求');
                            this.loadExchangeRequests();
                        } else {
                            this.$message.error(res.msg || '操作失败');
                        }
                    }).catch(err => {
                        console.error('接受交换请求失败', err);
                        this.$message.error('接受交换请求失败');
                    });
                }).catch(() => {
                    // 取消操作
                });
            },
            rejectRequest(requestId) {
                this.$confirm('确定拒绝此交换请求吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$api.rejectExchangeRequest(requestId).then(res => {
                        if (res.status_code === 1) {
                            this.$message.success('已拒绝交换请求');
                            this.loadExchangeRequests();
                        } else {
                            this.$message.error(res.msg || '操作失败');
                        }
                    }).catch(err => {
                        console.error('拒绝交换请求失败', err);
                        this.$message.error('拒绝交换请求失败');
                    });
                }).catch(() => {
                    // 取消操作
                });
            },
            completeExchange(requestId) {
                this.$confirm('确认已完成物品交换吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$api.completeExchange(requestId).then(res => {
                        if (res.status_code === 1) {
                            this.$message.success('交换已完成');
                            this.loadExchangeRequests();
                        } else {
                            this.$message.error(res.msg || '操作失败');
                        }
                    }).catch(err => {
                        console.error('完成交换失败', err);
                        this.$message.error('完成交换失败');
                    });
                }).catch(() => {
                    // 取消操作
                });
            },
            getFirstImage(pictureList) {
                if (Array.isArray(pictureList)) {
                    return this.getImageUrl(pictureList[0]);
                } else if (typeof pictureList === 'string') {
                    return this.getImageUrl(pictureList);
                } else {
                    console.error('无效的图片列表格式');
                    return '';
                }
            },
            // 捐赠相关方法
            donationButton(idleItemInfo) {
                this.donationForm.itemId = idleItemInfo.id;
                this.donationDialogVisible = true;
            },
            submitDonationRequest() {
                this.$refs.donationForm.validate((valid) => {
                    if (valid) {
                        this.submitLoading = true;
                        this.$api.createDonationRequest({
                            itemId: this.donationForm.itemId,
                            donationType: this.donationForm.donationType,
                            donationReason: this.donationForm.donationReason
                        }).then(res => {
                            if (res.status_code === 1) {
                                this.$message.success('捐赠请求已提交');
                                this.donationDialogVisible = false;
                                
                                // 清空表单
                                this.donationForm.donationType = 0;
                                this.donationForm.donationReason = '';
                                
                                // 更新物品状态
                                this.idleItemInfo.idleStatus = 2; // 捐赠中自动下架
                                
                            } else {
                                this.$message.error(res.msg || '提交捐赠请求失败');
                            }
                        }).catch(err => {
                            console.error('提交捐赠请求失败', err);
                            this.$message.error('提交捐赠请求失败');
                        }).finally(() => {
                            this.submitLoading = false;
                        });
                    } else {
                        return false;
                    }
                });
            },
            // 添加举报按钮点击事件
            reportButton(idleItemInfo) {
                this.reportDialogVisible = true;
            },
        },
    }
</script>

<style scoped>
    .idle-details-container {
        min-height: 85vh;
    }

    .details-header {
        min-height: 80px;
        height: auto;
        border-bottom: 10px solid #f6f6f6;
        display: flex;
        justify-content: space-between;
        padding: 20px;
        align-items: center;
        flex-wrap: wrap; /* 小屏幕支持换行 */
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
        flex-wrap: wrap; /* 允许按钮换行 */
        gap: 10px; /* 按钮之间的间距 */
        min-height: 50px;
        margin-left: 20px; /* 与用户信息保持一定距离 */
        flex: 1; /* 占用剩余空间 */
        justify-content: flex-end; /* 按钮靠右对齐 */
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

    /* 易物交换弹窗样式 */
    .exchange-preview-item {
        margin-bottom: 15px;
    }
    
    .item-preview {
        display: flex;
        align-items: center;
    }
    
    .item-option {
        display: flex;
        align-items: center;
    }
    
    .item-img {
        width: 40px;
        height: 40px;
        object-fit: cover;
        margin-right: 10px;
        border-radius: 4px;
    }
    
    .owner-info {
        display: flex;
        align-items: center;
    }
    
    .owner-avatar {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        margin-right: 10px;
    }
    
    .owner-name {
        font-size: 14px;
        color: #303133;
    }
    
    .exchange-preview {
        margin-top: 20px;
    }
    
    .exchange-preview h4 {
        margin: 10px 0;
        color: #409EFF;
    }
    
    .exchange-preview-content {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
    
    .preview-item {
        flex: 1;
        text-align: center;
    }
    
    .exchange-arrow {
        font-size: 24px;
        color: #409EFF;
        margin: 0 20px;
    }
    
    .preview-img {
        width: 100px;
        height: 100px;
        object-fit: cover;
        border-radius: 4px;
        margin-bottom: 10px;
    }
    
    .preview-info {
        text-align: center;
    }
    
    .preview-name {
        font-size: 14px;
        color: #303133;
        margin-bottom: 5px;
    }
    
    .preview-price {
        font-size: 14px;
        color: #F56C6C;
    }
    
    /* 交换请求管理弹窗样式 */
    .request-card {
        margin-bottom: 20px;
    }
    
    .request-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 15px;
    }
    
    .request-user {
        display: flex;
        align-items: center;
    }
    
    .user-avatar {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        margin-right: 10px;
    }
    
    .request-time {
        color: #909399;
        font-size: 14px;
    }
    
    .request-content {
        margin-bottom: 15px;
    }
    
    .exchange-items {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 15px;
    }
    
    .item-card {
        flex: 1;
        text-align: center;
        padding: 10px;
        border: 1px solid #EBEEF5;
        border-radius: 4px;
    }
    
    .item-title {
        font-size: 14px;
        color: #409EFF;
        margin-bottom: 10px;
    }
    
    .item-img {
        width: 80px;
        height: 80px;
        object-fit: cover;
        border-radius: 4px;
        margin-bottom: 10px;
    }
    
    .item-info {
        text-align: center;
    }
    
    .item-name {
        font-size: 14px;
        color: #303133;
        margin-bottom: 5px;
    }
    
    .item-price {
        font-size: 14px;
        color: #F56C6C;
    }
    
    .request-reason {
        background-color: #F5F7FA;
        padding: 10px;
        border-radius: 4px;
    }
    
    .reason-title {
        font-weight: bold;
        margin-bottom: 5px;
    }
    
    .reason-content {
        color: #606266;
    }
    
    .request-actions {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
    
    .action-buttons {
        display: flex;
        gap: 10px;
    }
    
    .empty-requests {
        text-align: center;
        padding: 30px;
        color: #909399;
    }
    
    .empty-requests i {
        font-size: 48px;
        margin-bottom: 10px;
    }

    /* 捐赠弹窗样式 */
    .donation-preview-item {
        margin-bottom: 15px;
    }

    .donation-notes {
        background-color: #f8f8f8;
        padding: 15px;
        border-radius: 4px;
        margin-top: 20px;
        font-size: 14px;
        color: #606266;
    }

    .donation-notes p {
        margin: 5px 0;
    }

    .donation-notes i {
        color: #E6A23C;
        margin-right: 5px;
    }
</style>
