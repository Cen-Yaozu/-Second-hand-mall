<template>
    <div class="main-border">
        <el-menu default-active="1" class="el-menu-demo" mode="horizontal" @select="handleSelect">
          <span v-show="this.mode != 3" class="app-title">
						<el-input placeholder="用户账号..." v-model="searchValue" @keyup.enter.native="searchIdle">
							<el-button slot="append" icon="el-icon-search" @click="searchIdle"></el-button>
						</el-input>
					</span>
            <el-menu-item index="1">正常用户</el-menu-item>
            <el-menu-item index="2">违规用户</el-menu-item>
            <el-menu-item index="3">管理员</el-menu-item>
            <div v-show="this.mode ==3" class="addAdminButton">
                <el-button size="mini" type="success" @click="adminRegVisible = true">添加管理员</el-button>
                <el-dialog
                        title="添加管理员"
                        :visible.sync="adminRegVisible"
                        width="25%"
                       >
                    <span style="margin-left: 10px">新增管理员名称</span>
                    <el-input v-model="adminName"  maxlength="8" placeholder="请输入管理员名称" style="padding: 10px 0" clearable required></el-input>
                    <span style="margin-left: 10px">新增管理员账户</span>
                    <el-input v-model="adminAccount" minlength="8" maxlength="10" placeholder="请输入管理员账户" style="padding: 10px 0"
                               clearable required></el-input>
                    <span style="margin-left: 10px">新增管理员密码</span>
                    <el-input v-model="adminPassword" minlength="8" placeholder="请输入管理员密码" style="padding: 10px 0" show-password required></el-input>
                    <span style="margin-left: 10px">确认管理员密码</span>
                    <el-input v-model="adminRePassword" minlength="10" placeholder="请再次输入管理员密码" style="padding: 10px 0" show-password required></el-input>
                    <span slot="footer" class="dialog-footer">
                        <el-button type="primary" @click="regAdmin">添加</el-button>
                    </span>
                </el-dialog>
            </div>
        </el-menu>
        <el-table v-show="this.mode == 1"
                  :data="userData"
                  stripe
                  style="width: 100%;color: #5a5c61;">
            <el-table-column label="头像" width="50">
                <template slot-scope="scope">
                    <el-avatar shape="square" :size="23" :src="scope.row.avatar"></el-avatar>
                </template>
            </el-table-column>
            <el-table-column
                    prop="accountNumber"
                    label="用户账号"
                    show-overflow-tooltip
                    min-width="150"
                    width="150">
            </el-table-column>
            <el-table-column
                    prop="nickname"
                    label="用户昵称"
                    show-overflow-tooltip
                    min-width="150"
                    width="150">
            </el-table-column>
            <el-table-column
                    prop="signInTime"
                    label="注册时间"
                    show-overflow-tooltip
                    width="200">
            </el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <el-button
                            size="mini"
                            type="danger"
                            @click="confirmSeal(scope.$index)">封号</el-button>
                    <el-button
                        size="mini"
                        type="normal"
                        @click="showUpdateModal(scope.$index)">修改</el-button>
                </template>

            </el-table-column>
        </el-table>
        <!-- 确认弹窗 -->
        <el-dialog
            :title="'确认封号'"
            :visible.sync="dialogConfirmVisible"
            width="30%"
            center>
            <p>您确定要封禁该账号吗？</p>
            <div slot="footer">
                <el-button @click="dialogConfirmVisible = false">取消</el-button>
                <el-button type="primary" @click="sealUserConfirmed(targetIndex)">确定</el-button>
            </div>
        </el-dialog>
        <!-- 内部创建的对话框 -->
        <el-dialog
            :visible.sync="dialogVisible"
            title="修改用户信息"
            @close="handleDialogClose">
            <el-form :model="userForm" ref="userForm" label-width="80px">
                <!-- 显示不可编辑的基本信息 -->
                <el-form-item label="账号">
                    <el-input :disabled = "true" v-model="userForm.accountNumber"></el-input>
                </el-form-item>

                <!-- 显示并允许编辑的文本框 -->
                <el-form-item label="用户名">
                    <el-input v-model="userForm.nickname"></el-input>
                </el-form-item>
                <el-form-item label="密码" prop="userPassword">
                    <el-input v-model="userForm.userPassword" :rules="[
                                                        {
                                                          validator: validatePassword,
                                                          trigger: 'blur'
                                                        }
                                                      ]">
                    </el-input>
                </el-form-item>

            </el-form>

            <div slot="footer">
                <el-button @click="handleCancel">取消</el-button>
                <el-button type="primary" @click="handleSave">保存</el-button>
            </div>
        </el-dialog>
        <el-table v-show="this.mode == 2"
                  :data="badUserData"
                  stripe
                  style="width: 100%;color: #5a5c61;">
            <el-table-column
                    label="头像"
                    width="50">
                <template slot-scope="scope">
                    <el-avatar shape="square" :size="23" :src="scope.row.avatar"></el-avatar>
                </template>
            </el-table-column>
            <el-table-column
                    prop="accountNumber"
                    label="用户账号"
                    show-overflow-tooltip
                    min-width="150"
                    width="150">
            </el-table-column>
            <el-table-column
                    prop="nickname"
                    label="用户昵称"
                    show-overflow-tooltip
                    width="150"
            >
            </el-table-column>
            <el-table-column
                    prop="signInTime"
                    label="注册时间"
                    show-overflow-tooltip
                    width="200">
            </el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <el-button
                            size="mini"
                            type="success"
                            @click="unsealUser(scope.$index)">解封</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-table  v-show="this.mode == 3"
                :data="userManage"
                stripe
                style="width: 100%;color: #5a5c61;">
            <el-table-column
                    prop="accountNumber"
                    label="管理员账号"
                    show-overflow-tooltip
                    width="200">
            </el-table-column>
            <el-table-column
                    prop="adminName"
                    label="管理名称"
                    >
            </el-table-column>
        </el-table>
        <div class="block">
            <el-pagination
                    @current-change="handleCurrentChange"
                    :current-page.sync="nowPage"
                    :page-size="7"
                    background
                    layout="prev, pager, next,jumper"
                    :total="total">
            </el-pagination>
        </div>
    </div>
</template>

<script>
    export default {
        name: "userList",
        created() {
            this.getUserData();
        },
        methods: {
            handleCurrentChange(val) {
                this.nowPage = val;
                if(this.mode == 1){
                    this.getUserData();
                }
                if(this.mode == 2){
                    this.getBadUserData();
                }
                if(this.mode == 3){
                    this.getUserManage();
                }
            },
            handleSelect(val){
                if(this.mode !== val){
                    this.mode = val
                    if(val == 1){
                        this.nowPage = 1;
                        this.getUserData();
                    }
                    if(val == 2){
                        this.nowPage = 1;
                        this.getBadUserData();
                    }
                    if(val == 3){
                        this.nowPage = 1;
                        this.getUserManage();
                    }
                }
            },
            getUserData(){
                //正常普通用户
                this.$api.getUserData({
                    page: this.nowPage,
                    nums:7,
                    status:0
                }).then(res => {
                    if(res.status_code==1){
                        this.userData = res.data.list;
                        this.total = res.data.count;
                    }else {
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            },
            getBadUserData(){
                //违规用户
                this.$api.getUserData({
                    page: this.nowPage,
                    nums:8,
                    status:1
                }).then(res => {
                    if(res.status_code==1){
                        this.badUserData = res.data.list;
                        this.total = res.data.count;
                    }else {
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                });
            },
            getUserManage(){
                //管理员
                this.$api.getUserManage({
                    page: this.nowPage,
                    nums:8,
                }).then(res => {
                    if(res.status_code==1){
                        this.userManage = res.data.list;
                        this.total = res.data.count;
                    }else {
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            },
            confirmSeal(index) {
                this.targetIndex = index;
                this.dialogConfirmVisible = true;
            },
            sealUserConfirmed(index) {
                // 执行封号操作
                this.sealUser(index);
                // 关闭确认弹窗
                this.dialogConfirmVisible = false;
            },
            // 封号
            sealUser(i){
                console.log( this.userData[i].id);
                this.$api.updateUserStatus({
                    id: this.userData[i].id,
                    status:1
                }).then(res => {
                    if(res.status_code==1){
                        this.getUserData();
                    }else {
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            },
            unsealUser(i){
                this.$api.updateUserStatus({
                    id: this.badUserData[i].id,
                    status:0
                }).then(res => {
                    if(res.status_code==1){
                        this.getBadUserData();
                    }else {
                        this.$message.error(res.msg)
                    }

                }).catch(e => {
                    console.log(e)
                })
            },
            regAdmin(){
                if(this.adminPassword == this.adminRePassword){
                    this.$api.regAdministrator({
                        adminName: this.adminName,
                        accountNumber: this.adminAccount,
                        adminPassword: this.adminPassword,
                    }).then(res => {
                        if(res.status_code==1){
                            this.total = this.total+1;
                            this.nowPage= Math.ceil(this.total/8);
                            console.log(this.nowPage);
                            this.getUserManage();
                        }else {
                            this.$message.error(res.msg)
                        }
                    }).catch(e => {
                        console.log(e);
                        this.$message.error("添加失败，账号重复或网络异常")
                    });
                    this.adminRegVisible = false
                }
                else {
                    this.$message.error("两次输入的密码不一致");
                }
            },
            searchIdle() {
                this.$api.queryUser({
                    searchValue: this.searchValue,
                    mode: this.mode,
                    page: this.nowPage,
                    nums: 8,
                }).then(res => {
                    if (res.status_code == 1) {
                        if(this.mode == 1){
                            this.userData = res.data.list;
                            this.total = res.data.count;
                        }else if(this.mode == 2){
                            this.badUserData = res.data.list;
                            this.total = res.data.count;
                        }else {
                            this.userManage = res.data.list;
                            this.total = res.data.count;
                        }
                    } else{
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            },
            showUpdateModal(index) {
                // 保存当前行的用户信息，并显示对话框
                this.selectedUserIndex = index;
                this.userForm = { ...this.userData[index] };
                this.dialogVisible = true;
            },
            handleCancel() {
                this.resetForm();
                this.dialogVisible = false;
            },
            handleSave() {
                // 验证并提交表单
                if (this.$refs.userForm.validate()) {
                    // 合并更新后的表单数据到原用户列表
                    this.userData[this.selectedUserIndex] = { ...this.userForm };

                    // 如果有需要，这里可以对接后台接口，将更新的数据发送到服务器
                    this.$api.updateUser({
                        id: this.userForm.id,
                        nickname: this.userForm.nickname,
                        password: this.userForm.userPassword,
                    }).then(res => {
                        if (res.status_code === 1) {
                            this.$message.success('更新成功');
                            this.dialogVisible = false;
                            this.getUserData();
                        }
                    }).catch(e => {
                        console.log(e)
                    })
                }
            },
            validatePassword(rule, value, callback) {
                const allowedChars = /^[a-zA-Z0-9!"#$%&'()*+,-./:;<=>?@[\\\]^_`{|}~]+$/;
                if (!allowedChars.test(value)) {
                    callback(new Error('密码只能包含字母、数字以及常见的特殊字符'));
                } else {
                    callback();
                }
            },
            resetForm() {
                this.userForm = {};
            },
            handleDialogClose() {
                // 对话框关闭时重置表单
                this.resetForm();
            },
        },
        data(){
            return {
                dialogConfirmVisible: false, // 控制确认弹窗的显示隐藏
                targetIndex: null, // 记录即将封号的用户索引
                userForm: {}, // 当前编辑的用户表单数据
                dialogVisible: false, // 控制对话框是否显示
                selectedUserIndex: null, // 当前选中用户的索引
                mode:1,
                nowPage: 1,
                total: 63,
                adminRegVisible: false,
                adminAccount:'',
                adminPassword:'',
                adminRePassword:'',
                adminName:'',
                userData: [],
                badUserData:[],
                userManage:[],
                searchValue: '',
            }
        },
    }
</script>

<style scoped>
    .main-border{
        background-color: #FFF;
        padding: 10px 30px;
        box-shadow: 0 1px 15px -6px rgba(0,0,0,.5);
        border-radius: 5px;
    }
    .block {
        display: flex;
        justify-content:center;
        padding-top: 15px;
        padding-bottom: 10px;
        width: 100%;
    }
    .addAdminButton{
        display:flex;
        justify-content: flex-end;
        align-items: center;
        height: 60px;
        outline: none;
    }
</style>
