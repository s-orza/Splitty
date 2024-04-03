package server.api;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/foreignCurrencies")
public class ForeignCurrenciesController {

    //          date->     from/to->rate
    public Map<String, Map<String, Double>> currenciesMap=new HashMap<>();

    @GetMapping("/{date}")
    public ResponseEntity<Double> getExchangeRate(@PathVariable("date") String date,
                                                  @RequestParam("from") String from,
                                                  @RequestParam("to") String to)
    {
        if(from.equals(to))
            return ResponseEntity.ok(1.0);
        //loadExchangeRates();
        //in case there is no rate cached on that date.
        System.out.println(currenciesMap);
        if(!currenciesMap.containsKey(date))
        {
            //put
            Random random=new Random();
            double rate=random.nextDouble();
            if(rate==0) rate=1;
            //This entry contains:
            //    from/to -> rate
            //    to/from -> 1/rate
            Map<String,Double> entry=new HashMap<>();
            entry.put(from+"/"+to,rate);
            entry.put(to+"/"+from,1.0/rate);
            currenciesMap.put(date,entry);
            saveRateAndItsInverse(date,from,to,rate);
            return ResponseEntity.ok(rate);
        }
        //a map that maps every from/to to its rate
        Map<String,Double> ratesFromDate=currenciesMap.get(date);
        //in case the exchange we want doesn't exist
        if(!ratesFromDate.containsKey(from+"/"+to))
        {
            Random random=new Random();
            double rate=random.nextDouble();
            if(rate==0) rate=1;
            ratesFromDate.put(from+"/"+to,rate);
            ratesFromDate.put(to+"/"+from,1.0/rate);
            saveRateAndItsInverse(date,from,to,rate);
            return ResponseEntity.ok(rate);
        }
        return ResponseEntity.ok(ratesFromDate.get(from+"/"+to));
//        2024-04-01/EUR/RON/3
//        2024-04-02/EUR/RON/5
    }
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
            FileOutputStream  writer=new FileOutputStream(resource.getFile(), true);
            writer.write(content.getBytes());
            writer.flush();
            writer.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    @GetMapping("/preparationsToLoad")
    public ResponseEntity<String> loadExchangeRates()
    {
        System.out.println("loadingggggg");
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
                    System.out.println(date+" "+from+" "+to+" "+rate);
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
