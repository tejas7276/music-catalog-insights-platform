import { Music4, Star, Users, Tag, Trophy, Clock } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { AnalyticsResponse } from "@/lib/types";

interface Props {
  summary: AnalyticsResponse["summaryCards"];
}

export function SummaryCards({ summary }: Props) {
  const cards = [
    {
      icon: Music4,
      label: "Total songs",
      value: summary.totalSongs.toString(),
    },
    {
      icon: Star,
      label: "Average rating",
      value: summary.averageRating != null ? summary.averageRating.toFixed(2) : "N/A",
    },
    {
      icon: Users,
      label: "Unique artists",
      value: summary.uniqueArtists.toString(),
    },
    {
      icon: Tag,
      label: "Unique genres",
      value: summary.uniqueGenres.toString(),
    },
    {
      icon: Trophy,
      label: "Highest rated",
      value: summary.highestRatedSong
        ? `${summary.highestRatedSong.title} (${summary.highestRatedSong.userRating}★)`
        : "N/A",
    },
    {
      icon: Clock,
      label: "Latest added",
      value: summary.latestAddedSong ? summary.latestAddedSong.title : "N/A",
    },
  ];

  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6">
      {cards.map((card) => (
        <Card key={card.label} className="p-4">
          <CardContent className="flex flex-col gap-2 p-0">
            <card.icon className="h-4 w-4 text-primary" />
            <p className="truncate text-sm font-medium text-foreground" title={card.value}>
              {card.value}
            </p>
            <p className="text-xs text-muted">{card.label}</p>
          </CardContent>
        </Card>
      ))}
    </div>
  );
}
