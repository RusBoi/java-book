package interruptible;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class InterruptibleSocketFrame extends JFrame {
    public final static int TEXT_ROWS = 20;
    public final static int TEXT_COLUMNS = 60;

    private JButton interruptibleButton;
    private JButton blockingButton;
    private JButton cancelButton;
    private JTextArea messages;

    private Scanner in;
    private TestServer server;
    private Thread connectThread;

    public InterruptibleSocketFrame() {
        JPanel northPanel = new JPanel();
        add(northPanel, BorderLayout.NORTH);

        messages = new JTextArea(TEXT_ROWS, TEXT_COLUMNS);
        DefaultCaret caret = (DefaultCaret)messages.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        add(new JScrollPane(messages));

        interruptibleButton = new JButton("Interruptible");
        blockingButton = new JButton("Blocking");
        northPanel.add(interruptibleButton);
        northPanel.add(blockingButton);
        interruptibleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                interruptibleButton.setEnabled(false);
                blockingButton.setEnabled(false);
                cancelButton.setEnabled(true);
                connectThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            connectInterruptobly();
                        } catch (IOException ex) {
                            messages.append("\nInterruptibleSocketTest.connectInterruptibly: " + ex);
                        }
                    }
                });
                connectThread.start();
            }
        });

        blockingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                interruptibleButton.setEnabled(false);
                blockingButton.setEnabled(false);
                cancelButton.setEnabled(true);
                connectThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            connectBlocking();
                        } catch (IOException ex) {
                            messages.append("\nInterruptibleSocketTest.connectBlocking: " + ex);
                        }
                    }
                });
                connectThread.start();
            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.setEnabled(false);
        northPanel.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectThread.interrupt();
                cancelButton.setEnabled(false);
            }
        });

        server = new TestServer(messages);
        new Thread(server).start();
        pack();
    }

    /**
     * Соединяет с сервером, используя блокирующий ввод-вывод
     */
    public void connectBlocking() throws IOException {
        messages.append("Blocking\n");
        try (Socket sock = new Socket("localhost", TestServer.PORT)) {
            in = new Scanner(sock.getInputStream());
            while (!Thread.currentThread().isInterrupted()) {
                messages.append("Reading ");
                if (in.hasNextLine()) {
                    messages.append(in.nextLine() + "\n");
                }
            }
        }
        finally {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    messages.append("Socket closed\n");
                    interruptibleButton.setEnabled(true);
                    blockingButton.setEnabled(true);
                }
            });
        }
    }

    /**
     * Соединяет с сервером, используя прерываемый ввод-вывод
     */
    public void connectInterruptobly() throws IOException {
        messages.append("Interruptible\n");
        try (SocketChannel sc = SocketChannel.open(new InetSocketAddress("localhost", TestServer.PORT))) {
            in = new Scanner(sc);
            while (!Thread.currentThread().isInterrupted()) {
                messages.append("Reading ");
                if (in.hasNextLine()) {
                    messages.append(in.nextLine() + "\n");
                }
            }
        }
        finally {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    messages.append("Channel closed\n");
                    interruptibleButton.setEnabled(true);
                    blockingButton.setEnabled(true);
                }
            });
        }
    }
}
