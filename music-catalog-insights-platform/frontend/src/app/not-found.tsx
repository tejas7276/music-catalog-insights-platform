import Link from "next/link";
import { Compass } from "lucide-react";
import { Button } from "@/components/ui/button";

export default function NotFound() {
  return (
    <div className="container-page flex min-h-[calc(100vh-4rem)] flex-col items-center justify-center gap-4 text-center">
      <Compass className="h-10 w-10 text-primary" />
      <h1 className="font-display text-4xl font-bold">404</h1>
      <p className="max-w-sm text-muted">
        We couldn&apos;t find the page you&apos;re looking for. It may have been moved or doesn&apos;t exist.
      </p>
      <Button asChild>
        <Link href="/">Back to home</Link>
      </Button>
    </div>
  );
}
