import Vue from 'vue';
import Router from 'vue-router';

const originalReplace = Router.prototype.replace;
Router.prototype.replace = function replace(location) {
    return originalReplace.call(this, location).catch(err => err);
};
const originalPush = Router.prototype.push
Router.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
};

Vue.use(Router);

export default new Router({
    mode: "hash",
    routes: [
        {
            path: '/',
            redirect: '/index'
        },
        {
            path: '/index',
            /*组件懒加载*/
            component: () => import('../components/page/index.vue'),
            meta: { title: '淘易阁二手资源商城平台' }
        },
        {
            path: '/search',
            component: () => import('../components/page/search.vue'),
            meta: { title: '闲置搜索 | 淘易阁二手资源商城平台' }
        },
        {
            path: '/me',
            component: () => import('../components/page/me.vue'),
            meta: { title: '个人中心 | 淘易阁二手资源商城平台' }
        },
        {
            path: '/message',
            component: () => import('../components/page/message.vue'),
            meta: { title: '消息 | 淘易阁二手资源商城平台' }
        },
        {
            path: '/release',
            component: () => import('../components/page/release.vue'),
            meta: { title: '发布闲置 | 淘易阁二手资源商城平台' }
        },
        {
            path: '/details',
            component: () => import('../components/page/idle-details.vue'),
            meta: { title: '闲置详情 | 淘易阁二手资源商城平台' }
        },
        {
            path: '/order',
            component: () => import('../components/page/order.vue'),
            meta: { title: '订单详情 | 淘易阁二手资源商城平台' }
        },
        {
            path: '/shopCart',
            component: () => import('../components/page/shopCart.vue'),
            meta: { title: '购物车 | 淘易阁二手资源商城平台' }
        },
        {
            path: '/login',
            component: () => import('../components/page/login.vue'),
            meta: { title: '登录 | 淘易阁二手资源商城平台' }
        },
        {
            path: '/sign-in',
            component: () => import('../components/page/sign-in.vue'),
            meta: { title: '注册 | 淘易阁二手资源商城平台' }
        },
        {
            path: '/login-admin',
            component: () => import('../components/page/login-admin.vue'),
            meta: { title: '管理员登陆' }
        },
        {
            path: '/platform-admin',
            component: () => import('../components/page/platform-admin.vue'),
            meta: { title: '后台管理' }
        },
        {
            path: '/factious',
            component: () => import('../components/page/platform-admin.vue'),
            meta: { title: '后台管理' }
        },
        {
            path: '*',
            redirect: '/'
        }
    ]
});
