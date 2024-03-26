<!-- App.vue 或者父组件 -->
<template>
    <div>
        <!-- 假设这里是数据列表 -->
        <el-table :data="users">
            <!-- ...其他列... -->
            <template slot-scope="scope">
                <el-button
                    size="mini"
                    type="normal"
                    @click="showUpdateModal(scope.$index)">
                    修改
                </el-button>
            </template>
        </el-table>

        <!-- 弹出的修改用户信息模态框 -->
        <UpdateUserModal
            v-if="isModalVisible"
            :userInfo="selectedUserInfo"
            @save="handleSave"
            @cancel="handleCancel"
        />
    </div>
</template>

<script>
import UpdateUserModal from './components/UpdateUserModal.vue';

export default {
    components: {
        UpdateUserModal,
    },
    data() {
        return {
            users: [...], // 用户数据列表
            selectedUserInfo: {}, // 当前选中用户的信息
            isModalVisible: false, // 控制模态框是否显示
        };
    },
    methods: {
        showUpdateModal(index) {
            // 根据索引获取用户信息
            this.selectedUserInfo = this.users[index];
            this.isModalVisible = true;
        },
        handleSave(modifiedUserInfo) {
            // 更新用户信息逻辑...
            this.isModalVisible = false;
        },
        handleCancel() {
            this.isModalVisible = false;
            // 可以选择恢复之前的状态
        },
    },
};
</script>

// UpdateUserModal.vue
<template>
    <el-dialog title="修改用户信息" :visible.sync="dialogVisible">
        <el-form :model="form">
            <!-- 显示不可编辑的基本信息 -->
            <el-form-item label="用户名">
                <span>{{ form.username }}</span>
            </el-form-item>

            <!-- 显示并允许编辑的文本框 -->
            <el-form-item label="邮箱">
                <el-input v-model="form.email"></el-input>
            </el-form-item>

            <!-- 其他可编辑字段... -->
        </el-form>

        <div slot="footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSave">保存</el-button>
        </div>
    </el-dialog>
</template>

<script>
export default {
    props: {
        userInfo: {
            type: Object,
            required: true,
        },
    },
    data() {
        return {
            dialogVisible: this.visible, // 弹窗可见性同步
            form: {}, // 初始化表单数据
        };
    },
    created() {
        this.form = { ...this.userInfo }; // 将用户信息赋值给表单
    },
    methods: {
        handleSave() {
            // 提取已修改的表单数据
            const modifiedData = {...this.form};

            // 调用父组件的回调函数，传递修改后的用户信息
            this.$emit('save', modifiedData);
        },
    },
};
</script>
