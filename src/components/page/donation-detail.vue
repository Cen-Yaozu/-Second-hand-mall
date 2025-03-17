<template>
  <div>
    <app-head></app-head>
    <app-body>
      <div class="donation-detail-container">
        <div v-loading="loading">
          <div v-if="!donationRequest" class="empty-detail">
            <i class="el-icon-warning-outline"></i>
            <p>未找到捐赠请求信息</p>
            <el-button type="primary" @click="goBack">返回</el-button>
          </div>
          
          <div v-else>
            <div class="detail-header">
              <div class="header-left">
                <h2>捐赠详情</h2>
                <el-tag :type="getStatusTag(donationRequest.status).type">{{ getStatusTag(donationRequest.status).text }}</el-tag>
              </div>
              <div class="header-right">
                <el-button type="primary" icon="el-icon-back" @click="goBack">返回</el-button>
              </div>
            </div>
            
            <el-card class="detail-card">
              <div class="detail-section">
                <h3>基本信息</h3>
                <el-row :gutter="20">
                  <el-col :span="12">
                    <div class="detail-item">
                      <span class="label">申请编号：</span>
                      <span>{{ donationRequest.id }}</span>
                    </div>
                  </el-col>
                  <el-col :span="12">
                    <div class="detail-item">
                      <span class="label">申请时间：</span>
                      <span>{{ donationRequest.createTime }}</span>
                    </div>
                  </el-col>
                  <el-col :span="12">
                    <div class="detail-item">
                      <span class="label">捐赠类型：</span>
                      <el-tag size="small" type="success" v-if="donationRequest.donationType === 0">公益捐赠</el-tag>
                      <el-tag size="small" type="warning" v-else>环保回收</el-tag>
                    </div>
                  </el-col>
                  <el-col :span="12">
                    <div class="detail-item">
                      <span class="label">当前状态：</span>
                      <el-tag :type="getStatusTag(donationRequest.status).type">{{ getStatusTag(donationRequest.status).text }}</el-tag>
                    </div>
                  </el-col>
                </el-row>
              </div>
              
              <el-divider></el-divider>
              
              <div class="detail-section">
                <h3>物品信息</h3>
                <div class="item-detail">
                  <div class="item-image">
                    <img :src="getImageUrl(donationRequest.item.pictureList[0])" alt="物品图片">
                  </div>
                  <div class="item-info">
                    <h4>{{ donationRequest.item.idleName }}</h4>
                    <div class="item-price">¥{{ donationRequest.item.idlePrice }}</div>
                    <div class="item-description">{{ donationRequest.item.idleDetails }}</div>
                  </div>
                </div>
              </div>
              
              <el-divider></el-divider>
              
              <div class="detail-section">
                <h3>捐赠理由</h3>
                <div class="reason-content">{{ donationRequest.donationReason }}</div>
              </div>
              
              <el-divider v-if="donationRequest.adminFeedback"></el-divider>
              
              <div class="detail-section" v-if="donationRequest.adminFeedback">
                <h3>管理员反馈</h3>
                <div class="feedback-content">{{ donationRequest.adminFeedback }}</div>
              </div>
              
              <el-divider v-if="donationRequest.status === 1"></el-divider>
              
              <div class="detail-section" v-if="donationRequest.status === 1">
                <h3>捐赠流程</h3>
                <div class="process-info">
                  <p><i class="el-icon-info"></i> 您的捐赠请求已被接受，请按照以下步骤完成捐赠：</p>
                  <ol>
                    <li>请将物品打包好，确保物品完好无损</li>
                    <li>工作人员将与您联系，安排物品交接事宜</li>
                    <li>完成物品交接后，捐赠流程将标记为已完成</li>
                    <li>您将收到捐赠证书或回收凭证（如适用）</li>
                  </ol>
                </div>
              </div>
              
              <div class="detail-actions" v-if="donationRequest.status === 0">
                <el-button type="danger" @click="cancelDonation">取消捐赠</el-button>
              </div>
            </el-card>
          </div>
        </div>
      </div>
    </app-body>
    <app-foot></app-foot>
  </div>
</template>

<script>
import AppHead from '../common/AppHeader.vue';
import AppBody from '../common/AppPageBody.vue';
import AppFoot from '../common/AppFoot.vue';

export default {
  name: 'donation-detail',
  components: {
    AppHead,
    AppBody,
    AppFoot
  },
  data() {
    return {
      loading: false,
      donationRequest: null
    };
  },
  created() {
    this.loadDonationDetail();
  },
  methods: {
    loadDonationDetail() {
      const requestId = this.$route.query.id;
      if (!requestId) {
        this.$message.error('缺少请求ID参数');
        return;
      }
      
      this.loading = true;
      this.$api.getDonationRequestDetail(requestId).then(res => {
        if (res.data) {
          this.donationRequest = res.data;
        } else {
          this.$message.error('未找到捐赠请求信息');
        }
      }).catch(err => {
        console.error('获取捐赠详情失败', err);
        this.$message.error('获取捐赠详情失败');
      }).finally(() => {
        this.loading = false;
      });
    },
    getStatusTag(status) {
      const statusMap = {
        0: { type: 'info', text: '待审核' },
        1: { type: 'success', text: '已接受' },
        2: { type: 'danger', text: '已拒绝' },
        3: { type: 'primary', text: '已完成' }
      };
      return statusMap[status] || { type: 'info', text: '未知状态' };
    },
    cancelDonation() {
      this.$confirm('确定要取消此捐赠请求吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$api.cancelDonationRequest(this.donationRequest.id).then(res => {
          if (res.status_code === 1) {
            this.$message.success('捐赠请求已取消');
            this.loadDonationDetail();
          } else {
            this.$message.error(res.msg || '操作失败');
          }
        }).catch(err => {
          console.error('取消捐赠请求失败', err);
          this.$message.error('取消捐赠请求失败');
        });
      }).catch(() => {
        // 取消操作
      });
    },
    goBack() {
      this.$router.push('/donation/my-requests');
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
.donation-detail-container {
  padding: 20px;
  min-height: 80vh;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-left h2 {
  margin: 0;
  margin-right: 15px;
}

.detail-card {
  margin-bottom: 20px;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h3 {
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 18px;
  color: #303133;
}

.detail-item {
  margin-bottom: 10px;
}

.label {
  font-weight: bold;
  color: #606266;
  margin-right: 10px;
}

.item-detail {
  display: flex;
}

.item-image {
  width: 200px;
  height: 200px;
  margin-right: 20px;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.item-info {
  flex: 1;
}

.item-info h4 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 16px;
}

.item-price {
  color: #F56C6C;
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
}

.item-description {
  color: #606266;
  line-height: 1.5;
}

.reason-content,
.feedback-content {
  background-color: #F5F7FA;
  padding: 15px;
  border-radius: 4px;
  color: #606266;
  line-height: 1.5;
}

.process-info {
  background-color: #F0F9EB;
  padding: 15px;
  border-radius: 4px;
  color: #606266;
}

.process-info i {
  color: #67C23A;
  margin-right: 5px;
}

.process-info ol {
  margin-top: 10px;
  margin-bottom: 0;
  padding-left: 20px;
}

.process-info li {
  margin-bottom: 5px;
}

.detail-actions {
  margin-top: 20px;
  text-align: right;
}

.empty-detail {
  text-align: center;
  padding: 50px 0;
  color: #909399;
}

.empty-detail i {
  font-size: 48px;
  margin-bottom: 20px;
}

.empty-detail p {
  margin-bottom: 20px;
}
</style> 