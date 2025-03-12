<template>
    <footer class="footer">
        <div class="footer-content">
            <div class="footer-main">
                <div class="footer-section">
                    <h3 class="footer-title">校易购</h3>
                    <p class="footer-desc">校园闲置物品交易平台 - 让闲置焕发新生</p>
                </div>
                <div class="footer-section">
                    <h3 class="footer-title">联系我们</h3>
                    <p class="footer-contact"><i class="el-icon-message"></i> contact@schoolbuy.com</p>
                    <p class="footer-contact"><i class="el-icon-phone"></i> 010-12345678</p>
                </div>
            </div>
            <div class="footer-copyright">
                <p>© {{ currentYear }} 校易购 - 毕业设计项目</p>
            </div>
        </div>
    </footer>
</template>

<script>
    export default {
        name: "AppFoot",
        computed: {
            currentYear() {
                return new Date().getFullYear();
            }
        }
    }
</script>

<style scoped>
    .footer {
        width: 100%;
        background-color: var(--el-bg-color);
        border-top: 1px solid var(--el-border-color-lighter);
        padding-top: var(--el-spacing-lg);
        box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
    }
    
    .footer-content {
        max-width: 1200px;
        margin: 0 auto;
        padding: 0 var(--el-spacing-lg);
    }
    
    .footer-main {
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        margin-bottom: var(--el-spacing-lg);
    }
    
    .footer-section {
        min-width: 200px;
        margin-bottom: var(--el-spacing-lg);
    }
    
    .footer-title {
        font-size: var(--el-font-size-large);
        color: var(--el-color-primary);
        margin-bottom: var(--el-spacing-md);
        font-weight: 600;
    }
    
    .footer-desc {
        color: var(--el-text-color-regular);
        line-height: 1.5;
    }
    
    .footer-contact {
        display: flex;
        align-items: center;
        margin-bottom: var(--el-spacing-sm);
        color: var(--el-text-color-regular);
    }
    
    .footer-contact i {
        margin-right: var(--el-spacing-sm);
        color: var(--el-color-primary);
    }
    
    .footer-copyright {
        border-top: 1px solid var(--el-border-color-lighter);
        padding: var(--el-spacing-md) 0;
        text-align: center;
        color: var(--el-text-color-secondary);
        font-size: var(--el-font-size-small);
    }
</style>