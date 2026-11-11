import java.io.IOException;

/**
 * Main launcher for the Unit 7 Online Chat Application.
 *
 * Usage:
 * java Unit7 server
 * java Unit7 client
 * java Unit7 client localhost 5000
 */
public class Unit7 {

    public static void main(String[] args) {

        if (args.length == 0) {
            showUsage();
            return;
        }

        String mode = args[0].toLowerCase();

        try {

            if ("server".equals(mode)) {

                String[] serverArgs = new String[args.length - 1];

                System.arraycopy(
                    args, 1, serverArgs, 0, serverArgs.length
                );

                ChatServer.main(serverArgs);

            } else if ("client".equals(mode)) {

                String[] clientArgs = new String[args.length - 1];

                System.arraycopy(
                    args, 1, clientArgs, 0, clientArgs.length
                );

                ChatClient.main(clientArgs);

            } else {
                showUsage();
            }

        } catch (Exception e) {
            System.err.println(
                "The application could not start: "
                + e.getMessage()
            );
        }
    }

    private static void showUsage() {

        System.out.println(
            "Unit 7 - Online Chat Application"
        );

        System.out.println(
            "Start server: java Unit7 server [port]"
        );

        System.out.println(
            "Start client: java Unit7 client [host] [port]"
        );
    }
}