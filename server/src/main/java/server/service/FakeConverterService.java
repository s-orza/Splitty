package server.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class FakeConverterService {
    //we need to have this service

    //some real conversion rates from 2024-04-04
    private double baseEurToUsd=1.085711;
    private double baseEurToRon=4.97017;
    private double baseEurToChf=0.983873;
    public double getRate(String date,String from,String to)
    {
        if(from.equals(to))
            return 1.0;
        //use the date as a seed to get the same rates if we call this function multiple times on the same date.
        Random random=new Random(date.hashCode());
        double ourRandom;
        double ourEurToUsd;
        double ourEurToRon;
        double ourEurToChf;
        //create conversion rate from that date
        //we need random randoms for different currencies.
        ourRandom=getRandomPercentage(random);
        ourEurToUsd=baseEurToUsd+baseEurToUsd*(ourRandom/100);
        ourRandom=getRandomPercentage(random);
        ourEurToRon=baseEurToRon+baseEurToRon*(ourRandom/100);
        ourRandom=getRandomPercentage(random);
        ourEurToChf=baseEurToChf+baseEurToChf*(ourRandom/100);
        Map<String,Double> namesToNumbers=new HashMap<>();

        namesToNumbers.put("EUR/RON",ourEurToRon);
        namesToNumbers.put("RON/EUR",1.0/ourEurToRon);

        namesToNumbers.put("EUR/USD",ourEurToUsd);
        namesToNumbers.put("USD/EUR",1.0/ourEurToUsd);

        namesToNumbers.put("EUR/CHF",ourEurToChf);
        namesToNumbers.put("CHF/EUR",1.0/ourEurToChf);

        //if we can stop here
        if(namesToNumbers.containsKey(from+"/"+to))
            return namesToNumbers.get(from+"/"+to);
        //RON->EUR->USD
        namesToNumbers.put("RON/USD",1.0/ourEurToRon*ourEurToUsd);
        namesToNumbers.put("USD/RON",1.0/ourEurToUsd*ourEurToRon);

        namesToNumbers.put("RON/CHF",1.0/ourEurToRon*ourEurToChf);
        namesToNumbers.put("CHF/RON",1.0/ourEurToChf*ourEurToRon);

        namesToNumbers.put("CHF/USD",1.0/ourEurToChf*ourEurToUsd);
        namesToNumbers.put("USD/CHF",1.0/ourEurToUsd*ourEurToChf);

        if(namesToNumbers.containsKey(from+"/"+to))
            return namesToNumbers.get(from+"/"+to);
        return -1;
    }
    public double getRandomPercentage(Random random)
    {
        //random.nextDouble()->numbers in [0,1)
        //[0,1)->[-0.5,0.5)->[-1,1)->[-10,10)
        return (random.nextDouble()-0.5)*2*10;
    }
}
