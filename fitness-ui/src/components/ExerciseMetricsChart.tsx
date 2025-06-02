import { EChartsOption, SeriesOption } from "echarts";
import { IntensityMetrics, VolumeMetrics } from "../types";
import EmptyState from "./EmptyState";
import EChartsReact from "echarts-for-react";
import { format } from "date-fns";

const toIntensitySeries =(
    exerciseMetrics: Map<Date, IntensityMetrics>, 
    bodyweightMetrics: Map<Date, IntensityMetrics>
): SeriesOption[] => {
    if (bodyweightMetrics.size !== 0) {
        const summedAvg = Array.from(exerciseMetrics.values())
            .map((m, i) => m.avg + Array.from(bodyweightMetrics.values())[i].avg);
        const summedMin = Array.from(exerciseMetrics.values())
            .map((m, i) => m.min + Array.from(bodyweightMetrics.values())[i].min);
        const summedMax = Array.from(exerciseMetrics.values())
            .map((m, i) => m.max + Array.from(bodyweightMetrics.values())[i].max);
        return [{
            name: "Bodyweight",
            type: 'line',
            stack: 'stack min',
            data: Array.from(bodyweightMetrics.values()).map(m => m.min),
        }, {
            name: "Bodyweight",
            type: 'line',
            stack: 'stack avg',
            tooltip: {
                show: false
            },
            data: Array.from(bodyweightMetrics.values()).map(m => m.min),
        }, {
            name: "Bodyweight",
            type: 'line',
            stack: 'stack max',
            tooltip: {
                show: false
            },
            data: Array.from(bodyweightMetrics.values()).map(m => m.min),
        }, {
            name: "Average (Added weight)",
            type: 'line',
            stack: 'stack avg',
            areaStyle: {},
            data: Array.from(exerciseMetrics.values()).map(m => m.avg)
        }, {
            name: "Average (Overall)",
            type: 'line',
            data: summedAvg
        }, {
            name: "Min (Added weight)",
            type: 'line',
            stack: 'stack min',
            areaStyle: {},
            data: Array.from(exerciseMetrics.values()).map(m => m.min)
        }, {
            name: "Min (Overall)",
            type: 'line',
            data: summedMin
        }, {
            name: "Max (Added weight)",
            type: 'line',
            stack: 'stack max',
            areaStyle: {},
            data: Array.from(exerciseMetrics.values()).map(m => m.max)
        }, {
            name: "Max (Overall)",
            type: 'line',
            data: summedMax
        }];
    } else {
        return [{
            name: "Average",
            type: 'line',
            data: Array.from(exerciseMetrics.values()).map(m => m.avg)
        }, {
            name: "Minimum",
            type: 'line',
            data: Array.from(exerciseMetrics.values()).map(m => m.min)
        }, {
            name: "Maximum",
            type: 'line',
            data: Array.from(exerciseMetrics.values()).map(m => m.max)
        }];
    }
}

const toVolumeSeries = (
    exerciseMetrics: Map<Date, VolumeMetrics>, 
    bodyweightMetrics: Map<Date, VolumeMetrics>
): SeriesOption[] => {
    if (bodyweightMetrics.size !== 0) {
        const summedAvgVolume = Array.from(exerciseMetrics.values())
            .map((m, i) => m.avg + Array.from(bodyweightMetrics.values())[i].avg)
        const summedSumVolume = Array.from(exerciseMetrics.values())
            .map((m, i) => m.sum + Array.from(bodyweightMetrics.values())[i].sum)
        return [{
            name: "Bodyweight",
            type: 'line',
            stack: 'stack avg',
            data: Array.from(bodyweightMetrics.values()).map(m => m.avg)
        }, {
            name: "Bodyweight",
            type: 'line',
            stack: 'stack sum',
            tooltip: {
                show: false
            },
            data: Array.from(bodyweightMetrics.values()).map(m => m.sum)
        }, {
            name: "Average (Added weight)",
            type: 'line',
            stack: 'stack avg',
            areaStyle: {},
            data: Array.from(exerciseMetrics.values()).map(m => m.avg)
        }, {
            name: "Average (Overall)",
            type: 'line',
            data: summedAvgVolume
        }, {
            name: "Sum (Added weight)",
            type: 'line',
            stack: 'stack sum',
            areaStyle: {},
            data: Array.from(exerciseMetrics.values()).map(m => m.sum)
        }, {
            name: "Sum (Overall)",
            type: 'line',
            data: summedSumVolume
        }];
    } else {
        return [{
            name: "Average",
            type: 'line',
            data: Array.from(exerciseMetrics.values()).map(m => m.avg)
        }, {
            name: "Sum",
            type: 'line',
            data: Array.from(exerciseMetrics.values()).map(m => m.sum)
        }];
    }
}


const ExerciseMetricsChart = (props: {
    volumeParameters: Map<Date, VolumeMetrics>,
    volumeBodyweight: Map<Date, VolumeMetrics>,
    intensityParameters: Map<Date, IntensityMetrics>,
    intensityBodyweight: Map<Date, IntensityMetrics>
}) => {
    const { volumeParameters, volumeBodyweight, intensityParameters, intensityBodyweight } = props

    if (volumeParameters.size === 0 || intensityParameters.size === 0) {
        return <EmptyState title="No data found" message="Change the filters or hit the gym"/>
    }

    const option: EChartsOption = {
        title: {
            text: "Volume & Intensity",
            left: 'center',
            top: 10
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                animation: false
            }
        },
        toolbox: {
            feature: {
                saveAsImage: {}
            },
            right: 10,
            top: 7
        },
        axisPointer: {
            link: [{
                xAxisIndex: 'all'
            }]
        },
        grid: [{
            left: 60,
            right: 50,
            height: '40%'
        }, {
            left: 60,
            right: 50,
            top: '55%',
            height: '40%'
        }],
        xAxis: [{
            type: 'category',
            boundaryGap: false,
            data: Array.from(volumeParameters.keys()).map(d => format(d, "yyyy-MM-dd")),
            axisTick: {
                alignWithLabel: true
            }
        }, {
            gridIndex: 1,
            type: 'category',
            boundaryGap: false,
            data: Array.from(intensityParameters.keys()).map(d => format(d, "yyyy-MM-dd")),
            axisTick: {
                alignWithLabel: true
            }
        }],
        yAxis: [{
            name: "Volume (kg)",
            type: 'value',
            axisLine: {
                show: true
            }
        }, {
            gridIndex: 1,
            name: "Intensity (kg)",
            type: 'value',
            axisLine: {
                show: true
            }
        }],
        series: toIntensitySeries(intensityParameters, intensityBodyweight)
            .map(s => ({ ...s, xAxisIndex: 1, yAxisIndex: 1} as SeriesOption))
            .concat(toVolumeSeries(volumeParameters, volumeBodyweight))
    };

    return <EChartsReact option={option} style={{ width: '100%', height: '100%' }} theme="dark"/>
}

export default ExerciseMetricsChart;