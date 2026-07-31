"use client";

import { useCallback, useEffect, useState } from "react";
import { UserCircle } from "lucide-react";
import { useRequireAuth } from "@/lib/hooks/useRequireAuth";
import { useAuth } from "@/context/AuthContext";
import { libraryApi } from "@/lib/api/library";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { initials } from "@/lib/utils";

export default function ProfilePage() {
  const { isLoading: authLoading, isAuthenticated } = useRequireAuth();
  const { user, logout } = useAuth();
  const [songCount, setSongCount] = useState<number | null>(null);

  const loadCount = useCallback(async () => {
    try {
      const response = await libraryApi.getLibrary({ page: 0, size: 1 });
      setSongCount(response.totalElements);
    } catch {
      setSongCount(null);
    }
  }, []);

  useEffect(() => {
    if (isAuthenticated) loadCount();
  }, [isAuthenticated, loadCount]);

  if (authLoading || !isAuthenticated || !user) return null;

  return (
    <div className="container-page py-10">
      <h1 className="mb-8 font-display text-2xl font-semibold">Profile</h1>

      <Card className="max-w-lg">
        <CardHeader className="flex-row items-center gap-4">
          <div className="flex h-14 w-14 items-center justify-center rounded-full bg-primary/15 font-display text-lg font-semibold text-primary">
            {initials(user.name)}
          </div>
          <div>
            <CardTitle>{user.name}</CardTitle>
            <CardDescription>{user.email}</CardDescription>
          </div>
        </CardHeader>
        <CardContent className="flex flex-col gap-4">
          <div className="flex items-center justify-between rounded-md border border-border bg-surfaceMuted p-4">
            <div className="flex items-center gap-2 text-sm text-muted">
              <UserCircle className="h-4 w-4" /> Songs saved
            </div>
            <span className="font-display text-lg font-semibold">{songCount ?? "--"}</span>
          </div>
          <Button variant="destructive" onClick={logout}>
            Log out
          </Button>
        </CardContent>
      </Card>
    </div>
  );
}
