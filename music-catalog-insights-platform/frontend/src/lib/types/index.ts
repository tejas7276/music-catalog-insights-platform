export interface AuthResponse {
  token: string;
  tokenType: string;
  userId: number;
  name: string;
  email: string;
  expiresInMillis: number;
}

export interface RegisterPayload {
  name: string;
  email: string;
  password: string;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export interface SongSearchResult {
  appleCatalogId: number;
  title: string;
  artistName: string;
  genre: string | null;
  releaseDate: string | null;
  durationMillis: number | null;
  artworkUrl: string | null;
  collectionName: string | null;
  trackPrice: number | null;
  previewUrl: string | null;
}

export interface SearchResponse {
  query: string;
  resultCount: number;
  results: SongSearchResult[];
}

export interface SavedSong {
  id: number;
  appleCatalogId: number;
  title: string;
  artistName: string;
  genre: string | null;
  releaseDate: string | null;
  durationMillis: number | null;
  artworkUrl: string | null;
  userRating: number | null;
  userNotes: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface SaveSongPayload {
  appleCatalogId: number;
  title: string;
  artistName: string;
  genre: string | null;
  releaseDate: string | null;
  durationMillis: number | null;
  artworkUrl: string | null;
  userRating?: number | null;
  userNotes?: string | null;
}

export interface UpdateSongPayload {
  userRating?: number | null;
  userNotes?: string | null;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface AnalyticsResponse {
  summaryCards: {
    totalSongs: number;
    averageRating: number | null;
    uniqueArtists: number;
    uniqueGenres: number;
    highestRatedSong: SavedSong | null;
    latestAddedSong: SavedSong | null;
  };
  genreDistribution: { genre: string; count: number }[];
  topArtists: { artist: string; count: number }[];
  songsAddedOverTime: { date: string; count: number }[];
  ratingsDistribution: { rating: number; count: number }[];
  releaseYearDistribution: { year: string; count: number }[];
  averageRatingByGenre: { genre: string; averageRating: number }[];
}

export interface AiInsightsResponse {
  listeningSummary: string;
  favouriteGenres: string[];
  favouriteArtists: string[];
  moodAnalysis: string;
  releaseEraPreference: string;
  recommendations: { title: string; artist: string; reason: string }[];
  personalizedSuggestions: string;
  librarySizeAnalyzed: number;
}

export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  details?: string[] | null;
}
