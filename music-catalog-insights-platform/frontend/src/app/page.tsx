import Link from "next/link";
import { BarChart3, Library, Search, Sparkles, ArrowRight } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";

const FEATURES = [
  {
    icon: Search,
    title: "Search the iTunes catalog",
    description:
      "Look up any song across the full iTunes library through a secure backend proxy - no direct client calls to Apple.",
  },
  {
    icon: Library,
    title: "Build your personal library",
    description:
      "Save songs you care about, rate them, and add personal notes. Duplicate entries are automatically prevented.",
  },
  {
    icon: BarChart3,
    title: "Explore rich analytics",
    description:
      "Six interactive charts break down your library by genre, artist, release era, ratings, and growth over time.",
  },
  {
    icon: Sparkles,
    title: "AI-powered listening insights",
    description:
      "Generate a Gemini-powered taste profile - favourite genres, mood analysis, and five tailored recommendations.",
  },
];

export default function LandingPage() {
  return (
    <div>
      <section className="border-b border-border">
        <div className="container-page flex flex-col items-center gap-6 py-24 text-center">
          <span className="rounded-full border border-border bg-surfaceMuted px-3 py-1 text-xs font-medium text-muted">
            Your music, your insights
          </span>
          <h1 className="max-w-3xl font-display text-4xl font-bold tracking-tight sm:text-5xl">
            Build a personal music library and turn it into{" "}
            <span className="text-primary">actionable insight</span>
          </h1>
          <p className="max-w-xl text-base text-muted sm:text-lg">
            Search millions of songs from the iTunes catalog, save the ones that matter to you,
            and let analytics and AI reveal patterns in your taste.
          </p>
          <div className="flex flex-wrap items-center justify-center gap-3">
            <Button size="lg" asChild>
              <Link href="/register">
                Create your library <ArrowRight className="h-4 w-4" />
              </Link>
            </Button>
            <Button size="lg" variant="outline" asChild>
              <Link href="/login">Log in</Link>
            </Button>
          </div>
        </div>
      </section>

      <section className="container-page py-20">
        <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
          {FEATURES.map((feature) => (
            <Card key={feature.title}>
              <CardHeader>
                <div className="mb-2 w-fit rounded-md bg-primary/15 p-2">
                  <feature.icon className="h-5 w-5 text-primary" />
                </div>
                <CardTitle>{feature.title}</CardTitle>
              </CardHeader>
              <CardContent>
                <CardDescription>{feature.description}</CardDescription>
              </CardContent>
            </Card>
          ))}
        </div>
      </section>
    </div>
  );
}
