# Coding Challenges

Solutions to challenges from [codingchallenges.fyi](https://codingchallenges.fyi/) — built from first principles.

## Challenges

### 1. [Build Your Own Web Server](./Build%20your%20Own%20Web%20Server/)

A basic HTTP web server built in Java from scratch using raw TCP sockets.

**Features:**
- HTTP request parsing (method, path, version)
- Static file serving from a `www/` directory
- Concurrent client handling with a thread pool (`ExecutorService`)
- Path traversal attack prevention via path normalization
- Custom 403/404 error pages

**How to run:**
```bash
cd "Build your Own Web Server"
javac -d bin server/HttpServer.java
java -cp bin server.HttpServer
# Server starts on http://localhost:8080
```

**Tech:** Java 21, `java.net.ServerSocket`, `java.util.concurrent`
