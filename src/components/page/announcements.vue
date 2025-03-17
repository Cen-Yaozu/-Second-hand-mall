<template>
  <div class="announcements">
    <app-head></app-head>
    <app-body>
      <div class="list-container">
        <div class="header">
          <h2>公告列表</h2>
        </div>
        
        <div class="announcement-list" v-loading="loading">
          <el-card class="announcement-item" v-for="item in announcementList" :key="item.id" @click.native="viewDetail(item.id)">
            <div class="item-header">
              <h3 class="item-title">{{ item.title }}</h3>
              <el-tag type="danger" size="small" v-if="item.important === 1">重要</el-tag>
            </div>
            <div class="item-content">{{ getPlainText(item.content) }}</div>
            <div class="item-footer">
              <span class="time">{{ formatDate(item.publish_time) }}</span>
              <el-button type="text" class="read-more">阅读更多</el-button>
            </div>
          </el-card>
          
          <div class="pagination-container" v-if="total > 0">
            <el-pagination
              @current-change="handleCurrentChange"
              :current-page="currentPage"
              :page-size="pageSize"
              layout="prev, pager, next, jumper"
              :total="total">
            </el-pagination>
          </div>
          
          <el-empty description="暂无公告" v-if="announcementList.length === 0 && !loading"></el-empty>
        </div>
      </div>
    </app-body>
    <app-foot></app-foot>
  </div>
</template>

<script>
import AppHead from '../common/AppHeader.vue'
import AppBody from '../common/AppPageBody.vue'
import AppFoot from '../common/AppFoot.vue'

export default {
  name: "Announcements",
  components: {
    AppHead,
    AppBody,
    AppFoot
  },
  data() {
    return {
      loading: false,
      announcementList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0
    };
  },
  created() {
    this.fetchAnnouncementList();
  },
  methods: {
    fetchAnnouncementList() {
      this.loading = true;
      this.$api.getAnnouncementList({
        page: this.currentPage,
        limit: this.pageSize,
        status: 1 // 只获取已发布的公告
      })
      .then(res => {
        this.loading = false;
        if (res.status_code === 1) {
          this.announcementList = res.data.list || [];
          this.total = res.data.total || 0;
        } else {
          this.$message.error(res.msg || '获取公告列表失败');
        }
      })
      .catch(err => {
        this.loading = false;
        console.error("获取公告列表失败:", err);
        this.$message.error('获取公告列表失败');
      });
    },
    
    handleCurrentChange(val) {
      this.currentPage = val;
      this.fetchAnnouncementList();
    },
    
    viewDetail(id) {
      this.$router.push(`/announcement/${id}`);
    },
    
    formatDate(timestamp) {
      if (!timestamp) return '';
      const date = new Date(timestamp * 1000);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
    },
    
    getPlainText(html) {
      if (!html) return '';
      // 创建临时元素解析HTML
      const temp = document.createElement('div');
      temp.innerHTML = html;
      // 获取纯文本并截取前100个字符
      const text = temp.textContent || temp.innerText || '';
      return text.length > 100 ? text.substring(0, 100) + '...' : text;
    }
  }
};
</script>

<style scoped>
.announcements {
  background-color: #f6f6f6;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.list-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  margin-bottom: 20px;
}

.header h2 {
  color: #303133;
  font-weight: 600;
}

.announcement-list {
  min-height: 400px;
}

.announcement-item {
  margin-bottom: 15px;
  cursor: pointer;
  transition: transform 0.3s;
}

.announcement-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.item-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.item-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  flex: 1;
}

.item-content {
  color: #606266;
  font-size: 14px;
  margin-bottom: 10px;
  line-height: 1.6;
}

.item-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #909399;
  font-size: 13px;
}

.read-more {
  padding: 0;
  font-size: 13px;
}

.pagination-container {
  margin-top: 20px;
  text-align: center;
}
</style> 