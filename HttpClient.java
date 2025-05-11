import java.io.*;
import java.net.*;
import java.util.Scanner;

public class HttpClient {

    public static void sendRequest(String method, String host, int port, String path, String headers, String body) throws IOException {
        try (Socket socket = new Socket(host, port);
             PrintWriter writer = new PrintWriter(socket.getOutputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            writer.print(method + " " + path + " HTTP/1.1\r\n");
            writer.print("Host: " + host + "\r\n");
            writer.print(headers);
            writer.print("Content-Length: " + body.length() + "\r\n");
            writer.print("Connection: close\r\n");
            writer.print("\r\n");
            writer.print(body);
            writer.flush();

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("\nMétodo HTTP (GET, POST, PUT, DELETE): ");
                String method = scanner.nextLine().trim().toUpperCase();

                System.out.println("Ruta (por ejemplo: /items o /items/1): ");
                String path = scanner.nextLine().trim();

                String body = "";
                if (method.equals("POST") || method.equals("PUT")) {
                    System.out.println("Cuerpo (texto plano): ");
                    body = scanner.nextLine();
                }

                sendRequest(method, "localhost", 8080, path, "", body);

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            System.out.println("\n¿Otra petición? (si/no): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("si")) break;
        }

        scanner.close();
    }
}
