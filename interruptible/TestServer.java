package interruptible;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer implements Runnable {
    public final static int PORT = 9999;
    private JTextArea messages;

    public TestServer(JTextArea messages) {
        this.messages = messages;
    }

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(PORT);
            while (true) {
                Socket incoming = ss.accept();
                new Thread(new TestServerHandler(incoming)).start();
            }

        } catch (IOException ex) {
            messages.append("\nTestServer.run: " + ex);
        }
    }

    private class TestServerHandler implements Runnable {
        private Socket incoming;
        private int counter;

        public TestServerHandler(Socket incoming) {
            this.incoming = incoming;
        }

        @Override
        public void run() {
            try {
                try {
                    PrintWriter out = new PrintWriter(incoming.getOutputStream(), true);

                    while (counter < 100) {
                        if (++counter <= 10)
                            out.println(counter);
                        Thread.sleep(100);
                    }
                }
                finally {
                    incoming.close();
                    messages.append("Closing server\n");
                }
            } catch (Exception ex) {
                messages.append("\nTestServerHandelr.run: " + ex);
            }
        }
    }
}
