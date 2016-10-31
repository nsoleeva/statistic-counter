package app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO: Will be cool to have some logger which we will can to switch on/off
// TODO: What about tests??? It will be great, but I'm not sure about it

    // TODO: create properties file for logging

/**
 *
 * @author nsoleeva
 *
 * Statistic counter app is responsible for collecting usage statistic for second level domains in search results.
 * From this statistic you can get know how many times second level domain is used in the links
 * provided by Yandex Blogs RSS API as a result of requests by key words from the file.
 *
 * This class roles as a entry point.
 *
 */
public class Main {

    public static ConcurrentHashMap<String, ArrayList<String>> domains = new ConcurrentHashMap();
    public static Logger log = Logger.getLogger("myApp");

    public static void main( String[] args ) {
        log.setLevel(Level.INFO);
        try {
            String fileName;
            if (args.length > 0) {
                fileName = args[0];
            } else {
                throw new IOException();
            }

            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            ExecutorService executorService = Executors.newFixedThreadPool(10);

            while (scanner.hasNextLine()) {
                String str = scanner.nextLine();
                log.fine("processing request for " + str + "...");
                if (!str.isEmpty()) {
                    executorService.execute(new RequestTask(str.trim()));
                }
            }

            try {
                executorService.shutdown();
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                log.fine(e.getMessage());
            }

            printResults();
        } catch (IOException e) {
            log.info("Input file not found or not specified. Please, provide correct file name.");
        }
    }


    /**
     * Method to print final usage statistic result
     */
    private static void printResults() {
        HashMap<String, Integer> statisticResults = new HashMap<>();
        for (String domainName : domains.keySet()) {
            statisticResults.put(domainName, domains.get(domainName).size());
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(statisticResults);
        // TODO: change out to log
        //log.info();
        System.out.println("\nStatistic results \n" + json);
    }
}
