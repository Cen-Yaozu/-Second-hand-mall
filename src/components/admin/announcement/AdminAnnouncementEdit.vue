<template>
  <div class="admin-announcement-edit">
    <div class="admin-announcement-header">
      <div class="header-title">{{ isEdit ? '编辑公告' : '新建公告' }}</div>
      <div class="header-actions">
        <el-button icon="el-icon-arrow-left" @click="goBack">返回</el-button>
      </div>
    </div>

    <el-form 
      :model="announcementForm" 
      :rules="rules" 
      ref="announcementForm" 
      label-width="100px"
      v-loading="loading">
      
      <el-form-item label="公告标题" prop="title">
        <el-input v-model="announcementForm.title" placeholder="请输入公告标题"></el-input>
      </el-form-item>
      
      <el-form-item label="公告类型" prop="type">
        <el-select v-model="announcementForm.type" placeholder="请选择公告类型" style="width: 100%">
          <el-option label="系统更新" :value="1"></el-option>
          <el-option label="活动通知" :value="2"></el-option>
          <el-option label="规则变更" :value="3"></el-option>
        </el-select>
      </el-form-item>
      
      <el-form-item label="是否重要" prop="isImportant">
        <el-switch
          v-model="announcementForm.isImportant"
          active-text="重要"
          inactive-text="普通">
        </el-switch>
      </el-form-item>
      
      <el-form-item label="过期时间" prop="expireTime">
        <el-date-picker
          v-model="announcementForm.expireTime"
          type="datetime"
          placeholder="选择过期时间（可选）"
          format="yyyy-MM-dd HH:mm"
          value-format="yyyy-MM-dd HH:mm:ss"
          style="width: 100%">
        </el-date-picker>
      </el-form-item>
      
      <el-form-item label="公告内容" prop="content">
        <el-input
          type="textarea"
          :rows="10"
          placeholder="请输入公告内容"
          v-model="announcementForm.content">
        </el-input>
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="submitForm">保存</el-button>
        <el-button type="success" v-if="isEdit && announcementForm.status === 0" @click="publishAnnouncement">保存并发布</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  name: 'AdminAnnouncementEdit',
  data() {
    return {
      loading: false,
      isEdit: false,
      announcementId: null,
      announcementForm: {
        title: '',
        content: '',
        type: 1,
        isImportant: false,
        status: 0,
        expireTime: null
      },
      rules: {
        title: [
          { required: true, message: '请输入公告标题', trigger: 'blur' },
          { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入公告内容', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择公告类型', trigger: 'change' }
        ]
      }
    };
  },
  created() {
    // 判断是编辑还是创建
    this.announcementId = this.$route.params.id;
    this.isEdit = !!this.announcementId;
    
    if (this.isEdit) {
      this.fetchAnnouncementDetail();
    }
  },
  methods: {
    fetchAnnouncementDetail() {
      this.loading = true;
      this.$api.adminGetAnnouncementDetail(this.announcementId).then(res => {
        this.loading = false;
        if (res.status_code === 1) {
          this.announcementForm = res.data;
        } else {
          this.$message.error('获取公告详情失败');
          this.goBack();
        }
      }).catch(() => {
        this.loading = false;
        this.$message.error('获取公告详情失败');
        this.goBack();
      });
    },
    submitForm() {
      this.$refs.announcementForm.validate(valid => {
        if (valid) {
          this.saveAnnouncement();
        } else {
          return false;
        }
      });
    },
    saveAnnouncement() {
      this.loading = true;
      let request;
      
      if (this.isEdit) {
        request = this.$api.adminUpdateAnnouncement(this.announcementId, this.announcementForm);
      } else {
        request = this.$api.adminCreateAnnouncement(this.announcementForm);
      }
      
      request.then(res => {
        this.loading = false;
        if (res.status_code === 1) {
          this.$message.success(this.isEdit ? '公告更新成功' : '公告创建成功');
          this.goBack();
        } else {
          this.$message.error(this.isEdit ? '公告更新失败' : '公告创建失败');
        }
      }).catch(() => {
        this.loading = false;
        this.$message.error(this.isEdit ? '公告更新失败' : '公告创建失败');
      });
    },
    publishAnnouncement() {
      this.$refs.announcementForm.validate(valid => {
        if (valid) {
          this.loading = true;
          // 先保存公告
          this.$api.adminUpdateAnnouncement(this.announcementId, this.announcementForm).then(res => {
            if (res.status_code === 1) {
              // 保存成功后发布公告
              this.$api.adminPublishAnnouncement(this.announcementId).then(res => {
                this.loading = false;
                if (res.status_code === 1) {
                  this.$message.success('公告已保存并发布');
                  this.goBack();
                } else {
                  this.$message.error('公告发布失败');
                }
              }).catch(() => {
                this.loading = false;
                this.$message.error('公告发布失败');
              });
            } else {
              this.loading = false;
              this.$message.error('公告保存失败');
            }
          }).catch(() => {
            this.loading = false;
            this.$message.error('公告保存失败');
          });
        } else {
          return false;
        }
      });
    },
    resetForm() {
      if (this.isEdit) {
        this.fetchAnnouncementDetail();
      } else {
        this.$refs.announcementForm.resetFields();
      }
    },
    goBack() {
      this.$router.push('/admin/announcement/list');
    }
  }
};
</script>

<style scoped>
.admin-announcement-edit {
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

.el-form {
  max-width: 800px;
}
</style> 