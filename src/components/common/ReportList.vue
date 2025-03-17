<template>
  <div class="report-list-container">
    <div class="search-bar">
      <el-input
        placeholder="搜索举报内容"
        v-model="searchValue"
        class="search-input"
        clearable
        @keyup.enter.native="handleSearch"
      >
        <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
      </el-input>
      <el-select v-model="statusFilter" placeholder="状态筛选" @change="handleSearch" clearable>
        <el-option label="待处理" :value="0"></el-option>
        <el-option label="已通过" :value="1"></el-option>
        <el-option label="已拒绝" :value="2"></el-option>
      </el-select>
    </div>

    <el-table
      :data="reportList"
      style="width: 100%"
      v-loading="loading"
      border
    >
      <el-table-column
        prop="id"
        label="ID"
        width="80"
      ></el-table-column>
      <el-table-column
        prop="reporterName"
        label="举报人"
        width="120"
      ></el-table-column>
      <el-table-column
        prop="reportedUserName"
        label="被举报人"
        width="120"
      ></el-table-column>
      <el-table-column
        prop="reportedItemName"
        label="被举报商品"
        width="180"
      >
        <template slot-scope="scope">
          <el-link type="primary" @click="viewItem(scope.row.reportedItemId)">
            {{ scope.row.reportedItemName }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column
        prop="reportReason"
        label="举报原因"
      ></el-table-column>
      <el-table-column
        prop="createTime"
        label="举报时间"
        width="160"
      ></el-table-column>
      <el-table-column
        prop="status"
        label="状态"
        width="100"
      >
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        width="200"
      >
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="primary"
            @click="handleDetail(scope.row)"
            icon="el-icon-view"
          >查看</el-button>
          <el-button
            v-if="scope.row.status === 0"
            size="mini"
            type="success"
            @click="handleApprove(scope.row)"
            icon="el-icon-check"
          >通过</el-button>
          <el-button
            v-if="scope.row.status === 0"
            size="mini"
            type="danger"
            @click="handleReject(scope.row)"
            icon="el-icon-close"
          >拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 30, 50]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      ></el-pagination>
    </div>

    <!-- 举报详情对话框 -->
    <el-dialog
      title="举报详情"
      :visible.sync="detailDialogVisible"
      width="50%"
    >
      <div v-if="currentReport">
        <div class="detail-item">
          <span class="label">举报ID：</span>
          <span>{{ currentReport.id }}</span>
        </div>
        <div class="detail-item">
          <span class="label">举报人：</span>
          <span>{{ currentReport.reporterName }}</span>
        </div>
        <div class="detail-item">
          <span class="label">被举报人：</span>
          <span>{{ currentReport.reportedUserName }}</span>
        </div>
        <div class="detail-item">
          <span class="label">被举报商品：</span>
          <el-link type="primary" @click="viewItem(currentReport.reportedItemId)">
            {{ currentReport.reportedItemName }}
          </el-link>
        </div>
        <div class="detail-item">
          <span class="label">举报原因：</span>
          <span>{{ currentReport.reportReason }}</span>
        </div>
        <div class="detail-item">
          <span class="label">举报时间：</span>
          <span>{{ currentReport.createTime }}</span>
        </div>
        <div class="detail-item">
          <span class="label">状态：</span>
          <el-tag :type="getStatusType(currentReport.status)">
            {{ getStatusText(currentReport.status) }}
          </el-tag>
        </div>
        <div class="detail-item" v-if="currentReport.handleTime">
          <span class="label">处理时间：</span>
          <span>{{ currentReport.handleTime }}</span>
        </div>
        <div class="detail-item" v-if="currentReport.handleReason">
          <span class="label">处理结果：</span>
          <span>{{ currentReport.handleReason }}</span>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button 
          v-if="currentReport && currentReport.status === 0" 
          type="success" 
          @click="handleApprove(currentReport)"
        >通过举报</el-button>
        <el-button 
          v-if="currentReport && currentReport.status === 0" 
          type="danger" 
          @click="handleReject(currentReport)"
        >拒绝举报</el-button>
      </span>
    </el-dialog>

    <!-- 处理举报对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="handleDialogVisible"
      width="40%"
    >
      <el-form :model="handleForm" label-width="100px">
        <el-form-item label="处理原因：">
          <el-input
            type="textarea"
            :rows="4"
            placeholder="请输入处理原因"
            v-model="handleForm.reason"
          ></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="handleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHandle">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "ReportList",
  data() {
    return {
      searchValue: "",
      statusFilter: "",
      reportList: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      detailDialogVisible: false,
      handleDialogVisible: false,
      currentReport: null,
      dialogTitle: "",
      handleForm: {
        reportId: null,
        reason: "",
        action: "" // "approve" 或 "reject"
      }
    };
  },
  created() {
    this.fetchReportList();
  },
  methods: {
    fetchReportList() {
      this.loading = true;
      const query = {
        page: this.currentPage,
        nums: this.pageSize
      };
      
      if (this.searchValue) {
        query.keyword = this.searchValue;
      }
      
      if (this.statusFilter !== "") {
        query.status = this.statusFilter;
      }
      
      this.$api.getAdminReportList(query)
        .then(res => {
          if (res.status_code === 1) {
            this.reportList = res.data.list;
            this.total = res.data.total;
          } else {
            this.$message.error(res.msg || "获取举报列表失败");
          }
        })
        .catch(err => {
          console.error(err);
          this.$message.error("获取举报列表失败");
        })
        .finally(() => {
          this.loading = false;
        });
    },
    handleSearch() {
      this.currentPage = 1;
      this.fetchReportList();
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.fetchReportList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.fetchReportList();
    },
    getStatusType(status) {
      const types = {
        0: "warning",
        1: "success",
        2: "danger"
      };
      return types[status] || "info";
    },
    getStatusText(status) {
      const texts = {
        0: "待处理",
        1: "已通过",
        2: "已拒绝"
      };
      return texts[status] || "未知";
    },
    handleDetail(row) {
      this.currentReport = row;
      this.detailDialogVisible = true;
    },
    handleApprove(row) {
      this.currentReport = row;
      this.handleForm.reportId = row.id;
      this.handleForm.reason = "";
      this.handleForm.action = "approve";
      this.dialogTitle = "通过举报";
      this.handleDialogVisible = true;
    },
    handleReject(row) {
      this.currentReport = row;
      this.handleForm.reportId = row.id;
      this.handleForm.reason = "";
      this.handleForm.action = "reject";
      this.dialogTitle = "拒绝举报";
      this.handleDialogVisible = true;
    },
    submitHandle() {
      if (!this.handleForm.reason) {
        this.$message.warning("请输入处理原因");
        return;
      }
      
      const data = {
        reportId: this.handleForm.reportId,
        handleReason: this.handleForm.reason
      };
      
      this.loading = true;
      
      if (this.handleForm.action === "approve") {
        this.$api.adminHandleReport(data)
          .then(this.handleResponse)
          .catch(this.handleError)
          .finally(() => {
            this.loading = false;
          });
      } else {
        this.$api.adminRejectReport(data)
          .then(this.handleResponse)
          .catch(this.handleError)
          .finally(() => {
            this.loading = false;
          });
      }
    },
    handleResponse(res) {
      if (res.status_code === 1) {
        this.$message.success(res.msg || "处理成功");
        this.handleDialogVisible = false;
        this.detailDialogVisible = false;
        this.fetchReportList();
      } else {
        this.$message.error(res.msg || "处理失败");
      }
    },
    handleError(err) {
      console.error(err);
      this.$message.error("处理失败");
    },
    viewItem(itemId) {
      if (itemId) {
        window.open(`/details?id=${itemId}`, '_blank');
      }
    }
  }
};
</script>

<style scoped>
.report-list-container {
  padding: 20px;
}

.search-bar {
  display: flex;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
  margin-right: 15px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.detail-item {
  margin-bottom: 15px;
  line-height: 24px;
}

.label {
  font-weight: bold;
  margin-right: 10px;
  display: inline-block;
  width: 100px;
  text-align: right;
}
</style> 