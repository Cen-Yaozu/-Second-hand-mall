<template>
  <div class="announcement-detail">
    <app-head></app-head>
    <app-body>
      <div class="detail-container" v-loading="loading">
        <div class="breadcrumb">
          <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item :to="{ path: '/announcements' }">公告列表</el-breadcrumb-item>
            <el-breadcrumb-item>公告详情</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="announcement-content" v-if="announcement">
          <h1 class="title">{{ announcement.title }}</h1>
          <div class="meta">
            <span class="time">发布时间: {{ formatDate(announcement.publish_time) }}</span>
            <el-tag type="danger" size="small" v-if="announcement.important === 1">重要</el-tag>
          </div>
          <div class="content" v-html="announcement.content"></div>
        </div>
        
        <div class="no-data" v-else-if="!loading">
          <el-empty description="公告不存在或已被删除"></el-empty>
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
  name: "AnnouncementDetail",
  components: {
    AppHead,
    AppBody,
    AppFoot
  },
  data() {
    return {
      loading: false,
      announcement: null,
      id: null
    };
  },
  created() {
    this.id = this.$route.params.id;
    if (this.id) {
      this.fetchAnnouncementDetail();
    }
  },
  methods: {
    fetchAnnouncementDetail() {
      this.loading = true;
      this.$api.getAnnouncementDetail({ id: this.id })
        .then(res => {
          this.loading = false;
          if (res.status_code === 1) {
            this.announcement = res.data;
          } else {
            this.$message.error(res.msg || '获取公告详情失败');
          }
        })
        .catch(err => {
          this.loading = false;
          console.error("获取公告详情失败:", err);
          this.$message.error('获取公告详情失败');
        });
    },
    formatDate(timestamp) {
      if (!timestamp) return '';
      const date = new Date(timestamp * 1000);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
    }
  }
};
</script>

<style scoped>
.announcement-detail {
  background-color: #f6f6f6;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.detail-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.breadcrumb {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.announcement-content {
  padding: 20px 0;
}

.title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 15px;
}

.meta {
  margin-bottom: 20px;
  color: #909399;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.time {
  margin-right: 10px;
}

.content {
  font-size: 16px;
  line-height: 1.8;
  color: #606266;
}

.no-data {
  padding: 40px 0;
  text-align: center;
}
</style> 