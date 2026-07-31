"use client";

import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import { CHART_GRID_COLOR, CHART_TEXT_COLOR, CHART_TOOLTIP_STYLE } from "./chartTheme";

interface Props {
  data: { year: string; count: number }[];
}

export function ReleaseYearChart({ data }: Props) {
  return (
    <ResponsiveContainer width="100%" height={280}>
      <BarChart data={data} margin={{ left: 8 }}>
        <CartesianGrid strokeDasharray="3 3" stroke={CHART_GRID_COLOR} vertical={false} />
        <XAxis dataKey="year" stroke={CHART_TEXT_COLOR} fontSize={11} interval="preserveStartEnd" />
        <YAxis stroke={CHART_TEXT_COLOR} fontSize={12} allowDecimals={false} />
        <Tooltip contentStyle={CHART_TOOLTIP_STYLE} cursor={{ fill: "rgba(45,212,191,0.08)" }} />
        <Bar dataKey="count" fill="#63B3ED" radius={[4, 4, 0, 0]} />
      </BarChart>
    </ResponsiveContainer>
  );
}
