import { EChartsOption } from "echarts"
import EChartsReact from "echarts-for-react"
import EmptyState from "./EmptyState"

const BodyweightChart = (props: { data: Map<Date, number> }) => {
    const { data } = props
    
    let options: EChartsOption = {
        grid: {
            top: 20,
            left: 60,
            right: 25,
            bottom: 25
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'cross'
            }
        },
        xAxis: {
            data: Object.keys(data),
            axisTick: {
                alignWithLabel: true
            }
        },
        yAxis: {
            type: 'value',
            alignTicks: true,
            axisLabel: {
                formatter: "{value} kg"
            },
            axisLine: {
                show: true,
                lineStyle: {
                    color: '#335656'
                }
            },
            min: (value) => Math.ceil(value.min) - 1,
            interval: 1
        },
        series: {
            name: 'Body Weight',
            type: 'line',
            data: Object.values(data)
        }
    }

    if (data.size === 0) {
        return <EmptyState title="No data found" message="Change the filters of hit the gym"/>
    }

    return (
        <EChartsReact 
            style={{ width: '100%', height: '100%' }} 
            option={options} 
            theme="dark"/>
    )
}

export default BodyweightChart;