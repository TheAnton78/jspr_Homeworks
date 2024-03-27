import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Request {
    public String method;
    public String path;
    public String queryString;
    List<NameValuePair> params;

    public List<NameValuePair> getQueryParams(){
        return params;
    }
    public List<NameValuePair> getQueryParam(String name){
        return params.stream()
                .filter(x -> x.getName().equals(name))
                .collect(Collectors.toList());
    }
}
