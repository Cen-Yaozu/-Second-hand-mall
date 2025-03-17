<template>
  <div>
    <app-head></app-head>
    <app-body>
      <div class="donation-container">
        <div class="donation-banner">
          <div class="banner-content">
            <h1>爱心捐赠 · 环保回收</h1>
            <p>让闲置物品发挥更大的价值，传递爱心，保护环境</p>
            <el-button type="success" @click="scrollToMyItems">查看我的可捐赠物品</el-button>
            <el-button type="primary" @click="goToMyRequests">我的捐赠记录</el-button>
          </div>
        </div>
        
        <div class="donation-intro">
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="intro-card">
                <div class="intro-icon">
                  <i class="el-icon-star-on"></i>
                </div>
                <div class="intro-content">
                  <h3>公益捐赠</h3>
                  <p>您的闲置物品将被捐赠给有需要的人或公益组织，传递爱心，帮助他人。</p>
                  <p>适合：衣物、书籍、玩具、电子产品等保存完好的物品</p>
                </div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="intro-card">
                <div class="intro-icon">
                  <i class="el-icon-delete"></i>
                </div>
                <div class="intro-content">
                  <h3>环保回收</h3>
                  <p>闲置物品将被专业回收处理，减少资源浪费，保护环境。</p>
                  <p>适合：旧电器、废纸、塑料、金属等可回收物品</p>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
        
        <div class="donation-process">
          <h2>捐赠流程</h2>
          <el-steps :active="4" finish-status="success" align-center>
            <el-step title="选择物品" description="从您的闲置物品中选择"></el-step>
            <el-step title="提交申请" description="填写捐赠理由和类型"></el-step>
            <el-step title="等待审核" description="工作人员会尽快审核"></el-step>
            <el-step title="完成捐赠" description="物品将被妥善处理"></el-step>
          </el-steps>
        </div>
        
        <div id="my-items" class="my-items-section">
          <h2>我的可捐赠物品</h2>
          <div v-loading="loading">
            <div v-if="myItems.length === 0" class="empty-items">
              <i class="el-icon-goods"></i>
              <p>您暂无可捐赠的物品</p>
              <el-button type="primary" @click="goToRelease">发布闲置物品</el-button>
            </div>
            
            <el-row :gutter="20">
              <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in myItems" :key="item.id">
                <el-card class="item-card" shadow="hover">
                  <div class="item-image">
                    <img :src="getImageUrl(item.pictureList[0])" alt="物品图片">
                  </div>
                  <div class="item-info">
                    <h3 class="item-name">{{ item.idleName }}</h3>
                    <div class="item-price">¥{{ item.idlePrice }}</div>
                    <div class="item-status">
                      <el-tag size="small" type="success" v-if="item.idleStatus === 1">在售</el-tag>
                      <el-tag size="small" type="info" v-else-if="item.idleStatus === 2">已下架</el-tag>
                      <el-tag size="small" type="warning" v-else>其他状态</el-tag>
                    </div>
                    <div class="item-actions">
                      <el-button type="success" size="small" @click="donateItem(item)" :disabled="item.idleStatus !== 1">捐赠</el-button>
                      <el-button type="primary" size="small" @click="viewItemDetail(item.id)">查看详情</el-button>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </div>
      </div>
    </app-body>
    <app-foot></app-foot>
    
    <!-- 捐赠弹窗 -->
    <el-dialog
      title="爱心捐赠"
      :visible.sync="donationDialogVisible"
      width="50%">
      <el-form :model="donationForm" :rules="donationRules" ref="donationForm" label-width="120px">
        <el-form-item label="捐赠物品" prop="itemId">
          <div class="donation-preview-item">
            <div v-if="selectedItem">
              <div class="item-preview">
                <img :src="getImageUrl(selectedItem.pictureList[0])" class="preview-img">
                <div class="item-info">
                  <div class="item-name">{{ selectedItem.idleName }}</div>
                  <div class="item-price">¥{{ selectedItem.idlePrice }}</div>
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
  </div>
</template>

<script>
import AppHead from '../common/AppHeader.vue';
import AppBody from '../common/AppPageBody.vue';
import AppFoot from '../common/AppFoot.vue';

export default {
  name: 'donation-index',
  components: {
    AppHead,
    AppBody,
    AppFoot
  },
  data() {
    return {
      loading: false,
      myItems: [],
      donationDialogVisible: false,
      selectedItem: null,
      submitLoading: false,
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
      }
    };
  },
  created() {
    this.loadMyItems();
  },
  methods: {
    loadMyItems() {
      this.loading = true;
      this.$api.getAllIdleItem().then(res => {
        if (res.status_code === 1 && res.data) {
          // 过滤出在售状态的物品
          this.myItems = res.data.filter(item => item.idleStatus === 1);
        } else {
          this.myItems = [];
        }
      }).catch(err => {
        console.error('获取我的物品失败', err);
        this.$message.error('获取我的物品失败');
      }).finally(() => {
        this.loading = false;
      });
    },
    donateItem(item) {
      this.selectedItem = item;
      this.donationForm.itemId = item.id;
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
              this.loadMyItems();
              
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
    viewItemDetail(itemId) {
      this.$router.push({
        path: '/details',
        query: { id: itemId }
      });
    },
    goToMyRequests() {
      this.$router.push('/donation/my-requests');
    },
    goToRelease() {
      this.$router.push('/release');
    },
    scrollToMyItems() {
      const element = document.getElementById('my-items');
      if (element) {
        element.scrollIntoView({ behavior: 'smooth' });
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
    }
  }
};
</script>

<style scoped>
.donation-container {
  min-height: 80vh;
}

.donation-banner {
  height: 300px;
  background: linear-gradient(135deg, #67C23A 0%, #409EFF 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  text-align: center;
  margin-bottom: 40px;
}

.banner-content h1 {
  font-size: 36px;
  margin-bottom: 20px;
}

.banner-content p {
  font-size: 18px;
  margin-bottom: 30px;
}

.donation-intro {
  margin-bottom: 40px;
}

.intro-card {
  display: flex;
  background-color: #f8f8f8;
  border-radius: 8px;
  padding: 20px;
  height: 100%;
}

.intro-icon {
  font-size: 48px;
  margin-right: 20px;
  color: #409EFF;
}

.intro-icon .el-icon-star-on {
  color: #E6A23C;
}

.intro-icon .el-icon-delete {
  color: #67C23A;
}

.intro-content h3 {
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 20px;
}

.donation-process {
  margin-bottom: 40px;
  text-align: center;
}

.donation-process h2 {
  margin-bottom: 30px;
}

.my-items-section {
  margin-bottom: 40px;
}

.my-items-section h2 {
  margin-bottom: 20px;
}

.item-card {
  margin-bottom: 20px;
  height: 100%;
}

.item-image {
  height: 200px;
  overflow: hidden;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  padding: 15px 0;
}

.item-name {
  margin: 0 0 10px 0;
  font-size: 16px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-price {
  color: #F56C6C;
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
}

.item-status {
  margin-bottom: 15px;
}

.item-actions {
  display: flex;
  justify-content: space-between;
}

.empty-items {
  text-align: center;
  padding: 50px 0;
  color: #909399;
}

.empty-items i {
  font-size: 48px;
  margin-bottom: 20px;
}

.empty-items p {
  margin-bottom: 20px;
}

.donation-preview-item {
  margin-bottom: 15px;
}

.item-preview {
  display: flex;
  align-items: center;
}

.preview-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 15px;
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