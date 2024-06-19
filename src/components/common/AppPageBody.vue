<template>
    <div class="main-container">
        <div class="main-content">
            <slot></slot>
        </div>
    </div>
</template>

<script>
    export default {
        name: "PageBody",
    }
</script>

<style scoped>
    .main-container{
        margin-top: 90px;
        display: flex;
        justify-content: center;
        background-color: #BCDDDB;
    }
    .main-content{
        width: 70%;
        background-color: #BCDDDB;
        min-height: 90vh;
    }
</style>
