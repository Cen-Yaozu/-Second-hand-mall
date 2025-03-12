<template>
	<div class="idle-goods-container">
		<div class="idle-header">
			<el-tabs v-model="status" @tab-click="handleSelect" type="border-card">
				<el-tab-pane label="上线的闲置" name="1"></el-tab-pane>
				<el-tab-pane label="下架的闲置" name="2"></el-tab-pane>
			</el-tabs>
			<div class="search-box">
				<el-input 
					placeholder="闲置名称..." 
					v-model="findValue" 
					@keyup.enter.native="searchIdle"
					class="search-input"
					prefix-icon="el-icon-search">
					<el-button slot="append" icon="el-icon-search" @click="searchIdle"></el-button>
				</el-input>
			</div>
		</div>

		<div class="idle-table-wrapper">
			<el-table
				v-if="mode == 1"
				:data="onlineGoods"
				stripe
				border
				highlight-current-row
				class="idle-table">
				<el-table-column
					prop="releaseTime"
					label="发布日期"
					width="180">
				</el-table-column>
				<el-table-column
					prop="idleName"
					label="闲置名称"
					show-overflow-tooltip>
				</el-table-column>
				<el-table-column
					prop="user.nickname"
					label="发布用户"
					show-overflow-tooltip
					width="120">
				</el-table-column>
				<el-table-column
					prop="idlePrice"
					label="价格"
					width="100">
					<template slot-scope="scope">
						<span class="price">¥{{ scope.row.idlePrice }}</span>
					</template>
				</el-table-column>
				<el-table-column label="操作" width="120" fixed="right">
					<template slot-scope="scope">
						<el-button
							size="mini"
							type="danger"
							icon="el-icon-delete"
							@click="handleOfflineGoods(scope.$index)">下架
						</el-button>
					</template>
				</el-table-column>
			</el-table>

			<el-table
				v-show="mode == 2"
				:data="OfflineGoods"
				stripe
				border
				highlight-current-row
				class="idle-table">
				<el-table-column
					prop="releaseTime"
					label="发布日期"
					width="180">
				</el-table-column>
				<el-table-column
					prop="idleName"
					label="闲置名称"
					show-overflow-tooltip>
				</el-table-column>
				<el-table-column
					prop="user.nickname"
					label="发布用户"
					show-overflow-tooltip
					width="120">
				</el-table-column>
				<el-table-column
					prop="idlePrice"
					label="价格"
					width="100">
					<template slot-scope="scope">
						<span class="price">¥{{ scope.row.idlePrice }}</span>
					</template>
				</el-table-column>
				<el-table-column label="操作" width="120" fixed="right">
					<template slot-scope="scope">
						<el-button
							size="mini"
							type="danger"
							icon="el-icon-delete"
							@click="deleteGoods(scope.$index)">删除
						</el-button>
					</template>
				</el-table-column>
			</el-table>

			<div class="pagination-container">
				<el-pagination
					@current-change="handleCurrentChange"
					:current-page.sync="nowPage"
					:page-size="8"
					background
					layout="prev, pager, next, jumper"
					:total="total">
				</el-pagination>
			</div>
		</div>
	</div>
</template>

<script>
    export default {
        name: "IdleGoods",
        data() {
            return {
                mode: 1,
                nowPage: 1,
                total: 0,
                onlineGoods: [],
                OfflineGoods: [],
                findValue: '',
								status: "1"
            }
        },
        created() {
            this.getOnlineGoods();
        },
        methods: {
            handleOfflineGoods(index) {
                this.$confirm('是否确定下架此闲置商品?', '警告', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    // 用户点击确定后的逻辑，例如调用后端接口下架商品
                    this.makeOfflineGoods(index);
                }).catch(() => {
                    // 用户点击取消后的逻辑，这里可以省略或添加相应处理
                    console.log('下架操作已取消');
                });
            },
            searchIdle(){
                this.$api.queryIdle({
                    findValue: this.findValue,
                    page: this.nowPage,
                    nums: 8,
										status: parseInt(this.status)
                }).then(res => {
                    console.log(res);
                    if (res.status_code == 1 && res.data.list != null) {
                            if(res.data.list[0].idleStatus == 1){
                                this.onlineGoods = res.data.list;
                                this.total = res.data.count;
														}else {
                                this.OfflineGoods = res.data.list;
                                this.total = res.data.count;
														}
                    } else{
                        this.$message.error(res.msg)
										}
                }).catch(e => {
                    console.log(e)
                })
            },
            handleCurrentChange(val) {
                this.nowPage = val;
                if (this.mode == 1) {
                    this.getOnlineGoods();
                }
                if (this.mode == 2) {
                    this.getOfflineGoods();
                }
            },
            handleSelect(tab) {
                const val = parseInt(tab.name);
                if (this.mode !== val) {
                    this.mode = val;
                    if (val == 1) {
                        this.nowPage = 1;
                        this.getOnlineGoods();
                    }
                    if (val == 2) {
                        this.nowPage = 1;
                        this.getOfflineGoods();
                    }
                }
            },
            makeOfflineGoods(i) {
                this.$api.updateGoods({
                    id: this.onlineGoods[i].id,
                    status: 2
                }).then(res => {
                    if (res.status_code == 1) {
                        this.$message.success('下架成功');
                        this.getOnlineGoods();
                    } else {
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            },
            deleteGoods(i) {
                this.$api.updateGoods({
                    id: this.OfflineGoods[i].id,
                    status: 0
                }).then(res => {
                    if (res.status_code == 1) {
                        this.getOfflineGoods();
                    } else {
                        this.$message.error(res.msg)
                    }

                }).catch(e => {
                    console.log(e)
                })
            },
            getOnlineGoods() {
                this.$api.getGoods({
                    status: 1,
                    page: this.nowPage,
                    nums: 8
                }).then(res => {
                    if (res.status_code == 1) {
                        this.onlineGoods = res.data.list;
                        this.total = res.data.count;
                    } else {
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            },
            getOfflineGoods() {
                this.$api.getGoods({
                    status: 2,
                    page: this.nowPage,
                    nums: 8
                }).then(res => {
                    if (res.status_code == 1) {
                        this.OfflineGoods = res.data.list;
                        this.total = res.data.count;
                    } else {
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            }
        }
    }
</script>

<style scoped>
	.idle-goods-container {
		background-color: var(--el-bg-color);
		padding: var(--el-spacing-lg);
		box-shadow: var(--el-box-shadow-light);
		border-radius: var(--el-border-radius-base);
	}

	.idle-header {
		margin-bottom: var(--el-spacing-lg);
	}

	.search-box {
		margin-top: var(--el-spacing-lg);
		max-width: 400px;
	}

	.search-input >>> .el-input__inner {
		border-radius: var(--el-border-radius-base);
	}

	.idle-table-wrapper {
		margin-top: var(--el-spacing-lg);
	}

	.idle-table {
		width: 100%;
		margin-bottom: var(--el-spacing-lg);
	}

	.price {
		color: var(--el-color-danger);
		font-weight: 500;
	}

	.pagination-container {
		display: flex;
		justify-content: center;
		padding: var(--el-spacing-lg) 0;
	}
</style>
