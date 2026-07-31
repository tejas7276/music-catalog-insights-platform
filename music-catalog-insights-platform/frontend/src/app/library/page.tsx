"use client";

import { useCallback, useEffect, useState } from "react";
import { Library as LibraryIcon } from "lucide-react";
import { toast } from "sonner";
import Link from "next/link";
import { useRequireAuth } from "@/lib/hooks/useRequireAuth";
import { libraryApi } from "@/lib/api/library";
import { SavedSongCard } from "@/components/songs/SavedSongCard";
import { EditSongDialog } from "@/components/songs/EditSongDialog";
import { ConfirmDialog } from "@/components/common/ConfirmDialog";
import { EmptyState } from "@/components/common/EmptyState";
import { ErrorState } from "@/components/common/ErrorState";
import { LoadingGrid } from "@/components/common/LoadingState";
import { Pagination } from "@/components/common/Pagination";
import { Button } from "@/components/ui/button";
import { SavedSong, UpdateSongPayload } from "@/lib/types";

const PAGE_SIZE = 12;

export default function LibraryPage() {
  const { isLoading: authLoading, isAuthenticated } = useRequireAuth();

  const [songs, setSongs] = useState<SavedSong[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [editingSong, setEditingSong] = useState<SavedSong | null>(null);
  const [isSavingEdit, setIsSavingEdit] = useState(false);

  const [deletingSong, setDeletingSong] = useState<SavedSong | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const loadLibrary = useCallback(async (targetPage: number) => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await libraryApi.getLibrary({ page: targetPage, size: PAGE_SIZE });
      setSongs(response.content);
      setTotalPages(response.totalPages);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load library");
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    if (isAuthenticated) {
      loadLibrary(page);
    }
  }, [isAuthenticated, page, loadLibrary]);

  async function handleUpdate(id: number, payload: UpdateSongPayload) {
    setIsSavingEdit(true);
    try {
      const updated = await libraryApi.updateSong(id, payload);
      setSongs((prev) => prev.map((song) => (song.id === id ? updated : song)));
      toast.success("Song updated");
      setEditingSong(null);
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Failed to update song");
    } finally {
      setIsSavingEdit(false);
    }
  }

  async function handleDelete() {
    if (!deletingSong) return;
    setIsDeleting(true);
    try {
      await libraryApi.deleteSong(deletingSong.id);
      toast.success(`"${deletingSong.title}" removed from your library`);
      setDeletingSong(null);
      await loadLibrary(page);
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Failed to delete song");
    } finally {
      setIsDeleting(false);
    }
  }

  if (authLoading || !isAuthenticated) return null;

  return (
    <div className="container-page py-10">
      <div className="mb-8 flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <h1 className="font-display text-2xl font-semibold">Your library</h1>
          <p className="mt-1 text-sm text-muted">  Songs you&apos;ve saved from the iTunes catalog.</p>
        </div>
        <Button asChild>
          <Link href="/search">Search for more songs</Link>
        </Button>
      </div>

      {isLoading && <LoadingGrid count={PAGE_SIZE} />}

      {!isLoading && error && <ErrorState message={error} onRetry={() => loadLibrary(page)} />}

      {!isLoading && !error && songs.length === 0 && (
        <EmptyState
          icon={LibraryIcon}
          title="Your library is empty"
          description="Search for songs and save the ones you love to start building your library."
          action={
            <Button asChild>
              <Link href="/search">Start searching</Link>
            </Button>
          }
        />
      )}

      {!isLoading && !error && songs.length > 0 && (
        <>
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {songs.map((song) => (
              <SavedSongCard
                key={song.id}
                song={song}
                onEdit={setEditingSong}
                onDelete={setDeletingSong}
              />
            ))}
          </div>
          <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </>
      )}

      <EditSongDialog
        song={editingSong}
        isSaving={isSavingEdit}
        onOpenChange={(open) => !open && setEditingSong(null)}
        onSubmit={handleUpdate}
      />

      <ConfirmDialog
        open={Boolean(deletingSong)}
        onOpenChange={(open) => !open && setDeletingSong(null)}
        title="Remove song from library?"
        description={`This will permanently remove "${deletingSong?.title}" from your library. This action cannot be undone.`}
        confirmLabel="Remove"
        destructive
        isLoading={isDeleting}
        onConfirm={handleDelete}
      />
    </div>
  );
}
