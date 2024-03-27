import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestBuilder {
    public Request request = new Request();
    public List<NameValuePair> params = new ArrayList<>();




    public Request requestParser(String[] requestLine){
        final var method = requestLine[0];
        request.method = method;
        System.out.println(method);
        final var path = requestLine[1];
        request.path = findPath(path);
        System.out.println(request.path);
        try {
            String url = "http://localhost:9998" +
                    path;
            params = URLEncodedUtils.parse(
                    new URI(url), "UTF-8");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        request.params = params;

        for (NameValuePair param : params) {
            System.out.println(param.getName() + " : " + param.getValue());

        }
        return request;
    }

    public String findPath(String path){
        StringBuilder sb = new StringBuilder();
        for (String elem : path.split("")){
            if(!elem.equals("?")) {
                sb.append(elem);
            } else{
                break;
            }
        }
        return sb.toString();
    }
}
