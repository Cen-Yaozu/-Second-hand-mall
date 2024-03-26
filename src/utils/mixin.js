/*屏幕自适应函数*/

// * 默认缩放值
const scale = {
    width: '1',
    height: '1',
}

// * 设计稿尺寸（px）
const baseWidth = 1920
const baseHeight = 1080
// * 需保持的比例（默认1.77778）
const baseProportion = parseFloat((baseWidth / baseHeight).toFixed(5))

export default {
    data() {
        return {
            // * 定时函数
            drawTiming: null
        }
    },
    mounted () {
        //加载后先计算一遍缩放比例
        this.calcRate()
        //生成一个监听器：窗口发生变化从新计算计算一遍缩放比例
        window.addEventListener('resize', this.resize)
    },
    beforeDestroy () {
        window.removeEventListener('resize', this.resize)
    },
    methods: {
        calcRate () {
            //拿到整个页面元素
            const appRef = this.$refs["zoom"]
            //如果没有值就结束
            if (!appRef) return
            // 当前宽高比
            const currentRate = parseFloat((window.innerWidth / window.innerHeight).toFixed(5))
            //判断：如果有值代表页面变化了
            if (appRef) {
                //判断当前宽高比例是否大于默认比例
                if (currentRate > baseProportion) {
                    scale.width = ((window.innerHeight * baseProportion) / baseWidth).toFixed(5)
                    scale.height = (window.innerHeight / baseHeight).toFixed(5)
                    //整个页面的元素样式，缩放宽高用当前同比例放大的宽高
                    appRef.style.transform = `scale(${scale.width}, ${scale.height}) translate(-50%, -50%)`
                } else {
                    scale.height = ((window.innerWidth / baseProportion) / baseHeight).toFixed(5)
                    scale.width = (window.innerWidth / baseWidth).toFixed(5)
                    appRef.style.transform = `scale(${scale.width}, ${scale.height}) translate(-50%, -50%)`
                }
            }
        },
        resize () {
            //先清除计时器
            clearTimeout(this.drawTiming)
            //开启计时器
            this.drawTiming = setTimeout(() => {
                this.calcRate()
            }, 200)
        }
    },
}
