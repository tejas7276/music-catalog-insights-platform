"use client";

import { useState } from "react";
import { toast } from "sonner";
import { Sparkles, Wand2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { LoadingSpinner } from "@/components/common/LoadingState";
import { aiApi } from "@/lib/api/ai";
import { AiInsightsResponse } from "@/lib/types";

export function AiInsightsPanel() {
  const [insights, setInsights] = useState<AiInsightsResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleGenerate() {
    setIsLoading(true);
    setError(null);
    try {
      const response = await aiApi.generateInsights();
      setInsights(response);
    } catch (err) {
      const message = err instanceof Error ? err.message : "Failed to generate AI insights";
      setError(message);
      toast.error(message);
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <Card>
      <CardHeader className="flex-row items-center justify-between">
        <div>
          <CardTitle className="flex items-center gap-2">
            <Sparkles className="h-5 w-5 text-primary" /> Music Taste Insights
          </CardTitle>
          <CardDescription>
            AI-generated analysis of your saved library, powered by Gemini.
          </CardDescription>
        </div>
        <Button onClick={handleGenerate} disabled={isLoading}>
          <Wand2 className="h-4 w-4" />
          {isLoading ? "Analyzing..." : insights ? "Regenerate" : "Generate insights"}
        </Button>
      </CardHeader>
      <CardContent>
        {isLoading && <LoadingSpinner label="Analyzing your library with Gemini..." />}

        {!isLoading && error && (
          <p className="rounded-md border border-danger/30 bg-danger/5 p-4 text-sm text-danger">{error}</p>
        )}

        {!isLoading && !error && !insights && (
          <p className="text-sm text-muted">
            Click &quot;Generate insights&quot; to get a listening summary, mood analysis, and five
            personalized recommendations based only on the songs in your library.
          </p>
        )}

        {!isLoading && insights && (
          <div className="flex flex-col gap-6">
            <section>
              <h4 className="mb-1 font-display text-sm font-semibold text-foreground">Listening summary</h4>
              <p className="text-sm text-muted">{insights.listeningSummary}</p>
            </section>

            <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
              <section>
                <h4 className="mb-2 font-display text-sm font-semibold text-foreground">Favourite genres</h4>
                <div className="flex flex-wrap gap-1.5">
                  {insights.favouriteGenres.map((genre) => (
                    <Badge key={genre}>{genre}</Badge>
                  ))}
                </div>
              </section>
              <section>
                <h4 className="mb-2 font-display text-sm font-semibold text-foreground">Favourite artists</h4>
                <div className="flex flex-wrap gap-1.5">
                  {insights.favouriteArtists.map((artist) => (
                    <Badge key={artist} variant="accent">
                      {artist}
                    </Badge>
                  ))}
                </div>
              </section>
            </div>

            <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
              <section>
                <h4 className="mb-1 font-display text-sm font-semibold text-foreground">Mood analysis</h4>
                <p className="text-sm text-muted">{insights.moodAnalysis}</p>
              </section>
              <section>
                <h4 className="mb-1 font-display text-sm font-semibold text-foreground">Release era preference</h4>
                <p className="text-sm text-muted">{insights.releaseEraPreference}</p>
              </section>
            </div>

            <section>
              <h4 className="mb-2 font-display text-sm font-semibold text-foreground">Recommended for you</h4>
              <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
                {insights.recommendations.map((rec) => (
                  <div key={`${rec.title}-${rec.artist}`} className="rounded-md border border-border bg-surfaceMuted p-3">
                    <p className="text-sm font-medium text-foreground">{rec.title}</p>
                    <p className="text-xs text-muted">{rec.artist}</p>
                    <p className="mt-1 text-xs text-muted">{rec.reason}</p>
                  </div>
                ))}
              </div>
            </section>

            <section>
              <h4 className="mb-1 font-display text-sm font-semibold text-foreground">Personalized suggestions</h4>
              <p className="text-sm text-muted">{insights.personalizedSuggestions}</p>
            </section>

            <p className="text-xs text-muted">
              Based on {insights.librarySizeAnalyzed} saved song{insights.librarySizeAnalyzed === 1 ? "" : "s"}.
            </p>
          </div>
        )}
      </CardContent>
    </Card>
  );
}
