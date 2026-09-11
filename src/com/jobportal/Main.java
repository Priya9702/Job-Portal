package com.jobportal;

import com.sun.net.httpserver.HttpServer;
import com.jobportal.handler.ApiHandler;
import com.jobportal.handler.StaticHandler;

import java.io.File;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) {
        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            // API Route Handler
            server.createContext("/api", new ApiHandler());

            // Static Web Assets Handler
            String webDirPath = new File("web").getAbsolutePath();
            server.createContext("/", new StaticHandler(webDirPath));

            server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(10));
            server.start();

            System.out.println("========================================================================");
            System.out.println(" 🚀 MCA RESUME PROJECT: AI JOB PORTAL & RESUME MATCHER (JAVA BACKEND)");
            System.out.println("========================================================================");
            System.out.println("  • Backend Engine  : Java 21 SE HttpServer + JDBC SQL Schema");
            System.out.println("  • Server Listening: http://localhost:" + port);
            System.out.println("  • Student Panel   : http://localhost:" + port + "#student");
            System.out.println("  • Recruiter Panel : http://localhost:" + port + "#recruiter");
            System.out.println("  • Admin Panel     : http://localhost:" + port + "#admin");
            System.out.println("========================================================================\n");

        } catch (Exception e) {
            System.err.println("❌ Failed to start Java Job Portal Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
