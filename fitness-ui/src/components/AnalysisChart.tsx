import { EChartsOption, SeriesOption } from "echarts"
import EChartsReact from "echarts-for-react"
import { Metric, Summary } from "../types"
import StringUtils from "../utils/StringUtils"

const AnalysisChart = (props: { 
    data: Summary,
    metric: Metric,
}) => {
    
    const summaryToSeries = (summary: Summary, metric: Metric): SeriesOption[] => {
        let series: SeriesOption[] = [];
        let metricData: number[] = [];

        switch (metric) {
            case "volume":
                metricData = Object.values(summary.parameters).map(p => p.volume);
                break;
            case "averageVolume":
                metricData = Object.values(summary.parameters).map(p => p.averageVolume);
                break;
            case "minIntensity":
                metricData = Object.values(summary.parameters).map(p => p.minIntensity);
                break;
            case "maxIntensity":
                metricData = Object.values(summary.parameters).map(p => p.maxIntensity);
                break;
            case "averageIntensity":
                metricData = Object.values(summary.parameters).map(p => p.averageIntensity);
                break;
        }

        if (summary.exercise.isBodyweight) {
            let bodyweightData: number[] = [];
            if (metric === "volume" || metric === "averageVolume") {
                if (metric === "volume") {
                    bodyweightData = Object.values(summary.parameters).map(p => p.bodyweightVolume);
                } else {
                    bodyweightData = Object.values(summary.parameters).map(p => p.averageBodyweightVolume)
                }
            } else {
                bodyweightData = Object.values(summary.parameters).map(p => p.bodyweight);
            }
            let summedData: number[] = [];
            for (let i = 0; i < metricData.length; i++) {
                summedData[i] = metricData[i] + bodyweightData[i];
            }
            series = [{
                name: `Bodyweight${metric === "volume" || metric === "averageVolume" ? ' Volume' : ''}`,
                type: 'line',
                stack: 'stack',
                data: bodyweightData
            }, {
                name: `Added weight${metric === "volume" || metric === "averageVolume" ? ' volume' : ''}`,
                type: 'line',
                stack: 'stack',
                data: metricData
            }, {
                name: "Overall weight",
                type: 'line',
                data: summedData
            }]
        } else {
            series = [{
                name: StringUtils.camelCaseToSpaced(metric),
                type: 'line',
                data: metricData
            }]
        }

        return series;
    }

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
            data: Object.keys(props.data.parameters),
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
        series: summaryToSeries(props.data, props.metric)
    }

    return (
        <EChartsReact 
            style={{ width: '100%', height: '100%' }} 
            option={options} 
            theme="dark"/>
    )
}

export default AnalysisChart;