# 校易购二手资源商城平台

这是一个基于Vue.js和Spring Boot开发的校园二手交易平台，旨在为高校学生提供便捷的二手物品交易服务。

## 项目概述

校易购二手资源商城平台是一个完整的前后端分离项目，包含了用户管理、商品发布、在线交易、支付集成等功能模块。通过这个平台，学生可以方便地发布、浏览、购买闲置物品，促进校园资源的循环利用。

## 技术架构

### 前端技术栈
- **框架**：Vue.js 2.6.10
- **UI库**：Element UI 2.11.0
- **路由**：Vue Router 3.0.3
- **状态管理**：Vuex 3.6.2
- **HTTP客户端**：Axios 0.18.0
- **二维码生成**：vue-qr 4.0.6
- **开发工具**：Vue CLI 3.9.0

### 后端技术栈
- **框架**：Spring Boot 2.6.7
- **ORM框架**：MyBatis-Plus 3.5.2
- **数据库**：MySQL
- **数据库连接池**：Druid 1.2.9
- **JSON处理**：FastJSON 1.2.61
- **API文档**：Knife4j (Swagger增强) 4.1.0
- **支付接口**：支付宝SDK 4.22.110
- **工具库**：Hutool 5.8.26、Lombok

## 项目结构

### 顶层目录结构

```
- .git/                    # Git版本控制目录
- public/                  # 静态资源目录
- src/                     # 源代码目录（包含前端和后端代码）
- .browserslistrc          # 浏览器兼容性配置
- .prettierrc              # 代码格式化工具配置
- .project                 # Eclipse项目配置文件
- babel.config.js          # Babel转译器配置
- package.json             # 前端依赖管理配置
- package-lock.json        # npm依赖锁定文件
- pom.xml                  # Maven项目配置（后端）
- postcss.config.js        # PostCSS配置
- README.md                # 中文项目说明
- README.en.md             # 英文项目说明
- vue.config.js            # Vue项目配置
```

### 前端部分 (Vue.js)

#### src目录结构

```
src/
├── api/                   # API接口封装
│   └── index.js           # 所有API请求方法
├── components/            # Vue组件
│   ├── common/            # 公共组件
│   └── page/              # 页面组件
│       ├── index.vue      # 首页
│       ├── login.vue      # 登录页
│       ├── sign-in.vue    # 注册页
│       ├── me.vue         # 个人中心
│       ├── idle-details.vue # 闲置物品详情
│       ├── order.vue      # 订单页面
│       ├── release.vue    # 发布闲置页面
│       ├── search.vue     # 搜索页面
│       ├── message.vue    # 消息页面
│       ├── shopCart.vue   # 购物车页面
│       ├── login-admin.vue # 管理员登录页
│       └── platform-admin.vue # 后台管理页面
├── router/                # 路由配置
│   └── index.js           # 路由定义
├── store/                 # Vuex状态管理
├── utils/                 # 实用工具库
│   └── request.js         # HTTP请求封装
├── App.vue                # 根组件
└── main.js                # 应用入口文件
```

### 后端部分 (Spring Boot)

#### 目录结构

```
src/main/
├── java/                  # Java源代码
│   └── com/second/hand/trading/server/
│       ├── config/        # 配置类
│       ├── controller/    # 控制器（处理HTTP请求）
│       │   ├── UserController.java        # 用户相关API
│       │   ├── IdleItemController.java    # 闲置物品API
│       │   ├── OrderController.java       # 订单API
│       │   ├── MessageController.java     # 消息API
│       │   ├── AddressController.java     # 地址API
│       │   ├── OrderAddressController.java # 订单地址API
│       │   ├── FavoriteController.java    # 收藏API
│       │   ├── FileController.java        # 文件上传API
│       │   ├── AdminController.java       # 管理员API
│       │   └── AliPayController.java      # 支付宝支付API
│       ├── dao/           # 数据访问对象(DAO)
│       ├── enums/         # 枚举类型
│       ├── Exception/     # 异常处理
│       ├── Handler/       # 处理器
│       ├── model/         # 数据模型（实体类）
│       ├── service/       # 业务逻辑服务
│       ├── utils/         # 工具类
│       ├── vo/            # 视图对象(VO)
│       ├── ServerApplication.java # 应用程序入口
│       ├── LogCostInterceptor.java # 日志拦截器
│       └── WebMvcConfig.java       # MVC配置
└── resources/             # 资源文件
    ├── static/            # 静态资源
    ├── templates/         # 模板文件
    └── application.yml    # 应用配置文件
```

## 功能模块

1. **用户模块**
   - 注册/登录
   - 个人信息管理
   - 地址管理

2. **商品模块**
   - 闲置物品发布
   - 闲置物品浏览
   - 商品搜索
   - 收藏管理

3. **订单模块**
   - 订单创建与管理
   - 购物车
   - 支付（集成支付宝）

4. **消息模块**
   - 用户间消息交流
   - 系统通知

5. **管理员模块**
   - 用户管理
   - 商品管理
   - 订单管理
   - 平台维护

## 架构特点

1. **前后端分离**：前端Vue.js，后端Spring Boot，通过RESTful API交互
2. **MVC架构**：后端采用经典的MVC（Model-View-Controller）架构
3. **响应式设计**：使用Element UI实现响应式布局，适配不同设备
4. **模块化开发**：前端采用Vue组件化开发，后端采用分层架构
5. **安全机制**：包含用户认证和权限控制
6. **第三方集成**：集成支付宝支付接口

## 开发与部署

### 本地开发
1. 前端
```bash
# 安装依赖
npm install

# 开发模式启动
npm run serve

# 构建生产版本
npm run build
```

2. 后端
```bash
# 使用Maven打包
mvn clean package

# 运行Jar包
java -jar target/server-1.0-SNAPSHOT.jar
```

## 项目特色

这个项目是一个完整的全栈应用，专为校园二手交易场景设计。前端UI美观实用，后端功能完善，能够满足校园用户二手物品交易的各种需求，促进资源循环利用，为校园生活增添便利。

#### 介绍
毕业设计，校园二手交易平台
#### 使用说明

1. 支付宝支付账号密码
   fxoklf3702@sandbox.com 111111
2. xxxx
3. xxxx

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
