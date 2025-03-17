<template>
  <div>
    <app-header></app-header>
    <app-page-body>
      <div class="my-reports-container">
        <div class="page-title">
          <h2>我的举报</h2>
        </div>
        
        <el-tabs v-model="activeTab" @tab-click="handleTabClick">
          <el-tab-pane label="全部" name="all"></el-tab-pane>
          <el-tab-pane label="待处理" name="pending"></el-tab-pane>
          <el-tab-pane label="已通过" name="approved"></el-tab-pane>
          <el-tab-pane label="已拒绝" name="rejected"></el-tab-pane>
        </el-tabs>
        
        <div class="reports-list" v-loading="loading">
          <div v-if="reportsList.length === 0" class="empty-data">
            <i class="el-icon-document"></i>
            <p>暂无举报记录</p>
          </div>
          
          <el-card v-for="item in reportsList" :key="item.id" class="report-card">
            <div class="report-header">
              <span class="report-id">举报编号：{{ item.id }}</span>
              <el-tag :type="getStatusType(item.status)" size="medium">
                {{ getStatusText(item.status) }}
              </el-tag>
            </div>
            
            <div class="report-content">
              <div class="report-item">
                <span class="label">被举报商品：</span>
                <el-link type="primary" @click="viewItem(item.reportedItemId)">
                  {{ item.reportedItemName }}
                </el-link>
              </div>
              
              <div class="report-item">
                <span class="label">被举报用户：</span>
                <span>{{ item.reportedUserName }}</span>
              </div>
              
              <div class="report-item">
                <span class="label">举报原因：</span>
                <span>{{ item.reportReason }}</span>
              </div>
              
              <div class="report-item">
                <span class="label">举报时间：</span>
                <span>{{ item.createTime }}</span>
              </div>
              
              <div class="report-item" v-if="item.status !== 0">
                <span class="label">处理结果：</span>
                <span>{{ item.handleReason || '无' }}</span>
              </div>
              
              <div class="report-item" v-if="item.status !== 0">
                <span class="label">处理时间：</span>
                <span>{{ item.handleTime || '无' }}</span>
              </div>
            </div>
            
            <div class="report-footer">
              <el-button 
                size="small" 
                type="danger" 
                plain 
                v-if="item.status === 0"
                @click="cancelReport(item.id)"
              >
                取消举报
              </el-button>
              <el-button 
                size="small" 
                type="primary" 
                @click="viewDetail(item)"
              >
                查看详情
              </el-button>
            </div>
          </el-card>
          
          <div class="pagination" v-if="reportsList.length > 0">
            <el-pagination
              @current-change="handleCurrentChange"
              :current-page.sync="currentPage"
              :page-size="pageSize"
              layout="prev, pager, next, jumper"
              :total="total">
            </el-pagination>
          </div>
        </div>
      </div>
    </app-page-body>
    
    <!-- 举报详情对话框 -->
    <el-dialog
      title="举报详情"
      :visible.sync="detailDialogVisible"
      width="50%"
    >
      <div v-if="currentReport">
        <div class="detail-item">
          <span class="detail-label">举报ID：</span>
          <span>{{ currentReport.id }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">被举报商品：</span>
          <el-link type="primary" @click="viewItem(currentReport.reportedItemId)">
            {{ currentReport.reportedItemName }}
          </el-link>
        </div>
        <div class="detail-item">
          <span class="detail-label">被举报用户：</span>
          <span>{{ currentReport.reportedUserName }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">举报原因：</span>
          <span>{{ currentReport.reportReason }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">举报时间：</span>
          <span>{{ currentReport.createTime }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">状态：</span>
          <el-tag :type="getStatusType(currentReport.status)">
            {{ getStatusText(currentReport.status) }}
          </el-tag>
        </div>
        <div class="detail-item" v-if="currentReport.status !== 0">
          <span class="detail-label">处理结果：</span>
          <span>{{ currentReport.handleReason || '无' }}</span>
        </div>
        <div class="detail-item" v-if="currentReport.status !== 0">
          <span class="detail-label">处理时间：</span>
          <span>{{ currentReport.handleTime || '无' }}</span>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button 
          v-if="currentReport && currentReport.status === 0" 
          type="danger" 
          @click="cancelReport(currentReport.id)"
        >取消举报</el-button>
      </span>
    </el-dialog>
    
    <app-foot></app-foot>
  </div>
</template>

<script>
import AppHeader from '../common/AppHeader.vue'
import AppPageBody from '../common/AppPageBody.vue'
import AppFoot from '../common/AppFoot.vue'

export default {
  name: 'my-reports',
  components: {
    AppHeader,
    AppPageBody,
    AppFoot
  },
  data() {
    return {
      activeTab: 'all',
      loading: false,
      reportsList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      detailDialogVisible: false,
      currentReport: null
    }
  },
  created() {
    this.fetchReports()
  },
  methods: {
    fetchReports() {
      if (!this.$sta.isLogin) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      
      this.loading = true
      
      this.$api.getMyReports()
        .then(res => {
          if (res.status_code === 1) {
            this.reportsList = res.data.list || []
            this.total = res.data.total || 0
            
            // 根据标签筛选
            if (this.activeTab !== 'all') {
              this.filterReportsByTab()
            }
          } else {
            this.$message.error(res.msg || '获取举报列表失败')
          }
        })
        .catch(err => {
          console.error(err)
          this.$message.error('获取举报列表失败')
        })
        .finally(() => {
          this.loading = false
        })
    },
    handleTabClick() {
      this.currentPage = 1
      if (this.activeTab === 'all') {
        this.fetchReports()
      } else {
        this.filterReportsByTab()
      }
    },
    filterReportsByTab() {
      const statusMap = {
        'pending': 0,
        'approved': 1,
        'rejected': 2
      }
      
      if (this.activeTab !== 'all') {
        const status = statusMap[this.activeTab]
        this.reportsList = this.reportsList.filter(item => item.status === status)
      }
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchReports()
    },
    getStatusType(status) {
      const types = {
        0: 'warning',
        1: 'success',
        2: 'danger'
      }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = {
        0: '待处理',
        1: '已通过',
        2: '已拒绝'
      }
      return texts[status] || '未知'
    },
    viewDetail(report) {
      this.currentReport = report
      this.detailDialogVisible = true
    },
    viewItem(itemId) {
      if (itemId) {
        this.$router.push(`/details?id=${itemId}`)
      }
    },
    cancelReport(reportId) {
      this.$confirm('确定要取消此举报吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$api.cancelReport({ reportId })
          .then(res => {
            if (res.status_code === 1) {
              this.$message.success('取消举报成功')
              this.detailDialogVisible = false
              this.fetchReports()
            } else {
              this.$message.error(res.msg || '取消举报失败')
            }
          })
          .catch(err => {
            console.error(err)
            this.$message.error('取消举报失败')
          })
      }).catch(() => {
        // 取消操作
      })
    }
  }
}
</script>

<style scoped>
.my-reports-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
}

.page-title h2 {
  font-size: 24px;
  font-weight: 500;
  color: #303133;
}

.empty-data {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 50px 0;
  color: #909399;
}

.empty-data i {
  font-size: 48px;
  margin-bottom: 20px;
}

.report-card {
  margin-bottom: 20px;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.report-id {
  color: #606266;
  font-size: 14px;
}

.report-content {
  margin-bottom: 15px;
}

.report-item {
  margin-bottom: 10px;
  line-height: 22px;
}

.label {
  display: inline-block;
  width: 100px;
  color: #606266;
  font-weight: bold;
}

.report-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 15px;
  padding-top: 10px;
  border-top: 1px solid #ebeef5;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}

.detail-item {
  margin-bottom: 15px;
  line-height: 24px;
}

.detail-label {
  display: inline-block;
  width: 100px;
  text-align: right;
  margin-right: 10px;
  font-weight: bold;
  color: #606266;
}
</style> 