<template>
  <div class="admin-announcement-list">
    <div class="admin-announcement-header">
      <div class="header-title">公告管理</div>
      <div class="header-actions">
        <el-button type="primary" icon="el-icon-plus" @click="createAnnouncement">新建公告</el-button>
      </div>
    </div>

    <div class="filter-container">
      <el-select v-model="queryParams.type" placeholder="公告类型" clearable class="filter-item">
        <el-option label="全部类型" :value="null"></el-option>
        <el-option label="系统更新" :value="1"></el-option>
        <el-option label="活动通知" :value="2"></el-option>
        <el-option label="规则变更" :value="3"></el-option>
      </el-select>
      
      <el-select v-model="queryParams.status" placeholder="公告状态" clearable class="filter-item">
        <el-option label="全部状态" :value="null"></el-option>
        <el-option label="草稿" :value="0"></el-option>
        <el-option label="已发布" :value="1"></el-option>
        <el-option label="已过期" :value="2"></el-option>
      </el-select>
      
      <el-select v-model="queryParams.isImportant" placeholder="重要程度" clearable class="filter-item">
        <el-option label="全部" :value="null"></el-option>
        <el-option label="重要" :value="true"></el-option>
        <el-option label="普通" :value="false"></el-option>
      </el-select>
      
      <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
      <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
    </div>

    <el-table
      v-loading="loading"
      :data="announcementList"
      style="width: 100%">
      <el-table-column
        prop="title"
        label="标题"
        min-width="200">
        <template slot-scope="scope">
          <div class="announcement-title">
            <span v-if="scope.row.isImportant" class="important-tag">重要</span>
            {{ scope.row.title }}
          </div>
        </template>
      </el-table-column>
      
      <el-table-column
        prop="type"
        label="类型"
        width="120">
        <template slot-scope="scope">
          <el-tag :type="getTypeTag(scope.row.type)">{{ getTypeName(scope.row.type) }}</el-tag>
        </template>
      </el-table-column>
      
      <el-table-column
        prop="status"
        label="状态"
        width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusTag(scope.row.status)">{{ getStatusName(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      
      <el-table-column
        prop="createTime"
        label="创建时间"
        width="160">
        <template slot-scope="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      
      <el-table-column
        prop="publishTime"
        label="发布时间"
        width="160">
        <template slot-scope="scope">
          {{ formatDate(scope.row.publishTime) || '未发布' }}
        </template>
      </el-table-column>
      
      <el-table-column
        label="操作"
        width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="viewAnnouncement(scope.row)">查看</el-button>
          <el-button size="mini" type="text" @click="editAnnouncement(scope.row)">编辑</el-button>
          <el-button 
            v-if="scope.row.status === 0" 
            size="mini" 
            type="text" 
            @click="publishAnnouncement(scope.row)">发布</el-button>
          <el-button 
            size="mini" 
            type="text" 
            class="danger-btn" 
            @click="deleteAnnouncement(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination
        @current-change="handleCurrentChange"
        :current-page.sync="queryParams.page"
        :page-size="queryParams.size"
        layout="total, prev, pager, next"
        :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AdminAnnouncementList',
  data() {
    return {
      loading: false,
      announcementList: [],
      total: 0,
      queryParams: {
        page: 1,
        size: 10,
        type: null,
        status: null,
        isImportant: null
      }
    };
  },
  created() {
    this.fetchAnnouncementList();
  },
  methods: {
    fetchAnnouncementList() {
      this.loading = true;
      // 构建请求参数，移除值为null的参数
      const params = {};
      Object.keys(this.queryParams).forEach(key => {
        if (this.queryParams[key] !== null) {
          params[key] = this.queryParams[key];
        }
      });
      
      this.$api.adminGetAnnouncementList(params).then(res => {
        this.loading = false;
        if (res.status_code === 1) {
          this.announcementList = res.data || [];
          this.total = res.total || this.announcementList.length;
        } else {
          this.$message.error('获取公告列表失败');
        }
      }).catch(() => {
        this.loading = false;
        this.$message.error('获取公告列表失败');
      });
    },
    createAnnouncement() {
      this.$router.push('/admin/announcement/create');
    },
    viewAnnouncement(row) {
      this.$router.push(`/admin/announcement/detail/${row.id}`);
    },
    editAnnouncement(row) {
      this.$router.push(`/admin/announcement/edit/${row.id}`);
    },
    publishAnnouncement(row) {
      this.$confirm('确认发布此公告?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$api.adminPublishAnnouncement(row.id).then(res => {
          if (res.status_code === 1) {
            this.$message.success('公告发布成功');
            this.fetchAnnouncementList();
          } else {
            this.$message.error('公告发布失败');
          }
        }).catch(() => {
          this.$message.error('公告发布失败');
        });
      }).catch(() => {});
    },
    deleteAnnouncement(row) {
      this.$confirm('确认删除此公告? 删除后无法恢复!', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        this.$api.adminDeleteAnnouncement(row.id).then(res => {
          if (res.status_code === 1) {
            this.$message.success('公告删除成功');
            this.fetchAnnouncementList();
          } else {
            this.$message.error('公告删除失败');
          }
        }).catch(() => {
          this.$message.error('公告删除失败');
        });
      }).catch(() => {});
    },
    handleSearch() {
      this.queryParams.page = 1;
      this.fetchAnnouncementList();
    },
    resetQuery() {
      this.queryParams = {
        page: 1,
        size: 10,
        type: null,
        status: null,
        isImportant: null
      };
      this.fetchAnnouncementList();
    },
    handleCurrentChange(page) {
      this.queryParams.page = page;
      this.fetchAnnouncementList();
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
    getStatusName(status) {
      switch (status) {
        case 0: return '草稿';
        case 1: return '已发布';
        case 2: return '已过期';
        default: return '未知';
      }
    },
    getStatusTag(status) {
      switch (status) {
        case 0: return 'info';
        case 1: return 'success';
        case 2: return 'danger';
        default: return '';
      }
    },
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
    }
  }
};
</script>

<style scoped>
.admin-announcement-list {
  padding: 20px;
}

.admin-announcement-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-title {
  font-size: 20px;
  font-weight: bold;
}

.filter-container {
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
}

.filter-item {
  margin-right: 10px;
  margin-bottom: 10px;
  width: 150px;
}

.important-tag {
  display: inline-block;
  padding: 2px 5px;
  background-color: #F56C6C;
  color: white;
  border-radius: 3px;
  font-size: 12px;
  margin-right: 5px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.danger-btn {
  color: #F56C6C;
}
</style> 