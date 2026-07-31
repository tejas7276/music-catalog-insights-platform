import { apiClient } from "@/lib/api/client";
import { SearchResponse } from "@/lib/types";

export const searchApi = {
  searchSongs: (query: string) =>
    apiClient
      .get<SearchResponse>("/search", { params: { query, type: "song" } })
      .then((res) => res.data),
};
