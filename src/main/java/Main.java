import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args){
        final var server = new Server();

        // добавление хендлеров (обработчиков)
        server.addHandler("GET", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                StringBuilder stringBuilder = new StringBuilder();
                request.getQueryParams()
                        .forEach(x -> stringBuilder.append(x.getName() + ": " + x.getValue()));
                String queryParams = stringBuilder.toString();
                try(FileOutputStream out = new FileOutputStream("public/response")) {
                    byte[] buffer = queryParams.getBytes();
                    out.write(buffer);
                }
                final var filePath = Path.of(".","public", "response");
                final var mimeType = Files.probeContentType(filePath);
                responseStream.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + Files.size(filePath) + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath,responseStream);
                responseStream.flush();
            }
        });
        server.addHandler("POST", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                final var filePath = Path.of(".","public", "postResponse");
                final var mimeType = Files.probeContentType(filePath);
                responseStream.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + Files.size(filePath) + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath,responseStream);
                responseStream.flush();
            }
        });

        Server.listen(9998);
    }
}

