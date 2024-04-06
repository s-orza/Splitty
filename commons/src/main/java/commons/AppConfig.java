package commons;

import java.util.ArrayList;
import java.util.Locale;

public class AppConfig {
  String currency;
  ArrayList<Long> recentEvents;
  String ip;
  String port;
  Locale lang;

  String email;
  String password;

  public AppConfig(String currency, ArrayList<Long> recentEvents, String ip, String port, Locale lang) {
    this.currency = currency;
    this.recentEvents = recentEvents;
    this.ip = ip;
    this.port = port;
    this.lang = lang;
  }

  public AppConfig() {
    this.currency = "EUR";
    this.recentEvents = new ArrayList<>();
    this.ip = "127.0.0.1";
    this.port = "8080";
    this.lang = new Locale("en", "US");
    this.email = "";
    this.password = "";
  }

  @Override
  public String toString() {
    return "AppConfig{" +
            "currency='" + currency + '\'' +
            ", recentEvents=" + recentEvents +
            ", ip='" + ip + '\'' +
            ", port='" + port + '\'' +
            ", language='" + lang +
            '}';
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Locale getLang() {
    return lang;
  }

  public void setLang(Locale lang) {
    this.lang = lang;
  }

  public String getCurrency() {
    return currency;
  }

  public ArrayList<Long> getRecentEvents() {
    return recentEvents;
  }

  public String getIp() {
    return ip;
  }
  public String getPort() {
    return port;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public void setRecentEvents(ArrayList<Long> recentEvents) {
    this.recentEvents = recentEvents;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }
  public void setPort(String port) {
    this.port = port;
  }

}
