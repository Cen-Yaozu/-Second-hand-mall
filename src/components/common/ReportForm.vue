<template>
  <div class="report-form">
    <el-form :model="reportForm" :rules="rules" ref="reportForm" label-width="100px">
      <el-form-item label="举报类型" prop="reportType">
        <el-radio-group v-model="reportForm.reportType">
          <el-radio :label="1">虚假信息</el-radio>
          <el-radio :label="2">违禁物品</el-radio>
          <el-radio :label="3">欺诈行为</el-radio>
          <el-radio :label="4">其他</el-radio>
        </el-radio-group>
      </el-form-item>
      
      <el-form-item label="举报理由" prop="reportReason">
        <el-input
          type="textarea"
          :rows="4"
          placeholder="请详细描述您举报的理由，例如具体的虚假信息、违规内容等"
          v-model="reportForm.reportReason">
        </el-input>
      </el-form-item>
      
      <el-form-item label="举证材料">
        <el-upload
          action="http://localhost:8082/file"
          list-type="picture-card"
          :on-success="handleUploadSuccess"
          :on-remove="handleUploadRemove"
          :before-upload="beforeUpload"
          multiple>
          <i class="el-icon-plus"></i>
          <div slot="tip" class="el-upload__tip">支持上传jpg/png图片，不超过5MB</div>
        </el-upload>
      </el-form-item>
    </el-form>
    
    <div class="form-actions">
      <el-button @click="resetForm">取消</el-button>
      <el-button type="primary" @click="submitReport" :loading="submitting">提交举报</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ReportForm',
  props: {
    reportedUserId: {
      type: Number,
      required: true
    },
    reportedItemId: {
      type: Number,
      default: null
    }
  },
  data() {
    return {
      reportForm: {
        reportType: 1,
        reportReason: '',
        evidenceUrls: '',
        reportedUserId: this.reportedUserId,
        reportedItemId: this.reportedItemId
      },
      evidenceList: [],
      submitting: false,
      rules: {
        reportType: [
          { required: true, message: '请选择举报类型', trigger: 'change' }
        ],
        reportReason: [
          { required: true, message: '请输入举报理由', trigger: 'blur' },
          { min: 10, max: 500, message: '举报理由长度应在10到500个字符之间', trigger: 'blur' }
        ]
      }
    };
  },
  methods: {
    handleUploadSuccess(response, file, fileList) {
      if (response.status_code === 1) {
        this.evidenceList.push(response.data);
        this.reportForm.evidenceUrls = this.evidenceList.join(',');
      } else {
        this.$message.error('图片上传失败');
      }
    },
    handleUploadRemove(file, fileList) {
      const index = this.evidenceList.indexOf(file.response.data);
      if (index !== -1) {
        this.evidenceList.splice(index, 1);
        this.reportForm.evidenceUrls = this.evidenceList.join(',');
      }
    },
    beforeUpload(file) {
      const isJPGOrPNG = file.type === 'image/jpeg' || file.type === 'image/png';
      const isLt5M = file.size / 1024 / 1024 < 5;

      if (!isJPGOrPNG) {
        this.$message.error('只能上传JPG或PNG格式的图片!');
      }
      if (!isLt5M) {
        this.$message.error('图片大小不能超过5MB!');
      }
      return isJPGOrPNG && isLt5M;
    },
    submitReport() {
      this.$refs.reportForm.validate((valid) => {
        if (valid) {
          this.submitting = true;
          this.$api.createReport(this.reportForm).then(res => {
            this.submitting = false;
            if (res.status_code === 1) {
              this.$message.success('举报提交成功');
              this.$emit('report-submitted');
              this.resetForm();
            } else {
              this.$message.error(res.msg || '提交失败，请稍后重试');
            }
          }).catch(() => {
            this.submitting = false;
            this.$message.error('网络错误，请稍后重试');
          });
        } else {
          return false;
        }
      });
    },
    resetForm() {
      this.$refs.reportForm.resetFields();
      this.evidenceList = [];
      this.reportForm.evidenceUrls = '';
      this.$emit('cancel');
    }
  }
};
</script>

<style scoped>
.report-form {
  padding: 20px;
}
.form-actions {
  margin-top: 20px;
  text-align: right;
}
.el-upload__tip {
  line-height: 1.2;
  font-size: 12px;
  color: #606266;
}
</style> 