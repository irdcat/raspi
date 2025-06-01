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
            data: Array.from(bodyweightMetrics.values()).map(m => m.avg)
        }, {
            name: "Average (Added weight)",
            type: 'line',
            data: Array.from(exerciseMetrics.values()).map(m => m.avg)
        }, {
            name: "Average (Overall)",
            type: 'line',
            data: summedAvg
        }, {
            name: "Min (Added weight)",
            type: 'line',
            data: Array.from(exerciseMetrics.values()).map(m => m.min)
        }, {
            name: "Min (Overall)",
            type: 'line',
            data: summedMin
        }, {
            name: "Max (Added weight)",
            type: 'line',
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
            data: Array.from(bodyweightMetrics.values()).map(m => m.avg)
        }, {
            name: "Average (Added weight)",
            type: 'line',
            data: Array.from(exerciseMetrics.values()).map(m => m.avg)
        }, {
            name: "Average (Overall)",
            type: 'line',
            data: summedAvgVolume
        }, {
            name: "Sum (Added weight)",
            type: 'line',
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

const VolumeMetricsChart = (props: {
    baseOptions: EChartsOption,
    parameters: Map<Date, VolumeMetrics>,
    bodyweight: Map<Date, VolumeMetrics>
}) => {
    const { baseOptions, parameters, bodyweight } = props;
    const options: EChartsOption = {
        ...baseOptions,
        title: {
            text: "Volume"
        },
        series: toVolumeSeries(parameters, bodyweight)
    }

    return <EChartsReact style={{ width: '100%', height: '100%' }} option={options} theme="dark"/>
}

const IntensityMetricsChart = (props: {
    baseOptions: EChartsOption,
    parameters: Map<Date, IntensityMetrics>, 
    bodyweight: Map<Date, IntensityMetrics> 
}) => {
    const { baseOptions, parameters, bodyweight } = props;
    const options: EChartsOption = {
        ...baseOptions,
        title: {
            text: "Intensity"
        },
        series: toIntensitySeries(parameters, bodyweight)
    }

    return <EChartsReact style={{ width: '100%', height: '100%' }} option={options} theme="dark"/>
}

const ExerciseMetricsChart = (props: {
    parameters: Map<Date, VolumeMetrics> | Map<Date, IntensityMetrics>,
    bodyweight: Map<Date, VolumeMetrics> | Map<Date, IntensityMetrics>
}) => {
    const { parameters, bodyweight } = props

    if (parameters.size === 0) {
        return <EmptyState title="No data found" message="Change the filters or hit the gym"/>
    }

    const baseOptions: EChartsOption = {
        grid: {
            top: 40,
            left: 65,
            right: 20,
            bottom: 25
        },
        legend: {
            show: true
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'cross'
            }
        },
        xAxis: {
            data: Array.from(parameters.keys()).map(d => format(d, "yyyy-MM-dd")),
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
    }

    if ("sum" in parameters.values().next().value!!) {
        const volumeParameters = parameters as Map<Date, VolumeMetrics>
        const volumeBodyweights = bodyweight as Map<Date, VolumeMetrics>
        return <VolumeMetricsChart
                    baseOptions={baseOptions} 
                    parameters={volumeParameters} 
                    bodyweight={volumeBodyweights}/>
    } else {
        const intensityParameters = parameters as Map<Date, IntensityMetrics>
        const intensityBodyweights = bodyweight as Map<Date, IntensityMetrics>
        return <IntensityMetricsChart 
                    baseOptions={baseOptions} 
                    parameters={intensityParameters} 
                    bodyweight={intensityBodyweights}/>
    }
}

export default ExerciseMetricsChart;