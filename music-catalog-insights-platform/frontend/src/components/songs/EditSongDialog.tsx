"use client";

import { useEffect, useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { StarRating } from "@/components/common/StarRating";
import { SavedSong, UpdateSongPayload } from "@/lib/types";

interface EditSongDialogProps {
  song: SavedSong | null;
  isSaving: boolean;
  onOpenChange: (open: boolean) => void;
  onSubmit: (id: number, payload: UpdateSongPayload) => void;
}

export function EditSongDialog({ song, isSaving, onOpenChange, onSubmit }: EditSongDialogProps) {
  const [rating, setRating] = useState<number>(0);
  const [notes, setNotes] = useState("");

  useEffect(() => {
    if (song) {
      setRating(song.userRating ?? 0);
      setNotes(song.userNotes ?? "");
    }
  }, [song]);

  if (!song) return null;

  return (
    <Dialog open={Boolean(song)} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{song.title}</DialogTitle>
          <DialogDescription>Update your rating and personal notes for this song.</DialogDescription>
        </DialogHeader>

        <div className="flex flex-col gap-4">
          <div className="flex flex-col gap-2">
            <Label>Your rating</Label>
            <StarRating value={rating} onChange={setRating} size={22} />
          </div>

          <div className="flex flex-col gap-2">
            <Label htmlFor="notes">Notes</Label>
            <Textarea
              id="notes"
              value={notes}
              maxLength={2000}
              rows={4}
              placeholder="What do you think about this song?"
              onChange={(event) => setNotes(event.target.value)}
            />
          </div>
        </div>

        <DialogFooter>
          <Button variant="outline" onClick={() => onOpenChange(false)} disabled={isSaving}>
            Cancel
          </Button>
          <Button
            onClick={() => onSubmit(song.id, { userRating: rating || null, userNotes: notes || null })}
            disabled={isSaving}
          >
            {isSaving ? "Saving..." : "Save changes"}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
