<template>
  <div>
    <app-head></app-head>
    <app-body>
      <div class="announcement-container">
        <div class="announcement-header">
          <h2>系统公告</h2>
          <div class="announcement-filter">
            <el-select v-model="selectedType" placeholder="公告类型" @change="handleTypeChange">
              <el-option label="全部公告" :value="null"></el-option>
              <el-option label="系统更新" :value="1"></el-option>
              <el-option label="活动通知" :value="2"></el-option>
              <el-option label="规则变更" :value="3"></el-option>
            </el-select>
          </div>
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
              <div class="announcement-title" @click="viewDetail(scope.row.id)">
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
            prop="publishTime"
            label="发布时间"
            width="180">
            <template slot-scope="scope">
              {{ formatDate(scope.row.publishTime) }}
            </template>
          </el-table-column>
          <el-table-column
            label="操作"
            width="120">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="viewDetail(scope.row.id)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="pagination-container">
          <el-pagination
            @current-change="handleCurrentChange"
            :current-page.sync="currentPage"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="total">
          </el-pagination>
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
  name: 'AnnouncementList',
  components: {
    AppHead,
    AppBody,
    AppFoot
  },
  data() {
    return {
      loading: false,
      announcementList: [],
      selectedType: null,
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
      const params = {
        page: this.currentPage,
        size: this.pageSize
      };
      
      if (this.selectedType !== null) {
        params.type = this.selectedType;
      }
      
      this.$api.getAnnouncementList(params).then(res => {
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
    handleCurrentChange(page) {
      this.currentPage = page;
      this.fetchAnnouncementList();
    },
    handleTypeChange() {
      this.currentPage = 1;
      this.fetchAnnouncementList();
    },
    viewDetail(id) {
      this.$router.push(`/announcement/detail/${id}`);
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
    }
  }
};
</script>

<style scoped>
.announcement-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  min-height: 70vh;
}

.announcement-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.announcement-filter {
  width: 150px;
}

.announcement-title {
  color: #409EFF;
  cursor: pointer;
  font-weight: 500;
}

.announcement-title:hover {
  text-decoration: underline;
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
  text-align: center;
}
</style> 