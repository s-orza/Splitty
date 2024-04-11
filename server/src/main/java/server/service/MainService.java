package server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.*;
import org.springframework.boot.SpringApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

@Service
public class MainService {

  static String ip;
  static String port;
  public MainService(){}

  public void genPass()
  {
    final String uri =  "http://" + ip + ":" + port + "/api/password";

    Password password = new Password();
    RestTemplate restTemplate = new RestTemplate();

    // delete all old passwords (0 or 1)
    restTemplate.delete(uri);

    // post new password
    ResponseEntity<Password> response = restTemplate.postForEntity(uri, password, Password.class);
  }
  public void loadExchangeRates()
  {
    //load cached exchange rates
    final String url = getBaseUrl()+"/api/foreignCurrencies/preparationsToLoad";
    RestTemplate restTemplate = new RestTemplate();

    //This method is useful when you already have some exchange rates in the file.
    //If not, it will start from an empty file (most of the time)
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
  }

  /**
   * It gives you the first part of the url.
   * @return the first part of the url.
   */
  public String getBaseUrl()
  {
    //this is just to be sure that they are not null
    if(ip==null ||port==null)
    {
      System.out.println("Ip or port is null");
      return "http://127.0.0.1:8080";
    }
    return "http://"+ip+":"+port;
  }

  public void setAddress(SpringApplication app){
    try {
      AppConfig newConfig = readConfig();
      if (  newConfig.getIp() != null) {
        app.setDefaultProperties(Collections
                .singletonMap("server.address", newConfig.getIp()));
        ip = newConfig.getIp();
      }
      if(newConfig.getPort() != null){
        app.setDefaultProperties(Collections
                        .singletonMap("server.port", newConfig.getPort() ));
        port = newConfig.getPort();
      }
    }catch(Exception e){
      e.printStackTrace();
    }
    System.out.println("Running on ip : " + ip + ":" +port);
  }
  public AppConfig readConfig() throws Exception {
    File selectedFile = new File("../App-Config.json");
    if (selectedFile != null && selectedFile.getName().contains(".json")) {
      ObjectMapper mapper = new ObjectMapper();
      try {
        System.out.println("This file" + new String(Files.readAllBytes(Paths.get(selectedFile.toURI()))));
        AppConfig config = mapper.readValue(selectedFile, AppConfig.class);
        System.out.println(config);
        return config;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return new AppConfig();
  }

}
