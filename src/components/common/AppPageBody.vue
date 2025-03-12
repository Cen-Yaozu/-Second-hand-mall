<template>
    <div class="page-body">
        <div class="page-content">
            <slot></slot>
        </div>
    </div>
</template>

<script>
    export default {
        name: "AppPageBody",
    }
</script>

<style scoped>
    .page-body {
        margin-top: 60px;
        min-height: calc(100vh - 60px);
        background-color: var(--el-bg-color-page);
        display: flex;
        flex-direction: column;
        align-items: center;
    }
    
    .page-content {
        width: 100%;
        max-width: 1200px;
        flex: 1;
        background-color: var(--el-bg-color);
        box-shadow: var(--el-box-shadow-light);
        border-radius: var(--el-border-radius-base);
        overflow: hidden;
    }
</style>
