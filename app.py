"""A small HTTP service for practicing a Jenkins pipeline."""

import json
import os
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer


def response_for(path: str) -> tuple[int, dict[str, str]]:
    if path == "/":
        return 200, {"message": "Hello from a Jenkins practice app"}
    if path == "/health":
        return 200, {"status": "ok"}
    return 404, {"error": "not found"}


class Handler(BaseHTTPRequestHandler):
    def do_GET(self) -> None:
        status, payload = response_for(self.path)
        body = json.dumps(payload).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)


if __name__ == "__main__":
    port = int(os.environ.get("PORT", "8080"))
    ThreadingHTTPServer(("0.0.0.0", port), Handler).serve_forever()
