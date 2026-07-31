"use client";

import { Toaster } from "sonner";
import { AuthProvider } from "@/context/AuthContext";

export function Providers({ children }: { children: React.ReactNode }) {
  return (
    <AuthProvider>
      {children}
      <Toaster
        theme="dark"
        position="top-right"
        toastOptions={{
          style: {
            background: "#171A21",
            border: "1px solid #2A2F3A",
            color: "#EDEEF2",
          },
        }}
      />
    </AuthProvider>
  );
}
