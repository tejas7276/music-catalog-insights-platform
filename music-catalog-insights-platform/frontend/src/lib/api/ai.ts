import { apiClient } from "@/lib/api/client";
import { AiInsightsResponse } from "@/lib/types";

export const aiApi = {
  generateInsights: () =>
    apiClient.post<AiInsightsResponse>("/ai/insights").then((res) => res.data),
};
