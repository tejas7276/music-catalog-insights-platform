"use client";

import Image from "next/image";
import { Music2, Pencil, Trash2 } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { StarRating } from "@/components/common/StarRating";
import { formatDate, formatDuration } from "@/lib/utils";
import { SavedSong } from "@/lib/types";

interface SavedSongCardProps {
  song: SavedSong;
  onEdit: (song: SavedSong) => void;
  onDelete: (song: SavedSong) => void;
}

export function SavedSongCard({ song, onEdit, onDelete }: SavedSongCardProps) {
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

      <StarRating value={song.userRating} readOnly />

      {song.userNotes && (
        <p className="line-clamp-2 rounded-md bg-surfaceMuted p-2 text-xs text-muted">{song.userNotes}</p>
      )}

      <div className="flex items-center justify-between pt-1">
        <span className="text-xs text-muted">Released {formatDate(song.releaseDate)}</span>
        <div className="flex gap-1">
          <Button variant="ghost" size="icon" onClick={() => onEdit(song)} aria-label="Edit song">
            <Pencil className="h-4 w-4" />
          </Button>
          <Button variant="ghost" size="icon" onClick={() => onDelete(song)} aria-label="Delete song">
            <Trash2 className="h-4 w-4 text-danger" />
          </Button>
        </div>
      </div>
    </Card>
  );
}
