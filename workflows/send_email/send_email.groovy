import javax.mail.*;
import javax.mail.internet.*;

public class SMTPAuthenticator extends Authenticator
{
    public PasswordAuthentication getPasswordAuthentication()
    {
        return new PasswordAuthentication(FD_SMTP_USER.toString(), FD_SMTP_PASSWORD.toCharArray());
    }
}

def d_email = FD_SMTP_USER.toString();
def d_uname = FD_SMTP_USER.toString();
def d_password = FD_SMTP_PASSWORD.toString();
def d_host = FD_SMTP_HOST_NAME.toString();
def d_port  = FD_SMTP_HOST_PORT.toString();
def m_to = "<TO>";
def m_subject = "<SUBJECT>";
def m_text = "<BODY>";

def props = new Properties();
props.put("mail.smtp.user", d_email);
props.put("mail.smtp.password", d_password);
props.put("mail.smtp.host", d_host);
props.put("mail.smtp.port", d_port);
props.put("mail.smtp.starttls.enable","true");
props.put("mail.smtp.debug", "true");
props.put("mail.smtp.auth", "true");
props.put("mail.smtp.socketFactory.port", d_port);
props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
props.put("mail.smtp.socketFactory.fallback", "true");
props.put("mail.smtp.ssl.enable", "false");

def auth = new SMTPAuthenticator();
def session = Session.getInstance(props, auth);
session.setDebug(true);

def msg = new MimeMessage(session);
msg.setText(m_text);
msg.setSubject(m_subject);
msg.setFrom(new InternetAddress(d_email));
msg.addRecipient(Message.RecipientType.TO, new InternetAddress(m_to));

Transport transport = session.getTransport("smtp");
transport.connect(d_host, FD_SMTP_HOST_PORT, d_uname, d_password);
transport.sendMessage(msg, msg.getAllRecipients());
transport.close();
