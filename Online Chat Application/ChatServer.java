import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ChatServer accepts multiple clients, assigns a unique ID
 * to each client, tracks active connections, and broadcasts
 * messages to connected users.
 */
public class ChatServer {

    private static final int DEFAULT_PORT = 5000;

    private static final Map<Integer, ClientHandler> clients =
            new ConcurrentHashMap<>();

    private static final AtomicInteger nextUserId =
            new AtomicInteger(1);

    public static void main(String[] args) {

        int port = DEFAULT_PORT;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);

            } catch (NumberFormatException e) {
                System.out.println(
                    "Invalid port; using default port "
                    + DEFAULT_PORT + "."
                );

                port = DEFAULT_PORT;
            }
        }

        try (ServerSocket serverSocket =
                     new ServerSocket(port)) {

            System.out.println(
                "Chat server is running on port "
                + port + "."
            );

            System.out.println("Waiting for clients...");

            // Continue accepting new clients.
            while (true) {

                Socket socket = serverSocket.accept();

                // Assign a unique ID to the new client.
                int userId = nextUserId.getAndIncrement();

                ClientHandler handler =
                        new ClientHandler(socket, userId);

                clients.put(userId, handler);

                // A separate thread handles each client.
                Thread thread = new Thread(
                        handler, "ChatClient-" + userId
                );

                thread.start();
            }

        } catch (IOException e) {

            System.err.println(
                "Server error: " + e.getMessage()
            );
        }
    }

    /**
     * Sends a message to every connected client.
     */
    private static void broadcast(String message) {

        for (ClientHandler client : clients.values()) {
            client.sendMessage(message);
        }

        System.out.println(message);
    }

    /**
     * Manages communication with one connected client.
     */
    private static class ClientHandler implements Runnable {

        private final Socket socket;
        private final int userId;

        private PrintWriter out;
        private String username;

        ClientHandler(Socket socket, int userId) {
            this.socket = socket;
            this.userId = userId;
        }

        @Override
        public void run() {

            try {

                BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                        socket.getInputStream()
                    )
                );

                out = new PrintWriter(
                    socket.getOutputStream(), true
                );

                out.println(
                    "Welcome! Your user ID is "
                    + userId + "."
                );

                out.println("Enter your username:");

                username = in.readLine();

                if (username == null ||
                        username.trim().isEmpty()) {

                    username = "User" + userId;

                } else {

                    username = username.trim()
                            .replaceAll("[\\r\\n]", "");
                }

                broadcast(
                    "[Server] " + username
                    + " has joined the chat."
                );

                String message;

                // Read messages until the client disconnects.
                while ((message = in.readLine()) != null) {

                    if ("/quit".equalsIgnoreCase(
                            message.trim())) {
                        break;
                    }

                    if (!message.trim().isEmpty()) {

                        broadcast(
                            "[" + username + " #"
                            + userId + "]: " + message
                        );
                    }
                }

            } catch (IOException e) {

                System.out.println(
                    "Connection issue for user "
                    + userId + ": " + e.getMessage()
                );

            } finally {

                // Remove disconnected users from the list.
                clients.remove(userId);

                String name = username == null
                        ? "User " + userId
                        : username;

                broadcast(
                    "[Server] " + name
                    + " has left the chat."
                );

                try {
                    socket.close();

                } catch (IOException e) {

                    System.err.println(
                        "Could not close socket: "
                        + e.getMessage()
                    );
                }

                System.out.println(
                    "Client " + userId + " disconnected."
                );
            }
        }

        /**
         * Sends a message to this particular client.
         */
        private void sendMessage(String message) {

            if (out != null) {
                out.println(message);
            }
        }
    }
}