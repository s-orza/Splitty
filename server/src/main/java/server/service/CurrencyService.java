package server.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class CurrencyService {
    private final MainService mainService;
    //This is the service that will interact with the exchange API and would locally store the exchange rates.
    /**
     * This variable is a map that maps every date to a map of "from/to" with its exchange rate.
     * It is efficient.
     */
    //          date->     from/to->rate
    public Map<String, Map<String, Double>> currenciesMap=new HashMap<>();

    public CurrencyService(MainService mainService) {
        this.mainService = mainService;
    }

    /**
     * This method gives you the exchange rate, and it also handles savings in the local cached file.
     * @param date the date
     * @param from from currency
     * @param to to currency
     * @return the exchange rate
     */
    public double getExchangeRateAndUpdateCacheFile(String date,String from,String to)
    {
        //the url for the exchange api.
        String url= mainService.getBaseUrl()+"/api/fakeRateConverter/"+date+"?base="+from+"&symbol="+to;
        try {
            if(from.equals(to))
                return 1.0;
            RestTemplate restTemplate=new RestTemplate();
            //in case there is no rate cached on that date.
            if(!currenciesMap.containsKey(date))
            {
                double rate=restTemplate.getForObject(url, Double.class);
                if(rate==0) rate=1;
                //This entry contains:
                //    from/to -> rate
                //    to/from -> 1/rate
                Map<String, Double> entry = new HashMap<>();
                entry.put(from+"/"+to,rate);
                entry.put(to+"/"+from,1.0/rate);
                currenciesMap.put(date, entry);
                saveRateAndItsInverse(date,from,to,rate);
                return rate;
            }
            //a map that maps every from/to to its rate
            Map<String, Double> ratesFromDate = currenciesMap.get(date);
            //in case the exchange we want doesn't exist
            if(!ratesFromDate.containsKey(from+"/"+to))
            {
                double rate=restTemplate.getForObject(url, Double.class);
                if(rate==0) rate=1;
                ratesFromDate.put(from+"/"+to,rate);
                ratesFromDate.put(to+"/"+from,1.0/rate);
                saveRateAndItsInverse(date,from,to,rate);
                return rate;
            }
            return ratesFromDate.get(from+"/"+to);
        }
        catch (Exception e){
            return -1;
        }
//        2024-04-01/EUR/RON/3
//        2024-04-02/EUR/RON/5
    }
    /**
     * This method save the exchange rate from a date and its "inverse"
     * (the rate with 1/rate for undo)
     * @param date the date
     * @param from from currency
     * @param to to currency
     * @param rate the rate
     */
    private void saveRateAndItsInverse(String date, String from,String to,double rate)
    {
        Map<String,Double> ratesFromDate=currenciesMap.get(date);
        if(ratesFromDate==null)return;
        ratesFromDate.put(from+"/"+to,rate);
        ratesFromDate.put(to+"/"+from,1.0/rate);
        try{
            String content=date+"/"+from+"/"+to+"/"+rate+"\n"+
                    date+"/"+to+"/"+from+"/"+1.0/rate+"\n";
            Resource resource = new ClassPathResource("/exchangeRatesFile.txt");
            // resource.getFile()
            FileOutputStream writer=new FileOutputStream(resource.getFile(), true);
            writer.write(content.getBytes());
            writer.flush();
            writer.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
    /**
     * This method is good to use when the server starts
     * @return a confirmation message that everything was loaded
     */
    public ResponseEntity<String> loadExchangeRates()
    {
        try {
            // ClassPathResource resource = new ClassPathResource("/exchangeRatesFile.txt");
            Resource sourceResource = new ClassPathResource("/exchangeRatesFile.txt");
            InputStream inputStream = sourceResource.getInputStream();
            // Read the contents of the file
            byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
            String content = new String(bytes, StandardCharsets.UTF_8);
            List<String> lines= Arrays.stream(content.split("\n")).toList();
            //load into the map
            if(lines!=null)
                for(String line:lines)
                    if(line!=null && !line.isEmpty())
                    {
                        Scanner scanner=new Scanner(line);
                        scanner.useDelimiter("/");
                        String date=scanner.next();
                        String from=scanner.next();
                        String to=scanner.next();
                        double rate=Double.parseDouble(scanner.next());
                        //System.out.println(date+" "+from+" "+to+" "+rate);
                        if(!currenciesMap.containsKey(date))
                        {
                            Map<String,Double> entry=new HashMap<>();
                            entry.put(from+"/"+to,rate);
                            currenciesMap.put(date,entry);
                        }
                        else
                        {
                            Map<String,Double> ratesFromDate=currenciesMap.get(date);
                            ratesFromDate.put(from+"/"+to,rate);
                        }
                    }

        }catch (Exception e){
            System.out.println(e);
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok("Loaded!");
    }
}
