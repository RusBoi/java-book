package interruptible;

import javax.swing.*;
import java.awt.*;

/**
 * В это программе демонстируется прерывание сокета через канал
 */

public class InterruptibleSocketTest {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame f = new InterruptibleSocketFrame();
                f.setName("Interruptible Socket Test");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setVisible(true);
            }
        });
    }
}
