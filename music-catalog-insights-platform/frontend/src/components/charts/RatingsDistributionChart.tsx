"use client";

import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import { CHART_GRID_COLOR, CHART_TEXT_COLOR, CHART_TOOLTIP_STYLE } from "./chartTheme";

interface Props {
  data: { rating: number; count: number }[];
}

export function RatingsDistributionChart({ data }: Props) {
  const formatted = data.map((item) => ({ ...item, label: `${item.rating} star${item.rating > 1 ? "s" : ""}` }));

  return (
    <ResponsiveContainer width="100%" height={280}>
      <BarChart data={formatted} margin={{ left: 8 }}>
        <CartesianGrid strokeDasharray="3 3" stroke={CHART_GRID_COLOR} vertical={false} />
        <XAxis dataKey="label" stroke={CHART_TEXT_COLOR} fontSize={12} />
        <YAxis stroke={CHART_TEXT_COLOR} fontSize={12} allowDecimals={false} />
        <Tooltip contentStyle={CHART_TOOLTIP_STYLE} cursor={{ fill: "rgba(139,124,246,0.08)" }} />
        <Bar dataKey="count" fill="#8B7CF6" radius={[4, 4, 0, 0]} />
      </BarChart>
    </ResponsiveContainer>
  );
}
