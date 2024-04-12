package server.service;

import commons.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

@Service
public class EmailService {
  private JavaMailSender mailSender;
  private String username;

  public void sendEmail(String mail, MailStructure mailStructure){
    mailSender = updateMailSender();
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(username);
    message.setSubject(mailStructure.getSubject());
    message.setText(mailStructure.getMessage());
    message.setTo(mail);
    message.setCc(username);
    mailSender.send(message);
    System.out.println("Sent email" + username + " -> " + mail);
  }

  public JavaMailSender updateMailSender(){
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost("smtp.gmail.com");
    mailSender.setPort(587);
    MainService service = new MainService();
    try {
      AppConfig newConfig = service.readConfig();
      String regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
      if (newConfig.getEmail() != null && newConfig.getEmail().matches(regex)) {
        mailSender.setUsername(newConfig.getEmail());
        username = newConfig.getEmail();
      }
      if (newConfig.getPassword() != null) {
        mailSender.setPassword(newConfig.getPassword());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.debug", "true");

    return  mailSender;
  }
}
