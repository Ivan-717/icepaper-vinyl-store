<script setup lang="ts">
import request from '@/api/request';
import { ref,onMounted,nextTick } from 'vue';
import * as echarts from 'echarts'


interface CategoryStat{
    categoryName:string
    count:number
}

interface ComboStat{
    name:string
    value:number
}

const loading=ref(false)

//商品统计
const productStats=ref<CategoryStat[]>([])
const comboStats=ref<ComboStat[]>([])

//加载数据
const loadStats=async()=>{
    loading.value=true
    try{
        //获取分类及商品数量
        const res1=await request.get('/categories')
        const categories=res1.data

        //获取所有商品
        const res2=await request.get('/admin/products')
        const products=res2.data

        //统计每个分类的商品数量
        const stats:CategoryStat[]=[]
        //遍历分类，统治每个分类有多少商品
        for(const cat of categories){
            //.length:筛选后的商品数量
            const count =products.filter((p:any)=>p.categoryId===cat.id).length
            //把{分类名+商品数量}存入stats数组
            stats.push({categoryName:cat.name,count})
        }
        productStats.value=stats

        //统计套餐状态
        const combosRes=await request.get('/admin/combo')
        const combos=combosRes.data

        console.log('combosRes:', combosRes)
        console.log('combosRes.data:', combosRes.data)
        console.log('类型:', typeof combosRes.data)
        console.log('是不是数组:', Array.isArray(combosRes.data))

        //计算上架套餐数量(status===1)
        const onCount=combos.filter((c:any)=>c.status===1).length
        //计算下架套餐数量(status===0)
        const offCount=combos.filter((c:any)=>c.status===0).length

        comboStats.value=[
            {name:'已上架',value:onCount},
            {name:'已下架',value:offCount}
        ]

        //数据准备好后，绘制图表 
        // 在 loadStats 最后
        setTimeout(() => {
          initCharts()
        }, 100)
        await nextTick() //等待dom更新
        initCharts()
    }catch(error){
        console.error('加载统计数据失败：',error)
    }finally{
        loading.value=false
    }

   
    
}

//初始化图标
const initCharts=()=>{
console.log('productStats:', productStats.value)
  console.log('comboStats:', comboStats.value)
  
    //1.商品分类统计柱状图
    //通过id查询，存着,告诉图画在哪里
    const productChartDom=document.getElementById('productChart')
     console.log('productChartDom:', productChartDom)
    if(productChartDom){
        //初始化Echarts实例
        const productChart=echarts.init(productChartDom)
        console.log('productChart 已初始化')

        //配置图标选项
        productChart.setOption({
            title:{
                text:'各分类商品数量',
                left:'center',
                textStyle:{fontSize:14}
            },
            tooltip:{
                trigger:'axis', //鼠标悬停时显示数据
                axisPointer:{type:'shadow'}
            },
            xAxis:{
                type:'category',
                data:productStats.value.map(v=>v.categoryName),  //x轴，分类名称
                name:'分类',
                axisLabel:{rotate:45} //如果名字太好，旋转45度
            },
            yAxis:{
                type:'value',
                name:'商品数量'
            },
            series:[{
                name:'商品数',
                type:'bar',  //柱状图
                data:productStats.value.map(v=>v.count), //数据：商品数量
                itemStyle:{
                    color:'#42b983',  //柱子颜色（绿色）
                    borderRadius:[4,4,0,0] //柱子顶部的圆角
                },
                label:{
                    show:true, //显示数值
                    position:'top' //数值显示在柱子顶部
                }
            }]
        })
        //窗口大小变化时，图表自动调整
        window.addEventListener('resize',()=>productChart.resize)
    }

    //2.套餐状态分布饼图
    const comboChartDom=document.getElementById('comboChart')
    if(comboChartDom){
        const comboChart=echarts.init(comboChartDom)

        comboChart.setOption({
            title: {
                text: '套餐状态分布',
                left: 'center',
                textStyle: { fontSize: 14 }
            },
             tooltip: {
                trigger: 'item',  // 鼠标悬停时显示饼图扇区信息
                formatter: '{b}: {d}%'  // 显示格式：名称: 百分比
            },
            series: [{
                name: '套餐状态',
                type: 'pie',  // 饼图
                radius: '50%',  // 半径
                data: comboStats.value,
                label: {
                show: true,
                formatter: '{b}: {d}%'  // 显示名称和百分比
                },
                emphasis: {
                    scale: true  // 鼠标悬停时扇区放大
                }
            }]  
        })
        window.addEventListener('resize', () => comboChart.resize())
    }
}

onMounted(()=>{
    loadStats()
})

</script>

<template>
    <div class="statistics">
        <h1>📊 数据统计</h1>

        <div v-if="loading" class="loading">
            ⏳ 加载中...
        </div> 

        <div v-else class="stats-grid">
            <div class="chart-card">
                <div id="productChart" class="chart"></div>
            </div>

            <div class="chart-card">
                <div id="comboChart" class="chart"></div>
            </div>
        </div>
        
    </div>

</template>

<style scoped>
.statistics {
  padding: 1rem;
}

.statistics h1 {
  margin-bottom: 2rem;
  color: #333;
}

/* 加载状态样式 */
.loading {
  text-align: center;
  padding: 3rem;
  color: #666;
}

/* 统计图表网格布局 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);  /* 固定两列，各占一半 */
  gap: 1.5rem;
}

/* 图表卡片样式 */
.chart-card {
  background: white;
  border-radius: 8px;
  padding: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

/* 图表容器 */
.chart {
  width: 100%;
  min-width: 300px;
  height: 400px;
}
</style>