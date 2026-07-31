"use client";

import {
  CartesianGrid,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import { CHART_GRID_COLOR, CHART_TEXT_COLOR, CHART_TOOLTIP_STYLE } from "./chartTheme";

interface Props {
  data: { date: string; count: number }[];
}

export function SongsOverTimeChart({ data }: Props) {
  return (
    <ResponsiveContainer width="100%" height={280}>
      <LineChart data={data} margin={{ left: 8, right: 16 }}>
        <CartesianGrid strokeDasharray="3 3" stroke={CHART_GRID_COLOR} />
        <XAxis dataKey="date" stroke={CHART_TEXT_COLOR} fontSize={11} />
        <YAxis stroke={CHART_TEXT_COLOR} fontSize={12} allowDecimals={false} />
        <Tooltip contentStyle={CHART_TOOLTIP_STYLE} />
        <Line
          type="monotone"
          dataKey="count"
          stroke="#2DD4BF"
          strokeWidth={2}
          dot={{ r: 3, fill: "#2DD4BF" }}
          activeDot={{ r: 5 }}
          name="Songs added"
        />
      </LineChart>
    </ResponsiveContainer>
  );
}
