import { EChartsOption, SeriesOption } from 'echarts';
import EChartsReact from 'echarts-for-react';
import ExerciseParameters from '../model/ExerciseParameters';

type ExerciseChartProps = {
  parameters: Map<Date, ExerciseParameters>;
  height?: string | number;
  width?: string | number;
};

const ExerciseChart = (props: ExerciseChartProps) => {
  const paramsToData = (params: ExerciseParameters[]): { [key: string]: number[] } => {
    const data: { [key: string]: number[] } = {};
    params.forEach((param) => {
      Object.keys(param).forEach((k) => {
        if (data[k] === undefined) {
          data[k] = [param[k as keyof ExerciseParameters]];
        } else {
          data[k].push(param[k as keyof ExerciseParameters]);
        }
      });
    });
    return data;
  };

  const formatFieldName = (name: string): string => {
    const result = name.replace(/([A-Z])/g, ' $1');
    return result.charAt(0).toUpperCase() + result.slice(1);
  };

  const dataToSeries = (data: { [key: string]: number[] }): SeriesOption[] => {
    const series: SeriesOption[] = [];
    for (const [key, value] of Object.entries(data)) {
      const formattedName = formatFieldName(key);
      series.push({
        name: formattedName,
        type: 'line',
        data: value,
      });
    }
    return series;
  };

  const dataToLegendData = (data: { [key: string]: number[] }): string[] => {
    const legendData: string[] = [];
    for (const key of Object.keys(data)) {
      const name = formatFieldName(key);
      legendData.push(name);
    }
    return legendData;
  };

  const parametersToEChartsOption = (params: Map<Date, ExerciseParameters>): EChartsOption => {
    const paramData = paramsToData(Object.values(params));
    return {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'cross',
        },
      },
      legend: {
        data: dataToLegendData(paramData),
        top: '20',
      },
      xAxis: {
        data: Object.keys(params),
        axisTick: {
          alignWithLabel: true,
        },
      },
      yAxis: {
        type: 'value',
        alignTicks: true,
        axisLabel: {
          formatter: '{value} kg',
        },
        axisLine: {
          show: true,
          lineStyle: {
            color: '#335656',
          },
        },
      },
      series: dataToSeries(paramData),
    };
  };

  const propsToStyle = (props: ExerciseChartProps): React.CSSProperties => {
    return {
      ...(props.height !== undefined && { height: props.height }),
      ...(props.width !== undefined && { width: props.width }),
    };
  };

  return (
    <EChartsReact style={propsToStyle(props)} option={parametersToEChartsOption(props.parameters)} theme={'dark'} />
  );
};

export default ExerciseChart;
