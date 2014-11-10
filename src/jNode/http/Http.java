package jNode.http;

import jNode.JNode;

import java.net.ServerSocket;
import java.net.Socket;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.message.BasicHttpResponse;

public class Http implements Runnable {

    private HttpHandler callback;
    private ServerSocket serverSocket;
    private JNode jNode;

    public Http(JNode jNode) throws Exception {
      this.serverSocket = new ServerSocket(85);
      this.jNode        = jNode;
    }

    public void get(HttpHandler r) {
      callback = r;
    }

    @Override
    public void run() {
      while(true) {

        try {
          Socket socket = serverSocket.accept();
          DefaultBHttpServerConnection conn = new DefaultBHttpServerConnection(8 * 1024);
          conn.bind(socket);
          HttpRequest request = conn.receiveRequestHeader();
//          System.out.println(request);
          HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "OK") ;

          class MyCallback implements Runnable {

            private DefaultBHttpServerConnection conn;
            private HttpRequest request;
            private HttpResponse response;

            public MyCallback(DefaultBHttpServerConnection conn, HttpRequest request, HttpResponse response) {
              this.conn     = conn;
              this.request  = request;
              this.response = response;
            }

            @Override
            public void run() {
              if (callback != null) {
                try {
                  callback.process(this.request,this.response);
                  this.conn.sendResponseHeader(this.response);
                  this.conn.sendResponseEntity(this.response);
                }
                catch(Exception e) {
                  e.printStackTrace();
                }
                finally {
                  try {
                    conn.close();
                  }
                  catch (Exception e) {
                    e.printStackTrace();
                  }
                }
              }
            }

          }

          jNode.registerEvent(new MyCallback(conn, request, response));

        }
        catch (ConnectionClosedException e) {
          System.out.println("The client closed the connection");
        }
        catch (Exception e) {
          e.printStackTrace();
        }

      }
    }

}




