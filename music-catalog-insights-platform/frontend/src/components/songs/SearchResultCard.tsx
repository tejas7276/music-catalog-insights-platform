"use client";

import Image from "next/image";
import { Music2, PlusCircle, Check } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { formatDate, formatDuration } from "@/lib/utils";
import { SongSearchResult } from "@/lib/types";

interface SearchResultCardProps {
  song: SongSearchResult;
  isSaved: boolean;
  isSaving: boolean;
  onSave: (song: SongSearchResult) => void;
}

export function SearchResultCard({ song, isSaved, isSaving, onSave }: SearchResultCardProps) {
  return (
    <Card className="flex flex-col gap-3">
      <div className="flex gap-3">
        <div className="relative h-16 w-16 shrink-0 overflow-hidden rounded-md bg-surfaceMuted">
          {song.artworkUrl ? (
            <Image src={song.artworkUrl} alt={song.title} fill sizes="64px" className="object-cover" />
          ) : (
            <div className="flex h-full w-full items-center justify-center">
              <Music2 className="h-6 w-6 text-muted" />
            </div>
          )}
        </div>
        <div className="min-w-0 flex-1">
          <p className="truncate font-medium text-foreground">{song.title}</p>
          <p className="truncate text-sm text-muted">{song.artistName}</p>
          <div className="mt-1 flex flex-wrap items-center gap-1.5">
            {song.genre && <Badge variant="secondary">{song.genre}</Badge>}
            <span className="text-xs text-muted">{formatDuration(song.durationMillis)}</span>
          </div>
        </div>
      </div>

      <div className="flex items-center justify-between">
        <span className="text-xs text-muted">{formatDate(song.releaseDate)}</span>
        <Button
          size="sm"
          variant={isSaved ? "secondary" : "default"}
          disabled={isSaved || isSaving}
          onClick={() => onSave(song)}
        >
          {isSaved ? (
            <>
              <Check className="h-4 w-4" /> Saved
            </>
          ) : (
            <>
              <PlusCircle className="h-4 w-4" /> {isSaving ? "Saving..." : "Save"}
            </>
          )}
        </Button>
      </div>
    </Card>
  );
}
