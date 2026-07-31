"use client";

import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import { CHART_GRID_COLOR, CHART_TEXT_COLOR, CHART_TOOLTIP_STYLE } from "./chartTheme";

interface Props {
  data: { artist: string; count: number }[];
}

export function TopArtistsChart({ data }: Props) {
  return (
    <ResponsiveContainer width="100%" height={280}>
      <BarChart data={data} layout="vertical" margin={{ left: 24 }}>
        <CartesianGrid strokeDasharray="3 3" stroke={CHART_GRID_COLOR} horizontal={false} />
        <XAxis type="number" stroke={CHART_TEXT_COLOR} fontSize={12} allowDecimals={false} />
        <YAxis
          type="category"
          dataKey="artist"
          stroke={CHART_TEXT_COLOR}
          fontSize={12}
          width={110}
          tick={{ fill: CHART_TEXT_COLOR }}
        />
        <Tooltip contentStyle={CHART_TOOLTIP_STYLE} cursor={{ fill: "rgba(232,179,76,0.08)" }} />
        <Bar dataKey="count" fill="#E8B34C" radius={[0, 4, 4, 0]} />
      </BarChart>
    </ResponsiveContainer>
  );
}
