package commons;

import java.util.ArrayList;
import java.util.Locale;

public class AppConfig {
  String currency;
  ArrayList<Long> recentEvents;
  String serverUrl;
  Locale lang;

  public Locale getLang() {
    return lang;
  }

  public AppConfig(String currency, ArrayList<Long> recentEvents, String serverUrl, Locale lang) {
    this.currency = currency;
    this.recentEvents = recentEvents;
    this.serverUrl = serverUrl;
    this.lang = lang;
  }

  @Override
  public String toString() {
    return "AppConfig{" +
            "currency='" + currency + '\'' +
            ", recentEvents=" + recentEvents +
            ", serverUrl='" + serverUrl + '\'' + lang +
            '}';
  }

  public void setLang(Locale lang) {
    this.lang = lang;
  }

  public AppConfig(){
    currency = "EUR";
    recentEvents = new ArrayList<>();
    serverUrl = "http://localhost:8080/";
    lang = new Locale("en", "US");
  }

  public String getCurrency() {
    return currency;
  }

  public ArrayList<Long> getRecentEvents() {
    return recentEvents;
  }

  public String getServerUrl() {
    return serverUrl;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public void setRecentEvents(ArrayList<Long> recentEvents) {
    this.recentEvents = recentEvents;
  }

  public void setServerUrl(String serverUrl) {
    this.serverUrl = serverUrl;
  }
}
