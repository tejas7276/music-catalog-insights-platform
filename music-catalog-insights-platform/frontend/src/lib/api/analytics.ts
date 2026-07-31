import { apiClient } from "@/lib/api/client";
import { AnalyticsResponse } from "@/lib/types";

export const analyticsApi = {
  getAnalytics: () => apiClient.get<AnalyticsResponse>("/analytics").then((res) => res.data),
};
