package jNode;

import jNode.http.Http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;

//http://hc.apache.org/httpcomponents-core-ga/
//http://hc.apache.org/httpcomponents-core-ga/tutorial/pdf/httpcore-tutorial.pdf
//http://geekforum.wordpress.com/2014/06/17/implementing-rest-server-using-jdk-http-server-and-jersey/
public class JNode {

  private Queue<Runnable> queue = new LinkedBlockingQueue<Runnable>();

  public void run() {
    Thread thread = new Thread(
        () -> {
          while (true) {
            Runnable task = JNode.this.queue.poll();
            if (task != null) {
              task.run();
            }
          }
        }
        );
    thread.setDaemon(false);
    thread.setName("EventLoop");
    thread.start();
  }

  public void registerEvent(Runnable r) {
    queue.add(r);
  }

  public static void main(String[] args) throws Exception {

    JNode jnode = new JNode();

    Http http = new Http(jnode);
    Thread thread = new Thread(http);
    thread.setName("HTTP");
    thread.setDaemon(true);
    thread.start();

    http.get((HttpRequest req, HttpResponse resp) -> {

      String message = "HELLO WORLD";
//      resp.setStatusCode(404);
//      resp.setReasonPhrase("Not Found");

      try {
          resp.setEntity(new HttpEntity() {

            @Override
            public void writeTo(OutputStream outstream) throws IOException {
              outstream.write(message.getBytes());
            }

            @Override
            public boolean isStreaming() {
              // TODO Auto-generated method stub
              return false;
            }

            @Override
            public boolean isRepeatable() {
              // TODO Auto-generated method stub
              return false;
            }

            @Override
            public boolean isChunked() {
              // TODO Auto-generated method stub
              return false;
            }

            @Override
            public Header getContentType() {
              return new BasicHeader("Content-Type","text/plain");
            }

            @Override
            public long getContentLength() {
              return message.length();
            }

            @Override
            public Header getContentEncoding() {
              // TODO Auto-generated method stub
              return null;
            }

            @Override
            public InputStream getContent() throws IOException, IllegalStateException {
              return new ByteArrayInputStream(message.getBytes());
            }

            @Override
            public void consumeContent() throws IOException {
System.out.println();
            }
          });
//        s.getOutputStream().write("HTTP/1.1 200 OK\n\nHELLO WORLD".getBytes());
      }
      catch (Exception e) {
        e.printStackTrace();
      }



    });

    jnode.run();

  }

}

