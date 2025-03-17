<template>
  <div>
    <div class="admin-container">
      <div class="admin-title">举报管理</div>
      
      <div class="filter-container">
        <el-radio-group v-model="statusFilter" @change="getReportList">
          <el-radio-button :label="-1">全部</el-radio-button>
          <el-radio-button :label="0">待处理</el-radio-button>
          <el-radio-button :label="1">已处理</el-radio-button>
          <el-radio-button :label="2">已驳回</el-radio-button>
        </el-radio-group>
      </div>
      
      <div v-loading="loading">
        <el-table
          :data="reportList"
          stripe
          style="width: 100%"
          @row-click="handleRowClick">
          <el-table-column
            prop="id"
            label="ID"
            width="70">
          </el-table-column>
          <el-table-column
            label="举报类型"
            width="100">
            <template slot-scope="scope">
              <el-tag :type="getReportTypeTag(scope.row.reportType).type">
                {{ getReportTypeTag(scope.row.reportType).name }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column
            label="举报人"
            width="150">
            <template slot-scope="scope">
              <div v-if="scope.row.reporter">{{ scope.row.reporter.nickname }}</div>
              <div v-else>未知用户</div>
            </template>
          </el-table-column>
          <el-table-column
            label="被举报用户"
            width="150">
            <template slot-scope="scope">
              <div v-if="scope.row.reportedUser">{{ scope.row.reportedUser.nickname }}</div>
              <div v-else>未知用户</div>
            </template>
          </el-table-column>
          <el-table-column
            label="被举报商品"
            width="180">
            <template slot-scope="scope">
              <div v-if="scope.row.reportedItem">{{ scope.row.reportedItem.idleName }}</div>
              <div v-else>无</div>
            </template>
          </el-table-column>
          <el-table-column
            prop="reportReason"
            label="举报理由"
            show-overflow-tooltip>
          </el-table-column>
          <el-table-column
            label="举报时间"
            width="150">
            <template slot-scope="scope">
              {{ formatDate(scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column
            label="状态"
            width="100">
            <template slot-scope="scope">
              <el-tag :type="getReportStatusTag(scope.row.status).type">
                {{ getReportStatusTag(scope.row.status).name }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column
            label="操作"
            width="180"
            fixed="right">
            <template slot-scope="scope">
              <el-button
                v-if="scope.row.status === 0"
                size="mini"
                type="success"
                @click.stop="handleReport(scope.row, 1)">处理</el-button>
              <el-button
                v-if="scope.row.status === 0"
                size="mini"
                type="danger"
                @click.stop="handleReport(scope.row, 2)">驳回</el-button>
              <el-button
                size="mini"
                type="primary"
                @click.stop="viewDetail(scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    
    <!-- 详情对话框 -->
    <el-dialog
      title="举报详情"
      :visible.sync="detailDialogVisible"
      width="60%">
      <div v-if="currentReport" class="report-detail">
        <div class="detail-item">
          <span class="label">举报ID：</span>
          <span>{{ currentReport.id }}</span>
        </div>
        <div class="detail-item">
          <span class="label">举报类型：</span>
          <el-tag :type="getReportTypeTag(currentReport.reportType).type">
            {{ getReportTypeTag(currentReport.reportType).name }}
          </el-tag>
        </div>
        <div class="detail-item">
          <span class="label">举报人：</span>
          <span v-if="currentReport.reporter">{{ currentReport.reporter.nickname }} (ID: {{ currentReport.reporter.id }})</span>
        </div>
        <div class="detail-item">
          <span class="label">被举报用户：</span>
          <span v-if="currentReport.reportedUser">{{ currentReport.reportedUser.nickname }} (ID: {{ currentReport.reportedUser.id }})</span>
        </div>
        <div class="detail-item" v-if="currentReport.reportedItem">
          <span class="label">被举报商品：</span>
          <span>{{ currentReport.reportedItem.idleName }} (ID: {{ currentReport.reportedItem.id }})</span>
        </div>
        <div class="detail-item">
          <span class="label">举报理由：</span>
          <div class="detail-content">{{ currentReport.reportReason }}</div>
        </div>
        <div class="detail-item">
          <span class="label">举报时间：</span>
          <span>{{ formatDate(currentReport.createTime) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">状态：</span>
          <el-tag :type="getReportStatusTag(currentReport.status).type">
            {{ getReportStatusTag(currentReport.status).name }}
          </el-tag>
        </div>
        <div class="detail-item" v-if="currentReport.status > 0">
          <span class="label">处理结果：</span>
          <div class="detail-content">{{ currentReport.handleResult || '无' }}</div>
        </div>
        
        <div class="detail-item" v-if="currentReport.evidenceUrls">
          <span class="label">举证材料：</span>
          <div class="evidence-list">
            <el-image
              v-for="(url, index) in getEvidenceUrlList(currentReport.evidenceUrls)"
              :key="index"
              :src="url"
              :preview-src-list="getEvidenceUrlList(currentReport.evidenceUrls)"
              fit="cover"
              class="evidence-image">
            </el-image>
          </div>
        </div>
      </div>
    </el-dialog>
    
    <!-- 处理对话框 -->
    <el-dialog
      :title="handleDialogTitle"
      :visible.sync="handleDialogVisible"
      width="50%">
      <el-form :model="handleForm" ref="handleForm" :rules="handleRules">
        <el-form-item label="处理结果" prop="handleResult" label-width="100px">
          <el-input
            type="textarea"
            :rows="4"
            placeholder="请输入处理结果说明"
            v-model="handleForm.handleResult">
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitHandle" :loading="submitting">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'report-management',
  data() {
    return {
      statusFilter: -1,
      loading: false,
      reportList: [],
      detailDialogVisible: false,
      handleDialogVisible: false,
      currentReport: null,
      handleForm: {
        id: null,
        status: 0,
        handleResult: ''
      },
      handleRules: {
        handleResult: [
          { required: true, message: '处理结果不能为空', trigger: 'blur' },
          { min: 5, max: 500, message: '处理结果长度应在5到500个字符之间', trigger: 'blur' }
        ]
      },
      submitting: false
    };
  },
  computed: {
    handleDialogTitle() {
      return this.handleForm.status === 1 ? '处理举报' : '驳回举报';
    }
  },
  created() {
    this.getReportList();
  },
  methods: {
    getReportList() {
      this.loading = true;
      const params = {};
      if (this.statusFilter !== -1) {
        params.status = this.statusFilter;
      }
      
      this.$api.getAdminReportList(params).then(res => {
        this.loading = false;
        if (res.status_code === 1) {
          this.reportList = res.data || [];
        } else {
          this.$message.error(res.msg || '获取举报列表失败');
        }
      }).catch(() => {
        this.loading = false;
        this.$message.error('网络错误，请稍后重试');
      });
    },
    getReportTypeTag(type) {
      const types = {
        1: { name: '虚假信息', type: 'warning' },
        2: { name: '违禁物品', type: 'danger' },
        3: { name: '欺诈行为', type: 'danger' },
        4: { name: '其他', type: 'info' }
      };
      return types[type] || { name: '未知', type: 'info' };
    },
    getReportStatusTag(status) {
      const statuses = {
        0: { name: '待处理', type: '' },
        1: { name: '已处理', type: 'success' },
        2: { name: '已驳回', type: 'danger' }
      };
      return statuses[status] || { name: '未知', type: 'info' };
    },
    formatDate(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
    },
    getEvidenceUrlList(urls) {
      if (!urls) return [];
      return urls.split(',').map(url => {
        // 如果URL不是以http开头，则添加基本URL
        if (!url.startsWith('http')) {
          return `http://localhost:8082/image?imageName=${url}`;
        }
        return url;
      });
    },
    handleRowClick(row) {
      this.viewDetail(row);
    },
    viewDetail(report) {
      this.currentReport = report;
      this.detailDialogVisible = true;
    },
    handleReport(report, status) {
      this.currentReport = report;
      this.handleForm.id = report.id;
      this.handleForm.status = status;
      this.handleForm.handleResult = '';
      this.handleDialogVisible = true;
    },
    submitHandle() {
      this.$refs.handleForm.validate(valid => {
        if (valid) {
          this.submitting = true;
          
          const apiMethod = this.handleForm.status === 1 
            ? this.$api.adminHandleReport
            : this.$api.adminRejectReport;
          
          apiMethod({
            id: this.handleForm.id,
            status: this.handleForm.status,
            handleResult: this.handleForm.handleResult
          }).then(res => {
            this.submitting = false;
            if (res.status_code === 1) {
              this.$message.success(res.msg || '操作成功');
              this.handleDialogVisible = false;
              this.getReportList();
            } else {
              this.$message.error(res.msg || '操作失败');
            }
          }).catch(() => {
            this.submitting = false;
            this.$message.error('网络错误，请稍后重试');
          });
        }
      });
    }
  }
};
</script>

<style scoped>
.admin-container {
  padding: 20px;
}
.admin-title {
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 20px;
}
.filter-container {
  margin-bottom: 20px;
}
.report-detail {
  padding: 10px;
}
.detail-item {
  margin-bottom: 15px;
}
.label {
  font-weight: bold;
  margin-right: 10px;
  color: #606266;
}
.detail-content {
  margin-top: 5px;
  padding: 10px;
  background-color: #f8f8f8;
  border-radius: 4px;
  white-space: pre-wrap;
  line-height: 1.5;
}
.evidence-list {
  display: flex;
  flex-wrap: wrap;
  margin-top: 10px;
}
.evidence-image {
  width: 120px;
  height: 120px;
  margin-right: 10px;
  margin-bottom: 10px;
  border-radius: 4px;
  object-fit: cover;
}
</style> 