import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class MonoThreadServer implements Runnable {
    private static Socket socket;

    public MonoThreadServer(Socket socket){
        MonoThreadServer.socket = socket;
    }

    @Override
    public void run() {
        while (!socket.isClosed()){
            try (final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 final var out = new BufferedOutputStream(socket.getOutputStream())) {
                final var requestLine = in.readLine();
                Request request = new RequestBuilder().requestParser(requestLine);
                if(Server.handlersMap.containsKey(request.method)
                        && Server.handlersMap.get(request.method).containsKey(request.path)) {
                    Server.handlersMap.get(request.method).get(request.path).handle(request, out);
                } else{
                    out.write((
                            "HTTP/1.1 404 Not Found\r\n" +
                                    "Content-Length: 0\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    out.flush();
                }
                socket.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
