"use client";

import { Star } from "lucide-react";
import { cn } from "@/lib/utils";

interface StarRatingProps {
  value: number | null;
  onChange?: (value: number) => void;
  readOnly?: boolean;
  size?: number;
}

export function StarRating({ value, onChange, readOnly = false, size = 18 }: StarRatingProps) {
  const rating = value ?? 0;

  return (
    <div className="flex items-center gap-0.5" role="img" aria-label={`Rating: ${rating} out of 5`}>
      {[1, 2, 3, 4, 5].map((star) => (
        <button
          key={star}
          type="button"
          disabled={readOnly}
          onClick={() => onChange?.(star)}
          className={cn("transition-transform", !readOnly && "hover:scale-110")}
          aria-label={`Rate ${star} star${star > 1 ? "s" : ""}`}
        >
          <Star
            width={size}
            height={size}
            className={cn(
              star <= rating ? "fill-primary text-primary" : "fill-transparent text-muted"
            )}
          />
        </button>
      ))}
    </div>
  );
}
