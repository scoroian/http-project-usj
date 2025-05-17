import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class HttpServer {
    private static final int PORT = 8080;
    private static final Map<Integer, String> items = new ConcurrentHashMap<>();
    private static int currentId = 1;

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor escuchando en el puerto: " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream())) {

            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isEmpty()) return;

            StringTokenizer tokenizer = new StringTokenizer(requestLine);
            String method = tokenizer.nextToken();
            String path = tokenizer.nextToken();

            // Leer headers
            String line;
            int contentLength = 0;
            while (!(line = reader.readLine()).isEmpty()) {
                if (line.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
            }

            char[] bodyChars = new char[contentLength];
            if (contentLength > 0) {
                reader.read(bodyChars, 0, contentLength);
            }
            String body = new String(bodyChars);
            // Manejar petición preflight OPTIONS para CORS
            if (method.equals("OPTIONS")) {
                sendResponse(writer, "200 OK", "text/plain", "");
                return;
            }
            // Manejo de endpoints
            switch (method) {
                case "GET":
                    if (path.equals("/")) {
                        sendResponse(writer, "200 OK", "text/html", "<h1>Bienvenido al servidor HTTP!</h1>");
                    } else if (path.equals("/items")) {
                        sendResponse(writer, "200 OK", "application/json", items.toString());
                    } else if (path.matches("/items/\\d+")) {
                        int id = Integer.parseInt(path.split("/")[2]);
                        String item = items.get(id);
                        if (item != null) {
                            sendResponse(writer, "200 OK", "text/plain", item);
                        } else {
                            sendResponse(writer, "404 Not Found", "text/plain", "ID no encontrado");
                        }
                    }
                    else {
                        sendResponse(writer, "400 Bad Request", "text/plain", "Solicitud incorrecta");
                    }
                    break;

                case "POST":
                    if (path.equals("/items")) {
                        int id = currentId++;
                        items.put(id, body);
                        sendResponse(writer, "201 Created", "text/plain", "Item creado con ID: " + id);
                    } else {
                        sendResponse(writer, "400 Bad Request", "text/plain", "Solicitud incorrecta");
                    }
                    break;

                case "PUT":
                    if (path.matches("/items/\\d+")) {
                        int id = Integer.parseInt(path.split("/")[2]);
                        if (items.containsKey(id)) {
                            items.put(id, body);
                            sendResponse(writer, "200 OK", "text/plain", "Item actualizado con ID: " + id);
                        } else {
                            sendResponse(writer, "404 Not Found", "text/plain", "ID no encontrado");
                        }
                    } else {
                        sendResponse(writer, "400 Bad Request", "text/plain", "Solicitud incorrecta");
                    }
                    break;

                case "DELETE":
                    if (path.matches("/items/\\d+")) {
                        int id = Integer.parseInt(path.split("/")[2]);
                        if (items.remove(id) != null) {
                            sendResponse(writer, "200 OK", "text/plain", "Item borrado con ID: " + id);
                        } else {
                            sendResponse(writer, "404 Not Found", "text/plain", "ID no encontrado");
                        }
                    } else {
                        sendResponse(writer, "400 Bad Request", "text/plain", "Solicitud incorrecta");
                    }
                    break;
                
                default:
                    sendResponse(writer, "405 Method Not Allowed", "text/plain", "Método no permitido");
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { clientSocket.close(); } catch (IOException ex) { ex.printStackTrace(); }
        }
    }

    private static void sendResponse(PrintWriter writer, String status, String contentType, String body) {
        writer.print("HTTP/1.1 " + status + "\r\n");
        writer.print("Content-Type: " + contentType + "\r\n");
        writer.print("Access-Control-Allow-Origin: *\r\n");
        writer.print("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS\r\n");
        writer.print("Access-Control-Allow-Headers: Content-Type\r\n");
        writer.print("Content-Length: " + body.length() + "\r\n");
        writer.print("Connection: close\r\n");
        writer.print("\r\n");
        writer.print(body);
        writer.flush();
    }

}
