package server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.service.CurrencyService;

@RestController
@RequestMapping("/api/foreignCurrencies")
public class ForeignCurrenciesController {
    public final CurrencyService currencyService;

    public ForeignCurrenciesController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    /**
     * This method gives you the exchange rate, and it also handles savings in the local cached file.
     * @param date the date
     * @param from from currency
     * @param to to currency
     * @return the exchange rate
     */
    @GetMapping("/{date}")
    public ResponseEntity<Double> getExchangeRate(@PathVariable("date") String date,
                                                  @RequestParam("from") String from,
                                                  @RequestParam("to") String to)
    {
        double rate=currencyService.getExchangeRateAndUpdateCacheFile(date,from,to);
        if(rate==-1)
            return ResponseEntity.internalServerError().build();
        return ResponseEntity.ok(rate);
    }

    /**
     * This method is good to use when the server starts
     * @return a confirmation message that everything was loaded
     */
    @GetMapping("/preparationsToLoad")
    public ResponseEntity<String> loadExchangeRates()
    {
        return currencyService.loadExchangeRates();
    }
}
