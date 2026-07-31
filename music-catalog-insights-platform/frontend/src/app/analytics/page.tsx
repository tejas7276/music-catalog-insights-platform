"use client";

import { useCallback, useEffect, useState } from "react";
import { BarChart3 } from "lucide-react";
import Link from "next/link";
import { useRequireAuth } from "@/lib/hooks/useRequireAuth";
import { analyticsApi } from "@/lib/api/analytics";
import { Card, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { EmptyState } from "@/components/common/EmptyState";
import { ErrorState } from "@/components/common/ErrorState";
import { LoadingSpinner } from "@/components/common/LoadingState";
import { SummaryCards } from "@/components/analytics/SummaryCards";
import { AiInsightsPanel } from "@/components/analytics/AiInsightsPanel";
import { GenreDistributionChart } from "@/components/charts/GenreDistributionChart";
import { TopArtistsChart } from "@/components/charts/TopArtistsChart";
import { SongsOverTimeChart } from "@/components/charts/SongsOverTimeChart";
import { RatingsDistributionChart } from "@/components/charts/RatingsDistributionChart";
import { ReleaseYearChart } from "@/components/charts/ReleaseYearChart";
import { AverageRatingByGenreChart } from "@/components/charts/AverageRatingByGenreChart";
import { AnalyticsResponse } from "@/lib/types";

export default function AnalyticsPage() {
  const { isLoading: authLoading, isAuthenticated } = useRequireAuth();

  const [analytics, setAnalytics] = useState<AnalyticsResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadAnalytics = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await analyticsApi.getAnalytics();
      setAnalytics(response);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load analytics");
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    if (isAuthenticated) {
      loadAnalytics();
    }
  }, [isAuthenticated, loadAnalytics]);

  if (authLoading || !isAuthenticated) return null;

  return (
    <div className="container-page py-10">
      <div className="mb-8">
        <h1 className="font-display text-2xl font-semibold">Analytics dashboard</h1>
        <p className="mt-1 text-sm text-muted">Insights and trends drawn from your saved library.</p>
      </div>

      {isLoading && <LoadingSpinner label="Crunching your library data..." />}

      {!isLoading && error && <ErrorState message={error} onRetry={loadAnalytics} />}

      {!isLoading && !error && analytics && analytics.summaryCards.totalSongs === 0 && (
        <EmptyState
          icon={BarChart3}
          title="No analytics yet"
          description="Save a few songs to your library to unlock charts and AI-powered insights."
          action={
            <Button asChild>
              <Link href="/search">Search for songs</Link>
            </Button>
          }
        />
      )}

      {!isLoading && !error && analytics && analytics.summaryCards.totalSongs > 0 && (
        <div className="flex flex-col gap-8">
          <SummaryCards summary={analytics.summaryCards} />

          <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
            <Card>
              <CardHeader>
                <CardTitle>Genre distribution</CardTitle>
                <CardDescription>Share of your library by genre</CardDescription>
              </CardHeader>
              <GenreDistributionChart data={analytics.genreDistribution} />
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Top artists</CardTitle>
                <CardDescription>Your 10 most-saved artists</CardDescription>
              </CardHeader>
              <TopArtistsChart data={analytics.topArtists} />
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Songs added over time</CardTitle>
                <CardDescription>Library growth by day</CardDescription>
              </CardHeader>
              <SongsOverTimeChart data={analytics.songsAddedOverTime} />
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Ratings distribution</CardTitle>
                <CardDescription>How you rate the songs you save</CardDescription>
              </CardHeader>
              <RatingsDistributionChart data={analytics.ratingsDistribution} />
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Release year distribution</CardTitle>
                <CardDescription>Which eras dominate your library</CardDescription>
              </CardHeader>
              <ReleaseYearChart data={analytics.releaseYearDistribution} />
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Average rating by genre</CardTitle>
                <CardDescription>Which genres you rate the highest</CardDescription>
              </CardHeader>
              <AverageRatingByGenreChart data={analytics.averageRatingByGenre} />
            </Card>
          </div>

          <AiInsightsPanel />
        </div>
      )}
    </div>
  );
}
