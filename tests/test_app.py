import json
import threading
import unittest
from http.server import ThreadingHTTPServer
from urllib.error import HTTPError
from urllib.request import urlopen

from app import Handler


class HttpAppTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.server = ThreadingHTTPServer(("127.0.0.1", 0), Handler)
        cls.thread = threading.Thread(target=cls.server.serve_forever, daemon=True)
        cls.thread.start()
        cls.base_url = f"http://127.0.0.1:{cls.server.server_port}"

    @classmethod
    def tearDownClass(cls):
        cls.server.shutdown()
        cls.server.server_close()
        cls.thread.join(timeout=2)

    def test_health_endpoint(self):
        with urlopen(self.base_url + "/health") as response:
            self.assertEqual(response.status, 200)
            self.assertEqual(json.load(response), {"status": "ok"})

    def test_unknown_path(self):
        with self.assertRaises(HTTPError) as failure:
            urlopen(self.base_url + "/missing")
        error = failure.exception
        try:
            self.assertEqual(error.code, 404)
            self.assertEqual(json.load(error), {"error": "not found"})
        finally:
            error.close()


if __name__ == "__main__":
    unittest.main()
