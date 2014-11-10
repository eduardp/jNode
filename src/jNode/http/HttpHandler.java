package jNode.http;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

@FunctionalInterface
public interface HttpHandler {

  void process(HttpRequest req, HttpResponse resp);

}
