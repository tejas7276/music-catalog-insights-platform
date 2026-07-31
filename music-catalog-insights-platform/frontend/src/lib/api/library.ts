import { apiClient } from "@/lib/api/client";
import { PageResponse, SavedSong, SaveSongPayload, UpdateSongPayload } from "@/lib/types";

export interface LibraryQuery {
  page?: number;
  size?: number;
  sortBy?: string;
  direction?: "ASC" | "DESC";
}

export const libraryApi = {
  getLibrary: (query: LibraryQuery = {}) =>
    apiClient
      .get<PageResponse<SavedSong>>("/library", {
        params: {
          page: query.page ?? 0,
          size: query.size ?? 12,
          sortBy: query.sortBy ?? "createdAt",
          direction: query.direction ?? "DESC",
        },
      })
      .then((res) => res.data),

  getSong: (id: number) => apiClient.get<SavedSong>(`/library/${id}`).then((res) => res.data),

  saveSong: (payload: SaveSongPayload) =>
    apiClient.post<SavedSong>("/library", payload).then((res) => res.data),

  updateSong: (id: number, payload: UpdateSongPayload) =>
    apiClient.put<SavedSong>(`/library/${id}`, payload).then((res) => res.data),

  deleteSong: (id: number) => apiClient.delete<void>(`/library/${id}`).then((res) => res.data),
};
