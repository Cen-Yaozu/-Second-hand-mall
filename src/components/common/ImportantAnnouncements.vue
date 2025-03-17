<template>
  <div class="important-announcements">
    <div class="announcements-header">
      <div class="announcements-title">
        <i class="el-icon-bell announcements-icon"></i>
        <span>重要公告</span>
      </div>
      <router-link to="/announcements" class="view-all">查看全部</router-link>
    </div>
    <div v-if="importantAnnouncements.length > 0">
      <ul class="announcements-list">
        <li v-for="item in importantAnnouncements.slice(0, 3)" :key="item.id" class="announcement-item" @click="viewAnnouncement(item.id)">
          {{ item.title }}
        </li>
      </ul>
    </div>
    <div class="no-data" v-else>
      暂无重要公告
    </div>
  </div>
</template>

<script>
export default {
  name: "ImportantAnnouncements",
  data() {
    return {
      importantAnnouncements: []
    };
  },
  created() {
    this.fetchImportantAnnouncements();
  },
  methods: {
    fetchImportantAnnouncements() {
      this.$api.getImportantAnnouncements({
        limit: 5
      })
      .then(res => {
        if (res.status_code === 1) {
          this.importantAnnouncements = res.data.list || [];
        }
      })
      .catch(err => {
        console.error("获取重要公告失败:", err);
      });
    },
    formatDate(timestamp) {
      if (!timestamp) return '';
      const date = new Date(timestamp);
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
    },
    viewAnnouncement(id) {
      this.$router.push(`/announcement/${id}`);
    }
  }
};
</script>

<style scoped>
.important-announcements {
  margin: 0 0 10px 0;
  padding: 5px 10px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.announcements-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 3px;
  padding-bottom: 3px;
  border-bottom: 1px solid #f0f0f0;
}

.announcements-title {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.announcements-icon {
  margin-right: 5px;
  color: #f56c6c;
}

.view-all {
  font-size: 12px;
  color: #409eff;
  cursor: pointer;
}

.announcements-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.announcement-item {
  padding: 3px 0;
  border-bottom: 1px dashed #f0f0f0;
  cursor: pointer;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.announcement-item:last-child {
  border-bottom: none;
}

.announcement-item:hover {
  color: #409eff;
}

.no-data {
  text-align: center;
  color: #999;
  padding: 8px 0;
}
</style> 