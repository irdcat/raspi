import { EChartsOption } from "echarts"
import EChartsReact from "echarts-for-react"

const AnalysisChart = (props: { 
    data: { 
        [date: string]: {
            bodyweight: number,
            volume: number,
            averageVolume: number,
            minIntensity: number,
            maxIntensity: number,
            averageIntensity: number 
        }
    },
    metric: "Volume" | "Intensity",
    isBodyweight: boolean
}) => {
    let options: EChartsOption = {
        grid: {
            top: 25,
            left: 60,
            right: 20,
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
            }
        },
        series: props.metric === "Volume" ? [{
            name: "Volume",
            type: 'line',
            data: Object.values(props.data).map(d => d.volume)
        }, {
            name: "Average volume",
            type: 'line',
            data: Object.values(props.data).map(d => d.averageVolume)
        }] : [{
            name: "Average intensity",
            type: 'line',
            stack: props.isBodyweight ? 'stack' : undefined,
            areaStyle: props.isBodyweight ? {} : undefined,
            data: Object.values(props.data).map(d => d.averageIntensity)
        }, {
            name: "Bodyweight",
            type: 'line',
            stack: props.isBodyweight ? 'stack' : undefined,
            areaStyle: props.isBodyweight ? {} : undefined,
            data: Object.values(props.data).map(d => d.bodyweight)
        }]
    }

    return (
        <EChartsReact 
            style={{ width: '100%', height: '100%' }} 
            option={options} 
            theme="dark"/>
    )
}

export default AnalysisChart;