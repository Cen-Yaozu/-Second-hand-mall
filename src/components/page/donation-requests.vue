<template>
  <div>
    <app-head></app-head>
    <app-body>
      <div class="donation-container">
        <h2 class="page-title">我的捐赠请求</h2>
        
        <div v-loading="loading">
          <div v-if="donationRequests.length === 0" class="empty-requests">
            <i class="el-icon-info"></i>
            <p>暂无捐赠请求记录</p>
            <el-button type="primary" @click="goToDonation">前往捐赠</el-button>
          </div>
          
          <el-card v-for="request in donationRequests" :key="request.id" class="request-card">
            <div class="request-header">
              <div class="request-info">
                <span class="request-id">申请编号: {{ request.id }}</span>
                <el-tag :type="getStatusTag(request.status).type">{{ getStatusTag(request.status).text }}</el-tag>
              </div>
              <div class="request-time">{{ request.createTime }}</div>
            </div>
            
            <div class="request-content">
              <div class="item-card">
                <img :src="getImageUrl(request.item.pictureList[0])" class="item-img">
                <div class="item-info">
                  <div class="item-name">{{ request.item.idleName }}</div>
                  <div class="item-price">¥{{ request.item.idlePrice }}</div>
                </div>
              </div>
              
              <div class="donation-details">
                <div class="donation-type">
                  <span class="label">捐赠类型:</span>
                  <el-tag size="small" type="success" v-if="request.donationType === 0">公益捐赠</el-tag>
                  <el-tag size="small" type="warning" v-else>环保回收</el-tag>
                </div>
                
                <div class="donation-reason">
                  <span class="label">捐赠理由:</span>
                  <div class="reason-content">{{ request.donationReason }}</div>
                </div>
                
                <div class="admin-feedback" v-if="request.adminFeedback">
                  <span class="label">管理员反馈:</span>
                  <div class="feedback-content">{{ request.adminFeedback }}</div>
                </div>
              </div>
            </div>
            
            <div class="request-actions">
              <el-button size="small" type="danger" v-if="request.status === 0" @click="cancelRequest(request.id)">取消捐赠</el-button>
              <el-button size="small" type="primary" v-if="request.status === 1" @click="viewDonationDetail(request.id)">查看详情</el-button>
            </div>
          </el-card>
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
  name: 'donation-requests',
  components: {
    AppHead,
    AppBody,
    AppFoot
  },
  data() {
    return {
      loading: false,
      donationRequests: []
    };
  },
  created() {
    this.loadDonationRequests();
  },
  methods: {
    loadDonationRequests() {
      this.loading = true;
      this.$api.getMyDonationRequests().then(res => {
        if (res.data) {
          this.donationRequests = res.data;
        } else {
          this.donationRequests = [];
        }
      }).catch(err => {
        console.error('获取捐赠请求失败', err);
        this.$message.error('获取捐赠请求失败');
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
    cancelRequest(requestId) {
      this.$confirm('确定要取消此捐赠请求吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$api.cancelDonationRequest(requestId).then(res => {
          if (res.status_code === 1) {
            this.$message.success('捐赠请求已取消');
            this.loadDonationRequests();
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
    viewDonationDetail(requestId) {
      this.$router.push({
        path: '/donation/detail',
        query: { id: requestId }
      });
    },
    goToDonation() {
      this.$router.push('/donation');
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
  padding: 20px;
  min-height: 80vh;
}

.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  color: #303133;
}

.request-card {
  margin-bottom: 20px;
}

.request-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.request-info {
  display: flex;
  align-items: center;
}

.request-id {
  font-size: 14px;
  color: #606266;
  margin-right: 10px;
}

.request-time {
  color: #909399;
  font-size: 14px;
}

.request-content {
  display: flex;
  margin-bottom: 15px;
}

.item-card {
  width: 120px;
  text-align: center;
  margin-right: 20px;
}

.item-img {
  width: 100px;
  height: 100px;
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

.donation-details {
  flex: 1;
}

.donation-type,
.donation-reason,
.admin-feedback {
  margin-bottom: 10px;
}

.label {
  font-weight: bold;
  color: #606266;
  margin-right: 10px;
}

.reason-content,
.feedback-content {
  background-color: #F5F7FA;
  padding: 10px;
  border-radius: 4px;
  margin-top: 5px;
  color: #606266;
}

.request-actions {
  display: flex;
  justify-content: flex-end;
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

.empty-requests p {
  margin-bottom: 20px;
}
</style> 