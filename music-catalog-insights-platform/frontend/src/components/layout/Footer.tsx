export function Footer() {
  return (
    <footer className="border-t border-border py-8">
      <div className="container-page flex flex-col items-center justify-between gap-2 text-sm text-muted sm:flex-row">
        <p>&copy; {new Date().getFullYear()} Music Catalog Insights Platform</p>
        <p>Built with Spring Boot, Next.js, and the iTunes Search API</p>
      </div>
    </footer>
  );
}
