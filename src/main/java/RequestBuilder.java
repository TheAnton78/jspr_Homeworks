public class RequestBuilder {
    public Request request = new Request();

    public Request requestParser(String requestLine){
        System.out.println(requestLine);
        var parts = requestLine.split(" ");
        request.method = parts[0];
        request.path = parts[1];
        return request;
    }
}
