package server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.service.FakeConverterService;

@RestController
@RequestMapping("/api/fakeRateConverter")
public class FakeConverterController {
    //this is our api that gives rates. If you call 2 times the same rate on the same day, it will give you
    //the same answer
    //also rate(A,B)=1/rate(B/A)
    public final FakeConverterService fakeConverterService;

    public FakeConverterController(FakeConverterService fakeConverterService) {
        this.fakeConverterService = fakeConverterService;
    }

    @GetMapping("/{date}")
    public ResponseEntity<Double> getRate(@PathVariable("date") String date,
                                  @RequestParam("base")String from,
                                  @RequestParam("symbol")String to)
    {
        double rate=fakeConverterService.getRate(date,from,to);
        if(rate==-1)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(rate);
    }
}
