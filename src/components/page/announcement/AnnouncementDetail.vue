<template>
  <div>
    <app-head></app-head>
    <app-body>
      <div class="announcement-detail-container" v-loading="loading">
        <div v-if="announcement">
          <div class="announcement-header">
            <el-page-header @back="goBack" content="公告详情"></el-page-header>
          </div>
          
          <div class="announcement-title">
            <span v-if="announcement.isImportant" class="important-tag">重要</span>
            <h2>{{ announcement.title }}</h2>
          </div>
          
          <div class="announcement-meta">
            <el-tag :type="getTypeTag(announcement.type)">{{ getTypeName(announcement.type) }}</el-tag>
            <span class="announcement-time">发布时间：{{ formatDate(announcement.publishTime) }}</span>
          </div>
          
          <div class="announcement-content">
            <div v-html="formatContent(announcement.content)"></div>
          </div>
          
          <div class="announcement-footer" v-if="announcement.expireTime">
            <p class="expire-info">公告有效期至：{{ formatDate(announcement.expireTime) }}</p>
          </div>
        </div>
        
        <div v-else-if="!loading" class="announcement-error">
          <el-empty description="公告不存在或已过期"></el-empty>
          <el-button type="primary" @click="goBack">返回公告列表</el-button>
        </div>
      </div>
      <app-foot></app-foot>
    </app-body>
  </div>
</template>

<script>
import AppHead from '../../common/AppHeader.vue';
import AppBody from '../../common/AppPageBody.vue';
import AppFoot from '../../common/AppFoot.vue';

export default {
  name: 'AnnouncementDetail',
  components: {
    AppHead,
    AppBody,
    AppFoot
  },
  data() {
    return {
      loading: false,
      announcement: null
    };
  },
  created() {
    const id = this.$route.params.id;
    if (id) {
      this.fetchAnnouncementDetail(id);
    }
  },
  methods: {
    fetchAnnouncementDetail(id) {
      this.loading = true;
      this.$api.getAnnouncementDetail(id).then(res => {
        this.loading = false;
        if (res.status_code === 1) {
          this.announcement = res.data;
        } else {
          this.$message.error('获取公告详情失败');
        }
      }).catch(() => {
        this.loading = false;
        this.$message.error('获取公告详情失败');
      });
    },
    goBack() {
      this.$router.push('/announcement/list');
    },
    getTypeName(type) {
      switch (type) {
        case 1: return '系统更新';
        case 2: return '活动通知';
        case 3: return '规则变更';
        default: return '未知类型';
      }
    },
    getTypeTag(type) {
      switch (type) {
        case 1: return 'primary';
        case 2: return 'success';
        case 3: return 'warning';
        default: return 'info';
      }
    },
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
    },
    formatContent(content) {
      if (!content) return '';
      // 将普通文本中的换行符转换为HTML换行标签
      return content.replace(/\n/g, '<br>');
    }
  }
};
</script>

<style scoped>
.announcement-detail-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  min-height: 70vh;
}

.announcement-header {
  margin-bottom: 20px;
}

.announcement-title {
  margin: 20px 0;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 15px;
}

.announcement-title h2 {
  display: inline-block;
  margin: 0;
  font-size: 22px;
  color: #303133;
}

.announcement-meta {
  margin-bottom: 20px;
  color: #909399;
}

.announcement-time {
  margin-left: 10px;
}

.announcement-content {
  padding: 20px 0;
  min-height: 300px;
  line-height: 1.8;
  color: #303133;
  font-size: 16px;
}

.important-tag {
  display: inline-block;
  padding: 2px 5px;
  background-color: #F56C6C;
  color: white;
  border-radius: 3px;
  font-size: 12px;
  margin-right: 5px;
  vertical-align: middle;
}

.announcement-footer {
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

.expire-info {
  color: #909399;
  font-size: 14px;
}

.announcement-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 0;
}
</style> 