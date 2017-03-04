package mail;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.Console;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class MailTest {
    public final static String FROM = "egor.druzhinin97@gmail.com";
    public final static String TO = "kivixit@yandex.ru";
    public final static String SUBJECT = "JAVA TEST";
    public final static String MESSAGE = "HELLO! I'VE DONE IT\n:)";

    public static void main(String[] args) throws IOException, MessagingException {
        Properties props = new Properties();
        try (InputStream is = new FileInputStream("src/mail/mes.properties")) {
            props.load(is);
        }
        Console console = System.console();
        String password = new String(console.readPassword("Password: "));

        Session mailSession = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(mailSession);
        message.setFrom(new InternetAddress(FROM));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(TO));
        message.setSubject(SUBJECT);
        message.setText(MESSAGE);
        Transport tr = mailSession.getTransport();
        try {
            tr.connect(null, password);
            tr.sendMessage(message, message.getAllRecipients());
        }
        finally {
            tr.close();
        }
    }
}
