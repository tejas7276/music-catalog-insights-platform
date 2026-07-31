"use client";

import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import { CHART_GRID_COLOR, CHART_TEXT_COLOR, CHART_TOOLTIP_STYLE } from "./chartTheme";

interface Props {
  data: { genre: string; averageRating: number }[];
}

export function AverageRatingByGenreChart({ data }: Props) {
  return (
    <ResponsiveContainer width="100%" height={280}>
      <BarChart data={data} margin={{ left: 8 }}>
        <CartesianGrid strokeDasharray="3 3" stroke={CHART_GRID_COLOR} vertical={false} />
        <XAxis dataKey="genre" stroke={CHART_TEXT_COLOR} fontSize={11} interval={0} angle={-20} textAnchor="end" height={60} />
        <YAxis stroke={CHART_TEXT_COLOR} fontSize={12} domain={[0, 5]} />
        <Tooltip contentStyle={CHART_TOOLTIP_STYLE} cursor={{ fill: "rgba(232,179,76,0.08)" }} />
        <Bar dataKey="averageRating" fill="#F6AD55" radius={[4, 4, 0, 0]} name="Avg. rating" />
      </BarChart>
    </ResponsiveContainer>
  );
}
