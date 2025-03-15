import { EChartsOption, SeriesOption } from "echarts"
import EChartsReact from "echarts-for-react"
import { Metric, Summary } from "../types"
import { camelCaseToSpaced } from "../utils/stringUtils"
import EmptyState from "./EmptyState"

const ExerciseAnalysisChart = (props: {
    data: Summary,
    metric: Metric,
    baseOptions: EChartsOption
}) => {

    const { data, metric, baseOptions } = props;
    const toSeries = (summary: Summary, metric: Metric): SeriesOption[] => {

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
        return [{
            name: camelCaseToSpaced(metric),
            type: 'line',
            data: metricData
        }]
    };

    let options: EChartsOption = {
        ...baseOptions,
        series: toSeries(data, metric)
    };

    return (
        <EChartsReact 
            style={{ width: '100%', height: '100%' }} 
            option={options} 
            theme="dark"/>
    );
}

const BodyweightExerciseAnalysisChart = (props: {
    data: Summary,
    metric: Metric,
    baseOptions: EChartsOption
}) => {

    const { data, metric, baseOptions } = props;
    const toSeries = (summary: Summary, metric: Metric): SeriesOption[] => {
        let metricData: number[] = [];
        let bodyweightData: number[] = [];
        let summedData: number[] = [];

        switch(metric) {
            case "volume":
                metricData = Object.values(summary.parameters).map(p => p.volume);
                bodyweightData = Object.values(summary.parameters).map(p => p.bodyweightVolume)
                break;
            case "averageVolume":
                metricData = Object.values(summary.parameters).map(p => p.averageVolume);
                bodyweightData = Object.values(summary.parameters).map(p => p.averageBodyweightVolume)
                break;
            case "minIntensity":
                metricData = Object.values(summary.parameters).map(p => p.minIntensity);
                bodyweightData = Object.values(summary.parameters).map(p => p.bodyweight);
                break;
            case "maxIntensity":
                metricData = Object.values(summary.parameters).map(p => p.maxIntensity);
                bodyweightData = Object.values(summary.parameters).map(p => p.bodyweight);
                break;
            case "averageIntensity":
                metricData = Object.values(summary.parameters).map(p => p.averageIntensity);
                bodyweightData = Object.values(summary.parameters).map(p => p.bodyweight);
                break;
        }

        summedData = metricData.map((v, i) => v + bodyweightData[i]);

        return [{
            name: `Bodyweight${metric === "volume" || metric === "averageVolume" ? ' volume' : ''}`,
            type: 'line',
            stack: 'stack',
            data: bodyweightData
        }, {
            name: `Added weight${metric === "volume" || metric === "averageVolume" ? ' volume' : ''}`,
            type: 'line',
            stack: 'stack',
            areaStyle: {},
            data: metricData
        }, {
            name: `Overall weight${metric === "volume" || metric === "averageVolume" ? ' volume' : ''}`,
            type: 'line',
            data: summedData
        }];
    };

    let options: EChartsOption = {
        ...baseOptions,
        series: toSeries(data, metric)
    };

    return (
        <EChartsReact 
            style={{ width: '100%', height: '100%' }} 
            option={options} 
            theme="dark"/>
    );
}

const AnalysisChart = (props: { 
    data: Summary,
    metric: Metric,
}) => {

    const { data, metric } = props;
    const baseOptions: EChartsOption = {
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
                show: true
            }
        }
    };

    if (data.parameters.size === 0) {
        return <EmptyState title="No data found" message="Change the filters or hit the gym"/>
    } else if (data.exercise.isBodyweight) {
        return <BodyweightExerciseAnalysisChart data={data} metric={metric} baseOptions={baseOptions}/>
    } else {
        return <ExerciseAnalysisChart data={data} metric={metric} baseOptions={baseOptions}/>
    }
}

export default AnalysisChart;