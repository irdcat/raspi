import { Box, Paper, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import ExercisesApi from "../api/ExercisesApi";
import useWindowDimensions from "../hooks/useWindowDimensions";
import TrainingsApi from "../api/TrainingsApi";
import EChartsReact from "echarts-for-react";
import { Exercise, ExerciseParameters, ExerciseSummary } from "../types";
import { EChartsOption, SeriesOption } from "echarts";

type ExerciseDetailsData = {
    exercise: Exercise,
    summary: ExerciseSummary
}

const ExerciseDetails = () => {
    const { id } = useParams()
    const { height } = useWindowDimensions()
    const [ data, setData ] = useState<ExerciseDetailsData>({
        exercise: { id: "", name: "", isBodyWeight: false },
        summary: { id: "", parameters: new Map() }
    }) 

    const paramsToData = (params: ExerciseParameters[]): { [key: string]: number[] } => {
        const data: { [key: string]: number[] } = {};
        params.forEach((param) => {
            Object.keys(param).forEach(k => {
                if (data[k] === undefined) {
                    data[k] = [param[k as keyof ExerciseParameters]]
                } else {
                    data[k].push(param[k as keyof ExerciseParameters])
                }
            })
        })
        return data;
    }

    const formatFieldName = (name: string): string => {
        const result = name.replace(/([A-Z])/g, ' $1');
        return result.charAt(0).toUpperCase() + result.slice(1);
    }

    const dataToSeries = (data: { [key: string]: number[] }): SeriesOption[] => {
        const series: SeriesOption[] = [];
        for (const [key, value] of Object.entries(data)) {
            const formattedName = formatFieldName(key);
            series.push({
                name: formattedName,
                type: 'line',
                data: value,
            })
        }
        return series;
    }

    const dataToLegendData = (data: { [key: string]: number[] }): string[] => {
        const legendData: string[] = [];
        for (const key of Object.keys(data)) {
            const name = formatFieldName(key);
            legendData.push(name)
        }
        return legendData;
    }

    const summaryToEChartsOption = (data: ExerciseDetailsData): EChartsOption => {
        const name = data.exercise.name;
        const params = data.summary.parameters;
        const paramData = paramsToData(Object.values(params));
        return {
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'cross'
                }
            },
            legend: {
                data: dataToLegendData(paramData)
            },
            xAxis: {
                data: Object.keys(params),
                axisTick: {
                    alignWithLabel: true
                }
            },
            yAxis: {
                type: 'value',
                alignTicks: true,
                maxInterval: 50,
                axisLabel: {
                    formatter: "{value} kg"
                }
            },
            series: dataToSeries(paramData)
        };
    }

    useEffect(() => {
        async function fetchData() {
            if (id !== undefined) {
                const from = new Date(Date.UTC(2022, 0, 1));
                const to = new Date(Date.UTC(2022, 3, 1));

                const e = await ExercisesApi.getById(id);
                const s = await TrainingsApi
                    .getSummary({ 
                        exerciseIds: [id], 
                        from: from,
                        to: to 
                    })
                    .then(s => s[0]);
                setData({exercise: e, summary: s});
            }
        }
        fetchData();
    }, [id])

    return (
        <Box sx={{ px: 3 }}>
            <Box sx={{ display: "flex", paddingY: 2, paddingX: 1 }}>
                <Typography variant="h6" color="white" sx={{ flexGrow: 1 }}>
                    { data.exercise.name }
                </Typography>
            </Box>
            <Paper sx={{ height: height - 160 }}>
                <EChartsReact 
                    style={{ height: "100%" }} 
                    option={summaryToEChartsOption(data)}
                    theme={'dark'}/>
            </Paper>
        </Box>
    )
}

export default ExerciseDetails;