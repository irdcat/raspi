import { EChartsOption } from "echarts"
import EChartsReact from "echarts-for-react"
import EmptyState from "./EmptyState"
import { addDays, format, parse } from "date-fns"

const BodyweightChart = (props: { 
    data: { [key: string]: number }, 
    fillGaps: boolean, 
}) => {
    const { data, fillGaps } = props
    
    let finalData: { [key: string]: number | null } = data;
    if (fillGaps && Object.keys(data).length > 0) {
        const tempData: { [key: string]: number | null } = {};
        const dateStrings = Object.keys(data).sort();
        const firstDate = parse(dateStrings[0], "yyyy-MM-dd", new Date());
        const lastDate = parse(dateStrings[dateStrings.length - 1], "yyyy-MM-dd", new Date());

        let currentDate = firstDate;
        
        while (currentDate <= lastDate) {
            const dateString = format(currentDate, "yyyy-MM-dd");
            tempData[dateString] = dateStrings.includes(dateString) ? data[dateString] : null;
            currentDate = addDays(currentDate, 1);
        }
        
        finalData = tempData;
    }

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
            data: Object.keys(finalData),
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
            data: Object.values(finalData),
            connectNulls: fillGaps
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