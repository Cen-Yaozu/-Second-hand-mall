import request from '../utils/request';

const api = {

    // 用户功能 对应usercontroller
    userLogin(query) {
        return request({
            url: '/user/login',
            method: 'post',
            params: query
        });
    },
    logout(query) {
        return request({
            url: '/user/logout',
            method: 'get',
            params: query
        });
    },
    signIn(data) {
        return request({
            url: '/user/sign-in',
            method: 'post',
            data: data
        });
    },
    getUserInfo(query) {
        return request({
            url: '/user/info',
            method: 'get',
            params: query
        });
    },
    updateUserPublicInfo(data) {
        return request({
            url: '/user/update-public-info',
            method: 'put',
            data: data
        });
    },
    updatePassword(query) {
        return request({
            url: '/user/update-password',
            method: 'put',
            params: query
        });
    },
    //  地址功能  对应addresscontroller
    addAddress(data) {
        return request({
            url: '/address/add',
            method: 'post',
            data: data
        });
    },
    getAddress(query) {
        return request({
            url: '/address/info',
            method: 'get',
            params: query
        });
    },
    updateAddress(data) {
        return request({
            url: '/address/update',
            method: 'post',
            data: data
        });
    },
    deleteAddress(data) {
        return request({
            url: '/address/delete',
            method: 'delete',
            data: data
        });
    },

    // 商品操作   对应idleitemController
    addIdleItem(data) {
        return request({
            url: '/idle/add',
            method: 'post',
            data: data
        });
    },
    getIdleItem(query) {
        return request({
            url: '/idle/info',
            method: 'get',
            params: query
        });
    },
    getAllIdleItem(query) {
        return request({
            url: '/idle/all',
            method: 'get',
            params: query
        });
    },
    findIdleTiem(query) {
        return request({
            url: '/idle/find',
            method: 'get',
            params: query
        });
    },
    findIdleTiemByLable(query) {
        return request({
            url: '/idle/lable',
            method: 'get',
            params: query
        });
    },
    updateIdleItem(data) {
        return request({
            url: '/idle/update',
            method: 'post',
            headers: {
                'Content-Type': 'application/json'
            },
            data: data
        });
    },

    // 个人中心的功能  ordercontroller
    addOrder(data) {
        return request({
            url: '/order/add',
            method: 'post',
            data: data
        });
    },
    getOrder(query) {
        return request({
            url: '/order/info',
            method: 'get',
            params: query
        });
    },
    updateOrder(data) {
        return request({
            url: '/order/update',
            method: 'post',
            data: data
        });
    },
    alipay(data){
        return request({
            url: '/alipay/pay',
            method: 'post',
            data: data
        })
    },
    getMyOrder(query) {
        return request({
            url: '/order/my',
            method: 'get',
            params: query
        });
    },
    getMySoldIdle(query) {
        return request({
            url: '/order/my-sold',
            method: 'get',
            params: query
        });
    },

    // 订单的地址信息   orderAddressController
    addOrderAddress(data) {
        return request({
            url: '/order-address/add',
            method: 'post',
            data: data
        });
    },
    updateOrderAddress(data) {
        return request({
            url: '/order-address/update',
            method: 'post',
            data: data
        });
    },
    getOrderAddress(query) {
        return request({
            url: '/order-address/info',
            method: 'get',
            params: query
        });
    },

    // 收藏功能就是购物车    favoriteController
    addFavorite(data) {
        return request({
            url: '/favorite/add',
            method: 'post',
            data: data
        });
    },
    getMyFavorite(query) {
        return request({
            url: '/favorite/my',
            method: 'get',
            params: query
        });
    },
    deleteFavorite(query) {
        return request({
            url: '/favorite/delete',
            method: 'delete',
            params: query
        });
    },
    checkFavorite(query) {
        return request({
            url: '/favorite/check',
            method: 'get',
            params: query
        });
    },

    checkMyIdle(query) {
        return request({
            url: '/idle/check',
            method: 'get',
            params: query
        });
    },

    // 留言功能   messagecontroller
    sendMessage(data) {
        return request({
            url: '/message/send',
            method: 'post',
            data: data
        });
    },
    getMessage(query) {
        return request({
            url: '/message/info',
            method: 'get',
            params: query
        });
    },
    getAllIdleMessage(query) {
        return request({
            url: '/message/idle',
            method: 'get',
            params: query
        });
    },
    getAllMyMessage(query) {
        return request({
            url: '/message/my',
            method: 'get',
            params: query
        });
    },
    // 消息未读状态相关API
    getUnreadMessageCount() {
        return request({
            url: '/message/unread-count',
            method: 'get'
        });
    },
    deleteMessage(query) {
        return request({
            url: '/message/delete',
            method: 'get',
            params: query
        });
    },
    markMessageAsRead(query) {
        return request({
            url: '/message/mark-read',
            method: 'put',
            params: query
        });
    },

    // 管理员相关操作
    getGoods(query) {
        return request({
            url: '/admin/idleList',
            method: 'get',
            params: query
        });
    },
    updateGoods(query) {
        return request({
            url: '/admin/updateIdleStatus',
            method: 'get',
            params: query
        });
    },
    getOrderList(query) {
        return request({
            url: '/admin/orderList',
            method: 'get',
            params: query
        });
    },
    deleteOrder(query) {
        return request({
            url: '/admin/deleteOrder',
            method: 'delete',
            params: query
        });
    },
    getUserData(query) {
        return request({
            url: '/admin/userList',
            method: 'get',
            params: query
        });
    },
    getUserManage(query) {
        return request({
            url: '/admin/list',
            method: 'get',
            params: query
        });
    },
    updateUserStatus(query){
        return request({
            url: '/admin/updateUserStatus',
            method: 'get',
            params: query
        });
    },
    regAdministrator(data){
        return request({
            url: '/admin/add',
            method: 'post',
            data: data
        });
    },
    adminLogin(query) {
        return request({
            url: '/admin/login',
            method: 'post',
            params: query
        });
    },
    loginOut(query) {
        return request({
            url: '/user/logout',
            method: 'post',
            params: query
        });
    },
    queryIdle(query) {
        return request({
            url: '/admin/queryIdle',
            method: 'get',
            params: query
        });
    },
    queryOrder(query) {
        return request({
            url: '/admin/queryOrder',
            method: 'get',
            params: query
        });
    },
    queryUser(query) {
        return request({
            url: '/admin/queryUser',
            method: 'get',
            params: query
        });
    },
    updateAlipay(query) {
        return request({
            url: '/alipay/pay',
            method: 'get',
            params: query
        });
    },
    addOrderByIds(data) {
        return request({
            url: '/order/addByIds',
            method: 'post',
            data: data
        });
    },
    removeShopCartByIds(data) {
        return request({
            url: '/favorite/removeByIds',
            method: 'delete',
            data: data
        });
    },
    updateUser(data) {
        return request({
            url: 'admin/updateUser',
            method: 'post',
            data: data
        });
    },

    // 物品交换功能 对应exchangecontroller
    createExchangeRequest(data) {
        return request({
            url: '/exchange/create',
            method: 'post',
            data: data
        });
    },
    getMyExchangeRequests() {
        return request({
            url: '/exchange/my-requests',
            method: 'get'
        });
    },
    getReceivedExchangeRequests() {
        return request({
            url: '/exchange/received-requests',
            method: 'get'
        });
    },
    getExchangeRequestDetail(requestId) {
        return request({
            url: `/exchange/detail/${requestId}`,
            method: 'get'
        });
    },
    acceptExchangeRequest(requestId) {
        return request({
            url: `/exchange/accept/${requestId}`,
            method: 'put'
        });
    },
    rejectExchangeRequest(requestId) {
        return request({
            url: `/exchange/reject/${requestId}`,
            method: 'put'
        });
    },
    completeExchange(requestId) {
        return request({
            url: `/exchange/complete/${requestId}`,
            method: 'put'
        });
    },

    // 捐赠功能 对应DonationController
    createDonationRequest(data) {
        return request({
            url: '/donation/create',
            method: 'post',
            data: data
        });
    },
    getMyDonationRequests(query) {
        return request({
            url: '/donation/my-requests',
            method: 'get',
            params: query
        });
    },
    getDonationRequestDetail(query) {
        return request({
            url: `/donation/detail/${query.requestId}`,
            method: 'get',
            params: query
        });
    },
    cancelDonationRequest(requestId) {
        return request({
            url: `/donation/cancel/${requestId}`,
            method: 'put'
        });
    },
    // 管理员捐赠管理接口
    getAllDonationRequests(query) {
        return request({
            url: '/admin/donation/list',
            method: 'get',
            params: query
        });
    },
    reviewDonationRequest(data) {
        return request({
            url: '/admin/donation/review',
            method: 'post',
            data: data
        });
    },
    completeDonation(requestId) {
        return request({
            url: `/donation/admin/complete/${requestId}`,
            method: 'put'
        });
    },
    
    // 举报功能
    createReport(data) {
        return request({
            url: '/report/create',
            method: 'post',
            data: data
        });
    },
    getMyReports() {
        return request({
            url: '/report/my-reports',
            method: 'get'
        });
    },
    getReportDetail(reportId) {
        return request({
            url: `/report/detail/${reportId}`,
            method: 'get'
        });
    },
    getAdminReportList(query) {
        return request({
            url: '/report/admin/list',
            method: 'get',
            params: query
        });
    },
    adminHandleReport(data) {
        return request({
            url: '/report/admin/approve',
            method: 'put',
            data: data
        });
    },
    adminRejectReport(data) {
        return request({
            url: '/report/admin/reject',
            method: 'put',
            data: data
        });
    },
    cancelReport(data) {
        return request({
            url: '/report/cancel',
            method: 'put',
            data: data
        });
    },
    
    // ----- 公告相关接口 -----
    // 获取公告列表
    getAnnouncementList(params) {
        return request({
            url: '/announcement/list',
            method: 'get',
            params: params
        });
    },
    // 获取公告详情
    getAnnouncementDetail(params) {
        return request({
            url: '/announcement/detail',
            method: 'get',
            params: params
        });
    },
    // 获取重要公告
    getImportantAnnouncements(params) {
        return request({
            url: '/announcement/important',
            method: 'get',
            params: params
        });
    },
    // 创建公告（管理员）
    createAnnouncement(data) {
        return request({
            url: '/admin/announcement/create',
            method: 'post',
            data: data
        });
    },
    // 更新公告（管理员）
    updateAnnouncement(data) {
        return request({
            url: '/admin/announcement/update/' + data.id,
            method: 'put',
            data: data
        });
    },
    // 删除公告（管理员）
    deleteAnnouncement(data) {
        return request({
            url: '/admin/announcement/delete/' + data.id,
            method: 'delete'
        });
    },
    // 发布公告（管理员）
    publishAnnouncement(id) {
        return request({
            url: '/admin/announcement/publish/' + id,
            method: 'put'
        });
    },
};


export default api;
