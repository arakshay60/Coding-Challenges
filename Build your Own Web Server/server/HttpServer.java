package server;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server is listening on port 8080...");
            ExecutorService pool = Executors.newFixedThreadPool(10);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[" + Thread.currentThread().getName() + "] Main thread accepted connection.");
                pool.submit(() -> handleClient(clientSocket));
            }
        } catch (Exception e) {
            System.err.println("Server exception:" + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        String threadName = Thread.currentThread().getName();
        System.out.println("[" + threadName + "] Started processing client request.");
        try (Socket socket = clientSocket;
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
            String message = reader.readLine();
            if (message == null || message.isEmpty()) {
                return;
            }
            String[] messageArray = message.split(" ");
            String path;
            if (messageArray[1].equals("/")) {
                path = "www" + "/index.html";
            } else {
                path = "www" + messageArray[1];
            }

            Path filePath = Path.of(path);
            filePath = filePath.toAbsolutePath().normalize();
            Path wwwDir = Path.of("www").toAbsolutePath().normalize();
            String inline404 = "<html><body style='font-family:sans-serif;text-align:center;padding-top:100px;background:#f8fafc;color:#334155;'><h1 style='font-size:6rem;color:#ef4444;margin:0;'>404</h1><h2>Resource Not Found</h2><p style='color:#64748b;'>The requested path does not exist on this server.</p><a href='/' style='display:inline-block;margin-top:20px;padding:10px 20px;background:#0f172a;color:#fff;text-decoration:none;border-radius:5px;'>Return Home</a></body></html>";
            String inline403 = "<html><body style='font-family:sans-serif;text-align:center;padding-top:100px;background:#f8fafc;color:#334155;'><h1 style='font-size:6rem;color:#f59e0b;margin:0;'>403</h1><h2>Access Denied</h2><p style='color:#64748b;'>You do not have permission to access this directory.</p><a href='/' style='display:inline-block;margin-top:20px;padding:10px 20px;background:#0f172a;color:#fff;text-decoration:none;border-radius:5px;'>Return Home</a></body></html>";
            if (!filePath.startsWith(wwwDir)) {
                writer.println("HTTP/1.1 403 OK\r\n\r\n" + inline403);
            } else if (Files.exists(filePath)) {
                String content = Files.readString(filePath);
                writer.println("HTTP/1.1 200 OK\r\n\r\n" + content);
            } else {
                writer.println("HTTP/1.1 404 Not Found \r\n\r\n" + inline404);
            }
        } catch (Exception e) {
            System.err.println("[" + Thread.currentThread().getName() + "] Error: " + e.getMessage());
        }

    }

}
