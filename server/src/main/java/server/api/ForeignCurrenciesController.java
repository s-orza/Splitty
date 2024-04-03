package server.api;

import org.springframework.core.io.ClassPathResource;
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
    public ResponseEntity<String> getExchangeRate(@PathVariable("date") String date,
                                                  @RequestParam("from") String from,
                                                  @RequestParam("to") String to)
    {
        //loadExchangeRates();
        //in case there is no rate cached on that date.
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
            return ResponseEntity.ok(rate+"");
        }
        Map<String,Double> ratesFromDate=currenciesMap.get(date);
        //in case the exchange we want doesn't exist
        if(!ratesFromDate.containsKey(from+"/"+to))
        {
            Random random=new Random();
            double rate=random.nextDouble();
            if(rate==0) rate=1;
        }
        return ResponseEntity.ok("1.0");
//        2024-04-01/EUR/RON/3
//        2024-04-02/EUR/RON/5
    }
    @GetMapping("/preparationsToLoad")
    public ResponseEntity<String> loadExchangeRates()
    {
        try {
            ClassPathResource resource = new ClassPathResource("/exchangeRatesFile.txt");
            InputStream inputStream = resource.getInputStream();
            // Read the contents of the file
            byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
            String content = new String(bytes, StandardCharsets.UTF_8);
            List<String> lines= Arrays.stream(content.split("\n")).toList();
            //load into the map
            for(String line:lines)
            {
                Scanner scanner=new Scanner(line);
                scanner.useDelimiter("/");
                String date=scanner.next();
                String from=scanner.next();
                String to=scanner.next();
                double rate=Double.parseDouble(scanner.next());
                System.out.println(date+" "+from+" "+to+" "+rate);
            }

        }catch (Exception e){
            System.out.println(e);
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok("Loaded!");
    }
    @GetMapping("/preparationsToSave")
    public ResponseEntity<String> saveExchangeRates()
    {
        try{
            String content="Hello\nBuna\n";
            ClassPathResource resource = new ClassPathResource("/exchangeRatesFile.txt");
           // resource.getFile()
            Writer writer=new FileWriter(resource.getFile());
            writer.write(content);
            writer.flush();
            writer.close();


        }
        catch (Exception e)
        {
            System.out.println(e);
            return ResponseEntity.internalServerError().build();

        }
        return ResponseEntity.ok("Saved!");
    }
}
