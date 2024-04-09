package server.api;

import commons.MailStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.service.EmailService;

@RestController
@RequestMapping("/mail")
public class MailController {
@Autowired
  private EmailService emailService;
  @PostMapping("/send/{mail}")
  public ResponseEntity<String> sendMail(@PathVariable String mail, @RequestBody MailStructure mailStructure){
    //emailService.updateMailSender();
  emailService.sendEmail(mail, mailStructure);
return  ResponseEntity.ok("Succesfuly sent");
  }

}
