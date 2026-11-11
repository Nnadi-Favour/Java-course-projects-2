import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * ChatClient connects to ChatServer, sends messages typed
 * by the user, and displays messages from other clients.
 */
public class ChatClient {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 5000;

    public static void main(String[] args) {

        String host = args.length > 0
                ? args[0]
                : DEFAULT_HOST;

        int port = DEFAULT_PORT;

        if (args.length > 1) {

            try {
                port = Integer.parseInt(args[1]);

            } catch (NumberFormatException e) {

                System.out.println(
                    "Invalid port; using default port "
                    + DEFAULT_PORT + "."
                );
            }
        }

        try (
            Socket socket = new Socket(host, port);

            BufferedReader serverIn = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream()
                )
            );

            PrintWriter serverOut = new PrintWriter(
                socket.getOutputStream(), true
            );

            BufferedReader keyboard = new BufferedReader(
                new InputStreamReader(System.in)
            )
        ) {

            System.out.println(
                "Connected to chat server at "
                + host + ":" + port + "."
            );

            System.out.println(
                "Type /quit to leave the chat."
            );

            System.out.println(
                "----------------------------------------"
            );

            // Receive messages in the background.
            Thread receiver = new Thread(() -> {

                try {

                    String incoming;

                    while ((incoming =
                            serverIn.readLine()) != null) {

                        System.out.println(incoming);
                    }

                    System.out.println(
                        "The server closed the connection."
                    );

                } catch (IOException e) {

                    System.out.println(
                        "Message receiver stopped: "
                        + e.getMessage()
                    );
                }

            }, "ChatReceiver");

            receiver.setDaemon(true);
            receiver.start();

            // Read the user's messages and send them.
            String outgoing;

            while ((outgoing = keyboard.readLine()) != null) {

                serverOut.println(outgoing);

                if ("/quit".equalsIgnoreCase(
                        outgoing.trim())) {
                    break;
                }
            }

        } catch (IOException e) {

            System.err.println(
                "Could not connect to the chat server: "
                + e.getMessage()
            );

            System.err.println(
                "Check that the server is running "
                + "and the host/port are correct."
            );
        }
    }
}