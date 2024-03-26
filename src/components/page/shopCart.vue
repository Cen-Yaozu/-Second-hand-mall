<template>
    <app-body>
        <app-head></app-head>
        <div class='shopCart-header'>
            <el-button @click="toggleSelection(dataList)">全选</el-button>
            <el-button @click="toggleSelection()">取消选择</el-button>
        </div>
        <el-table
            ref="multipleTable"
            :data="dataList"
            tooltip-effect="dark"
            class='el-table-cart'
            @selection-change="handleSelectionChange">
            <el-table-column
                type="selection"
                width="55">
            </el-table-column>
            <el-table-column
                label="商品图片"
                width="400%">
                <template slot-scope="scope">
                    <div class="info">
                        <div class="pic">
                            <img :src="scope.row.imgUrl" alt="商品图片" width="150px">
                        </div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column
                prop="idleName"
                label="商品名称"
                width="200px">
            </el-table-column>
            <el-table-column
                prop="idlePrice"
                label="商品价格"
                width="200px">
            </el-table-column>
            <el-table-column
                prop="idleDetails"
                label="商品描述"
                width="200px">
            </el-table-column>
            <el-table-column
                prop="idlePlace"
                label="商品所在地"
                width="200px">
            </el-table-column>
            <el-table-column
                label="操作"
                width="200px">
                <template slot-scope="scope">
                    <el-button icon="el-icon-delete"
                               @click="removeShopCart(scope.row,scope.index)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <div class='pay'>
            <div>所 选 总 价 ： {{total}}</div>
            <el-button icon='el-icon' @click="payAll()">支 付</el-button>
        </div>
        <app-foot></app-foot>
    </app-body>

</template>

<script>
import AppHead from '@/components/common/AppHeader.vue';
import AppBody from '@/components/common/AppPageBody.vue';
import AppFoot from '@/components/common/AppFoot.vue';

export default {

    name: "shopCart",
    components: {
        AppHead,
        AppBody,
        AppFoot
    },
    data() {
        return {
            isShowTip: false,
            tipContent: "",
            dataList: [],
            idlePlace: '',
            total: 0,
            idleItemids:[],
            favoriteIds:[]
        }
    },

    mounted() {
    },
    created() {

        if (!this.$globalData.userInfo.nickname) {
            this.$api.getUserInfo().then(res => {
                if (res.status_code === 1) {
                    res.data.signInTime = res.data.signInTime.substring(0, 10);
                    console.log(res.data);
                    this.$globalData.userInfo = res.data;
                    this.userInfo = this.$globalData.userInfo;
                }
            })
        } else {
            this.userInfo = this.$globalData.userInfo;
            console.log(this.userInfo);
        }
        this.getMyFavorite()
        this.getIdleItemData()
    },
    computed: {
    },

    methods: {

        /*支付功能*/
        payAll(){
            this.$api.addOrderByIds({
                idleItemIds:this.idleItemids,
                total_price:this.total,
            }).then(res=>{
                console.log(res);
                if(res.status_code===1){
                    this.$api.removeShopCartByIds({
                        favoriteIds: this.favoriteIds
                    }).then(res=>{
                        // alert("支付成功，商品已经从购物车中移出")
                    })
                    this.$router.push({path: '/order', query: {id: res.data.id}});
                }else {
                    this.$message.error(res.msg)
                }
            }).catch(e=>{

            })
        },
        /*多选框*/
        toggleSelection(rows) {
            if (rows) {
                rows.forEach(row => {
                    this.$refs.multipleTable.toggleRowSelection(row);
                });
            } else {
                this.$refs.multipleTable.clearSelection();
            }
        },
        handleSelectionChange(val) {
            this.multipleSelection = val;
            console.log(val)

            /*计算勾选总价*/
            if (val.length !== 0) {
                for (var i = 0; i < val.length; i++){
                    this.idleItemids[i] = val[i].id;
                    this.favoriteIds[i] = val[i].favoriteId;
                }

                console.log(this.idleItemids,this.favoriteIds)
                this.total = this.countTotal(val, 'idlePrice')
            }else{
                this.total = 0
            }
        },
        countTotal(arr, keyName) {
            let $total = 0
            $total = arr.reduce(function(total, currentValue, currentIndex, arr) {
                return currentValue[keyName] ? (total + currentValue[keyName]) : total
            }, 0)
            return $total
        },
        removeShopCart(row, index) {
            this.$api.deleteFavorite({
                id: row.favoriteId
            }).then(res => {
                console.log(res);
                console.log(row)
                // console.log("列表的数据"+ row.id);
                if (res.status_code === 1) {
                    this.$message({
                        message: '已取消购物车！',
                        type: 'success'
                    });
                    this.dataList.splice(index, 1);
                    // this.getMyFavorite()
                } else {
                    this.$message.error(res.msg)
                }
                // this.getMyFavorite()
            })
        },
        /*获取购物车内容*/
        getMyFavorite() {
            this.$api.getMyFavorite().then(res => {
                console.log('getMyFavorite', res);
                if (res.status_code === 1) {
                    for (let i = 0; i < res.data.length; i++) {
                        let pictureList = JSON.parse(res.data[i].idleItem.pictureList);
                        this.dataList.push({
                            favoriteId: res.data[i].id,
                            id: res.data[i].idleItem.id,
                            imgUrl: pictureList.length > 0 ? pictureList[0] : '',
                            idleName: res.data[i].idleItem.idleName,
                            idleDetails: res.data[i].idleItem.idleDetails,
                            timeStr: res.data[i].createTime.substring(0, 10) + " " + res.data[i].createTime.substring(11, 19),
                            idlePrice: res.data[i].idleItem.idlePrice,
                            idleStatus: res.data[i].idleItem.idleStatus
                        });
                        this.idlePlace = res.data[i].idleItem.idlePlace
                    }
                }
            })
        }
}

}

</script>
<style>
    .shopCart-header{
        margin-top: 30px;
        width: auto;
        padding-left: 7%;
        height: 7%;
    }
    .el-table-cart{
        width: 100%;
        padding-left: 10%;
    }
</style>