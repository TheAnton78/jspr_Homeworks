import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.util.Arrays;
import java.util.List;


public class MonoThreadServer implements Runnable {
    private static Socket socket;
    public static final String GET = "GET";
    public static final String POST = "POST";


    public MonoThreadServer(Socket socket){
        MonoThreadServer.socket = socket;
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try (final var in = new BufferedInputStream(socket.getInputStream());
                 final var out = new BufferedOutputStream(socket.getOutputStream())) {
                final var allowedMethods = List.of(GET, POST);
                final var limit = 4096;

                in.mark(limit);
                final var buffer = new byte[limit];
                final var read = in.read(buffer);

                // ищем request line
                final var requestLineDelimiter = new byte[]{'\r', '\n'};
                final var requestLineEnd = indexOf(buffer, requestLineDelimiter, 0, read);
                if (requestLineEnd == -1) {
                    badRequest(out);
                    continue;
                }

                // читаем request line
                final var requestLine = new String(Arrays.copyOf(buffer, requestLineEnd)).split(" ");
                if (requestLine.length != 3) {
                    badRequest(out);
                    continue;
                }
                Request request = new RequestBuilder().requestParser(requestLine);
                if (Server.handlersMap.containsKey(request.method)
                        && Server.handlersMap.get(request.method).containsKey(request.path)
                        && allowedMethods.contains(request.method)) {
                    Server.handlersMap.get(request.method).get(request.path).handle(request, out);
                } else {
                    badRequest(out);
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
        private static void badRequest(BufferedOutputStream out) throws IOException {
            out.write((
                    "HTTP/1.1 400 Bad Request\r\n" +
                            "Content-Length: 0\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.flush();
        }

        // from google guava with modifications
        private static int indexOf(byte[] array, byte[] target, int start, int max) {
            outer:
            for (int i = start; i < max - target.length + 1; i++) {
                for (int j = 0; j < target.length; j++) {
                    if (array[i + j] != target[j]) {
                        continue outer;
                    }
                }
                return i;
            }
            return -1;
        }

}
