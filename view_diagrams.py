#!/usr/bin/env python3
"""
Script to open and display the diagrams HTML file in a browser using a local web server
"""

import webbrowser
import http.server
import socketserver
from pathlib import Path
import threading
import time

def main():
    # Get the path to the Diagrams directory
    script_dir = Path(__file__).parent
    diagrams_dir = script_dir / "Diagrams"

    # Check if directory exists
    if not diagrams_dir.exists():
        print(f"Error: Diagrams directory not found at {diagrams_dir}")
        return False

    # Change to the diagrams directory
    import os
    os.chdir(diagrams_dir)

    # Set up the web server
    PORT = 8000
    Handler = http.server.SimpleHTTPRequestHandler

    # Create the server
    with socketserver.TCPServer(("", PORT), Handler) as httpd:
        print(f"Starting web server on http://localhost:{PORT}")
        print(f"Serving diagrams from: {diagrams_dir}")
        print("Press Ctrl+C to stop the server\n")

        # Open browser in a separate thread
        def open_browser():
            time.sleep(1)  # Wait for server to start
            webbrowser.open(f"http://localhost:{PORT}/teetime-simplified-fixed.html")
            print("Diagrams opened in your default browser!")

        browser_thread = threading.Thread(target=open_browser, daemon=True)
        browser_thread.start()

        try:
            httpd.serve_forever()
        except KeyboardInterrupt:
            print("\nServer stopped.")

if __name__ == "__main__":
    main()
