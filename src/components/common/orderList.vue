<template>
    <div class="main-border">
        	<span class="app-title">
						<el-input placeholder="订单编号..." v-model="searchValue" @keyup.enter.native="searchIdle">
							<el-button slot="append" icon="el-icon-search" @click="searchIdle"></el-button>
						</el-input>
					</span>
        <el-table
                :data="Order"
                stripe
                style="width: 100%;color: #5a5c61;"
                @row-dblclick="showOrderDetails">
            <el-table-column
                    prop="orderNumber"
                    label="订单号"
                    show-overflow-tooltip
                    width="200">
            </el-table-column>
            <el-table-column
                    prop="idleItem.idleName"
                    label="闲置名称"
                    show-overflow-tooltip
                    >
            </el-table-column>
            <el-table-column
                    prop="orderPrice"
                    label="金额"
                    show-overflow-tooltip
                    min-width="100"
                    width="100">
            </el-table-column>
            <el-table-column
                    prop="createTime"
                    label="创建时间"
                    show-overflow-tooltip
                    min-width="150"
                    width="200">
            </el-table-column>
            <el-table-column
                    label="订单状态"
                    width="100"
                    show-overflow-tooltip>
                <template slot-scope="scope">
                    <div>{{orderStatus[scope.row.orderStatus]}}</div>
                </template>
            </el-table-column>
            <el-table-column
                    label="支付状态"
                    width="100"
                    show-overflow-tooltip
                    class="cell el-tooltip">
                <template slot-scope="scope">
                    <div>{{paymentStatus[scope.row.paymentStatus]}}</div>
                </template>
            </el-table-column>
            <el-table-column
                    prop="paymentWay"
                    label="支付方式"
                    width="100"
                    show-overflow-tooltip>
            </el-table-column>
            <el-table-column
                    prop="paymentTime"
                    label="支付时间"
                    show-overflow-tooltip>
            </el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <el-button
                            size="mini"
                            type="danger"
                            @click="confirmDelete(scope.$index)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 确认弹窗 -->
        <el-dialog
            :title="'确认删除'"
            :visible.sync="dialogDeleteVisible"
            width="30%"
            center>
            <p>您确定要删除这个订单吗？</p>
            <div slot="footer">
                <el-button @click="dialogDeleteVisible = false">取消</el-button>
                <el-button type="primary" @click="deleteOrder(targetIndex)">确定</el-button>
            </div>
        </el-dialog>
        <el-dialog
            :visible.sync="dialogVisible"
            title="订单详情"
            width="50%">
            <div v-if="selectedRow">
                <p>订单号: {{ selectedRow.orderNumber }}</p>
                <p>闲置名称: {{ selectedRow.idleItem.idleName }}</p>
                <p>金额: {{ selectedRow.orderPrice }}</p>
                <p>创建时间: {{ selectedRow.createTime }}</p>
                <p>订单状态: {{ orderStatus[selectedRow.orderStatus] }}</p>
                <p>支付状态: {{ paymentStatus[selectedRow.paymentStatus] }}</p>
                <p>支付方式: {{ selectedRow.paymentWay }}</p>
                <p>支付时间: {{ selectedRow.paymentTime }}</p>
            </div>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">关闭</el-button>
            </div>
        </el-dialog>
        <div class="block">
            <el-pagination
                    @current-change="handleCurrentChange"
                    :current-page.sync="nowPage"
                    :page-size="8"
                    background
                    layout="prev, pager, next,jumper"
                    :total="total">
            </el-pagination>
        </div>
    </div>
</template>

<script>
    export default {
        name: "orderList",
        created() {
            this.getOrder();
        },
        methods:{
            confirmDelete(index){
                this.targetIndex = index;
                this.dialogDeleteVisible = true;
            },
            showOrderDetails(row) {
                this.selectedRow = row;
                this.dialogVisible = true;
            },
            getOrder(){
                this.$api.getOrderList({
                    page: this.nowPage,
                    nums:8
                }).then(res => {
                    if(res.status_code==1){
                        this.Order = res.data.list;
                        this.total = res.data.count;
                    }else {
                        this.$message.error(res.msg)
                    }

                }).catch(e => {
                    console.log(e)
                })
            },
            deleteOrder(index){
                this.$api.deleteOrder({
                    id:this.Order[index].id
                }).then(res=>{
                    if(res.status_code==1){
                        this.getOrder();
                        this.dialogDeleteVisible = false;
                    }else {
                        this.$message.error(res.msg)
                    }

                }).catch(e => {
                    console.log(e)
                })
            },
            handleCurrentChange(val) {
                this.nowPage = val;
                this.getOrder();
            },
            searchIdle(){
                this.$api.queryOrder({
                    page: this.nowPage,
                    nums: 8,
                    searchValue: this.searchValue
                }).then(res => {
                    console.log(res);
                    if (res.status_code == 1) {
                        this.Order = res.data.list;
                        this.total = res.data.count;
                    } else{
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            }
        },
        data(){
            return {
                mode:1,
                dialogDeleteVisible: false,
                targetIndex: null,
                nowPage: 1,
                total: 0,
                paymentStatus:['未支付','已支付'],
                orderStatus:['待付款','待发货','待收货','已完成','已取消'],
                Order: [],
                searchValue: '',
                dialogVisible: false,
                selectedRow: null,
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
</style>
