<template>
  <div class="announcement-edit">
    <el-card class="edit-card">
      <div slot="header" class="header">
        <span>{{ isEdit ? '编辑公告' : '创建公告' }}</span>
        <el-button-group>
          <el-button size="small" @click="goBack">返回</el-button>
          <el-button size="small" type="primary" @click="saveAsDraft">保存草稿</el-button>
          <el-button size="small" type="success" @click="publish">发布公告</el-button>
        </el-button-group>
      </div>
      
      <el-form :model="form" :rules="rules" ref="form" label-width="100px" v-loading="loading">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题"></el-input>
        </el-form-item>
        
        <el-form-item label="重要公告">
          <el-switch v-model="form.important" :active-value="1" :inactive-value="0"></el-switch>
        </el-form-item>
        
        <el-form-item label="内容" prop="content">
          <el-input
            type="textarea"
            v-model="form.content"
            :rows="15"
            placeholder="请输入公告内容，支持HTML格式"></el-input>
        </el-form-item>
      </el-form>
      
      <div class="preview">
        <div class="preview-header">
          <h3>预览</h3>
        </div>
        <div class="preview-title">{{ form.title || '公告标题' }}</div>
        <div class="preview-content" v-html="form.content || '公告内容'"></div>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "AnnouncementEdit",
  data() {
    return {
      isEdit: false,
      loading: false,
      announcementId: null,
      form: {
        title: '',
        content: '',
        important: 0,
        status: 0 // 0: 草稿, 1: 已发布
      },
      rules: {
        title: [
          { required: true, message: '请输入公告标题', trigger: 'blur' },
          { min: 2, max: 100, message: '标题长度在2到100个字符之间', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入公告内容', trigger: 'blur' }
        ]
      }
    };
  },
  created() {
    // 判断是否为编辑模式
    this.announcementId = this.$route.params.id;
    this.isEdit = !!this.announcementId;
    
    if (this.isEdit) {
      this.fetchAnnouncementDetail();
    }
  },
  methods: {
    fetchAnnouncementDetail() {
      this.loading = true;
      this.$api.getAnnouncementDetail({ id: this.announcementId })
        .then(res => {
          this.loading = false;
          if (res.status_code === 1) {
            this.form = {
              title: res.data.title,
              content: res.data.content,
              important: res.data.important,
              status: res.data.status
            };
          } else {
            this.$message.error(res.msg || '获取公告详情失败');
            this.goBack();
          }
        })
        .catch(err => {
          this.loading = false;
          console.error("获取公告详情失败:", err);
          this.$message.error('获取公告详情失败');
          this.goBack();
        });
    },
    
    saveAnnouncement(status) {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.loading = true;
          const params = {
            title: this.form.title,
            content: this.form.content,
            important: this.form.important,
            status: status
          };
          
          // 如果是编辑模式，添加ID
          if (this.isEdit) {
            params.id = this.announcementId;
          }
          
          const apiMethod = this.isEdit ? 'updateAnnouncement' : 'createAnnouncement';
          
          this.$api[apiMethod](params)
            .then(res => {
              this.loading = false;
              if (res.status_code === 1) {
                this.$message.success(status === 1 ? '发布成功' : '保存成功');
                this.goBack();
              } else {
                this.$message.error(res.msg || '操作失败');
              }
            })
            .catch(err => {
              this.loading = false;
              console.error("操作失败:", err);
              this.$message.error('操作失败');
            });
        }
      });
    },
    
    saveAsDraft() {
      this.saveAnnouncement(0);
    },
    
    publish() {
      this.saveAnnouncement(1);
    },
    
    goBack() {
      this.$router.push('/platform-admin');
    }
  }
};
</script>

<style scoped>
.announcement-edit {
  padding: 20px;
  background-color: #f6f6f6;
  min-height: 100vh;
}

.edit-card {
  max-width: 1000px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.preview {
  margin-top: 30px;
  padding: 20px;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
  background-color: #f8f8f8;
}

.preview-header {
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.preview-header h3 {
  margin: 0;
  color: #606266;
}

.preview-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #303133;
}

.preview-content {
  line-height: 1.6;
  color: #606266;
}
</style> 