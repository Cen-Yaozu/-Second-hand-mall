<template>
  <div class="announcement-management">
    <div class="management-header">
      <h3>公告管理</h3>
      <el-button type="primary" @click="createAnnouncement">创建公告</el-button>
    </div>
    
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="全部公告" name="all"></el-tab-pane>
      <el-tab-pane label="已发布" name="published"></el-tab-pane>
      <el-tab-pane label="草稿" name="draft"></el-tab-pane>
    </el-tabs>
    
    <el-table
      :data="announcementList"
      style="width: 100%"
      v-loading="loading"
      border>
      <el-table-column
        prop="id"
        label="ID"
        width="80">
      </el-table-column>
      <el-table-column
        prop="title"
        label="标题"
        min-width="200">
      </el-table-column>
      <el-table-column
        label="状态"
        width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        label="重要"
        width="80">
        <template slot-scope="scope">
          <el-tag type="danger" v-if="scope.row.important === 1">重要</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="create_time"
        label="创建时间"
        width="180">
        <template slot-scope="scope">
          {{ formatDate(scope.row.create_time) }}
        </template>
      </el-table-column>
      <el-table-column
        prop="publish_time"
        label="发布时间"
        width="180">
        <template slot-scope="scope">
          {{ scope.row.publish_time ? formatDate(scope.row.publish_time) : '-' }}
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            @click="viewAnnouncement(scope.row.id)">查看</el-button>
          <el-button
            size="mini"
            type="primary"
            @click="editAnnouncement(scope.row.id)">编辑</el-button>
          <el-button
            size="mini"
            type="danger"
            @click="deleteAnnouncement(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination-container">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 30, 50]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </div>
    
    <!-- 公告详情对话框 -->
    <el-dialog title="公告详情" :visible.sync="dialogVisible" width="60%">
      <div v-if="currentAnnouncement">
        <h2>{{ currentAnnouncement.title }}</h2>
        <div class="announcement-meta">
          <span>状态: {{ getStatusText(currentAnnouncement.status) }}</span>
          <span>创建时间: {{ formatDate(currentAnnouncement.create_time) }}</span>
          <span v-if="currentAnnouncement.publish_time">发布时间: {{ formatDate(currentAnnouncement.publish_time) }}</span>
        </div>
        <div class="announcement-content" v-html="currentAnnouncement.content"></div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="editAnnouncement(currentAnnouncement.id)">编辑</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "AnnouncementManagement",
  data() {
    return {
      activeTab: 'all',
      announcementList: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      currentAnnouncement: null
    };
  },
  created() {
    this.fetchAnnouncementList();
  },
  methods: {
    fetchAnnouncementList() {
      this.loading = true;
      const params = {
        page: this.currentPage,
        limit: this.pageSize
      };
      
      // 根据选中的标签筛选状态
      if (this.activeTab === 'published') {
        params.status = 1; // 已发布
      } else if (this.activeTab === 'draft') {
        params.status = 0; // 草稿
      }
      
      this.$api.getAnnouncementList(params)
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
    
    handleTabClick() {
      this.currentPage = 1;
      this.fetchAnnouncementList();
    },
    
    handleSizeChange(val) {
      this.pageSize = val;
      this.fetchAnnouncementList();
    },
    
    handleCurrentChange(val) {
      this.currentPage = val;
      this.fetchAnnouncementList();
    },
    
    createAnnouncement() {
      this.$router.push('/admin/announcement/create');
    },
    
    viewAnnouncement(id) {
      this.loading = true;
      this.$api.getAnnouncementDetail({ id })
        .then(res => {
          this.loading = false;
          if (res.status_code === 1) {
            this.currentAnnouncement = res.data;
            this.dialogVisible = true;
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
    
    editAnnouncement(id) {
      this.$router.push(`/admin/announcement/edit/${id}`);
    },
    
    deleteAnnouncement(id) {
      this.$confirm('确定要删除该公告吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true;
        this.$api.deleteAnnouncement({ id })
          .then(res => {
            this.loading = false;
            if (res.status_code === 1) {
              this.$message.success('删除成功');
              this.fetchAnnouncementList();
            } else {
              this.$message.error(res.msg || '删除失败');
            }
          })
          .catch(err => {
            this.loading = false;
            console.error("删除公告失败:", err);
            this.$message.error('删除失败');
          });
      }).catch(() => {
        // 取消删除
      });
    },
    
    getStatusType(status) {
      switch (status) {
        case 0: return 'info';    // 草稿
        case 1: return 'success'; // 已发布
        case 2: return 'warning'; // 已过期
        default: return 'info';
      }
    },
    
    getStatusText(status) {
      switch (status) {
        case 0: return '草稿';
        case 1: return '已发布';
        case 2: return '已过期';
        default: return '未知';
      }
    },
    
    formatDate(timestamp) {
      if (!timestamp) return '-';
      const date = new Date(timestamp * 1000);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
    }
  }
};
</script>

<style scoped>
.announcement-management {
  padding: 20px;
}

.management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.management-header h3 {
  margin: 0;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.announcement-meta {
  margin: 10px 0 20px;
  color: #606266;
  font-size: 14px;
}

.announcement-meta span {
  margin-right: 15px;
}

.announcement-content {
  margin-top: 20px;
  line-height: 1.6;
}
</style> 