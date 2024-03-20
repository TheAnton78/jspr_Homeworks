import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final ExecutorService executeIt = Executors.newFixedThreadPool(64);
    protected static Map<String, Map<String, Handler>> handlersMap = new ConcurrentHashMap<>();


    public void addHandler(String method, String path, Handler handler){
        Map<String, Handler> methodMap =  new ConcurrentHashMap<>();
        methodMap.put(path, handler);
        handlersMap.put(method, methodMap);
    }

    public static void listen(Integer socketNumber) {
        try(final var serverSocket = new ServerSocket(socketNumber)){
            while (!serverSocket.isClosed()) {
                Socket client = serverSocket.accept();
                executeIt.execute(new MonoThreadServer(client));
                System.out.print("Connection accepted.");
            }
            executeIt.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
