import { apiClient } from "@/lib/api/client";
import { AuthResponse, LoginPayload, RegisterPayload } from "@/lib/types";

export const authApi = {
  register: (payload: RegisterPayload) =>
    apiClient.post<AuthResponse>("/auth/register", payload).then((res) => res.data),

  login: (payload: LoginPayload) =>
    apiClient.post<AuthResponse>("/auth/login", payload).then((res) => res.data),
};
