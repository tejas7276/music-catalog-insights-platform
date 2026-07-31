"use client";

import { useEffect, useState, useCallback } from "react";
import { Search as SearchIcon, Music2 } from "lucide-react";
import { toast } from "sonner";
import { useRequireAuth } from "@/lib/hooks/useRequireAuth";
import { useDebouncedValue } from "@/lib/hooks/useDebouncedValue";
import { searchApi } from "@/lib/api/search";
import { libraryApi } from "@/lib/api/library";
import { Input } from "@/components/ui/input";
import { SearchResultCard } from "@/components/songs/SearchResultCard";
import { EmptyState } from "@/components/common/EmptyState";
import { ErrorState } from "@/components/common/ErrorState";
import { LoadingGrid } from "@/components/common/LoadingState";
import { SongSearchResult } from "@/lib/types";

export default function SearchPage() {
  const { isLoading: authLoading, isAuthenticated } = useRequireAuth();

  const [query, setQuery] = useState("");
  const debouncedQuery = useDebouncedValue(query, 450);

  const [results, setResults] = useState<SongSearchResult[]>([]);
  const [isSearching, setIsSearching] = useState(false);
  const [searchError, setSearchError] = useState<string | null>(null);
  const [savedIds, setSavedIds] = useState<Set<number>>(new Set());
  const [savingId, setSavingId] = useState<number | null>(null);

  const loadSavedIds = useCallback(async () => {
    try {
      const page = await libraryApi.getLibrary({ page: 0, size: 1000 });
      setSavedIds(new Set(page.content.map((song) => song.appleCatalogId)));
    } catch {
      // Non-fatal: worst case, save buttons don't pre-disable.
    }
  }, []);

  useEffect(() => {
    if (isAuthenticated) {
      loadSavedIds();
    }
  }, [isAuthenticated, loadSavedIds]);

  useEffect(() => {
    if (!debouncedQuery.trim()) {
      setResults([]);
      setSearchError(null);
      return;
    }

    let cancelled = false;
    setIsSearching(true);
    setSearchError(null);

    searchApi
      .searchSongs(debouncedQuery.trim())
      .then((response) => {
        if (!cancelled) setResults(response.results);
      })
      .catch((error) => {
        if (!cancelled) setSearchError(error instanceof Error ? error.message : "Search failed");
      })
      .finally(() => {
        if (!cancelled) setIsSearching(false);
      });

    return () => {
      cancelled = true;
    };
  }, [debouncedQuery]);

  async function handleSave(song: SongSearchResult) {
    setSavingId(song.appleCatalogId);
    try {
      await libraryApi.saveSong({
        appleCatalogId: song.appleCatalogId,
        title: song.title,
        artistName: song.artistName,
        genre: song.genre,
        releaseDate: song.releaseDate,
        durationMillis: song.durationMillis,
        artworkUrl: song.artworkUrl,
      });
      setSavedIds((prev) => new Set(prev).add(song.appleCatalogId));
      toast.success(`"${song.title}" added to your library`);
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Failed to save song");
    } finally {
      setSavingId(null);
    }
  }

  if (authLoading || !isAuthenticated) return null;

  return (
    <div className="container-page py-10">
      <div className="mb-8">
        <h1 className="font-display text-2xl font-semibold">Search songs</h1>
        <p className="mt-1 text-sm text-muted">
          Search the iTunes catalog and save songs to your personal library.
        </p>
      </div>

      <div className="relative mb-8 max-w-xl">
        <SearchIcon className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted" />
        <Input
          value={query}
          onChange={(event) => setQuery(event.target.value)}
          placeholder="Search for a song or artist..."
          className="pl-9"
          autoFocus
        />
      </div>

      {isSearching && <LoadingGrid count={6} />}

      {!isSearching && searchError && (
        <ErrorState message={searchError} onRetry={() => setQuery((q) => q + "")} />
      )}

      {!isSearching && !searchError && debouncedQuery.trim() && results.length === 0 && (
        <EmptyState
          icon={Music2}
          title="No results found"
          description={`We couldn't find any songs matching "${debouncedQuery}". Try a different search term.`}
        />
      )}

      {!isSearching && !searchError && !debouncedQuery.trim() && (
        <EmptyState
          icon={SearchIcon}
          title="Start typing to search"
          description="Search by song title or artist name to discover music from the iTunes catalog."
        />
      )}

      {!isSearching && !searchError && results.length > 0 && (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {results.map((song) => (
            <SearchResultCard
              key={song.appleCatalogId}
              song={song}
              isSaved={savedIds.has(song.appleCatalogId)}
              isSaving={savingId === song.appleCatalogId}
              onSave={handleSave}
            />
          ))}
        </div>
      )}
    </div>
  );
}
