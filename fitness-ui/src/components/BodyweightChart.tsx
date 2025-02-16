import { EChartsOption } from "echarts"
import EChartsReact from "echarts-for-react"

const BodyweightChart = (props: { data: { [date: string]: number } }) => {
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
            data: Object.keys(props.data),
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
            min: (value) => value.min - 1,
            max: (value) => value.max + 1,
            interval: 1
        },
        series: {
            name: 'Body Weight',
            type: 'line',
            data: Object.values(props.data)
        }
    }

    return (
        <EChartsReact 
            style={{ width: '100%', height: '100%' }} 
            option={options} 
            theme="dark"/>
    )
}

export default BodyweightChart;