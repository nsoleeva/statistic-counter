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
public class DomainUsageStatisticApp {

    static {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
    }

    public static ConcurrentHashMap<String, ArrayList<String>> domains = new ConcurrentHashMap();

    public static void main( String[] args ) {
        try {
            String fileName;
            if (args.length > 0) {
                fileName = args[0];
            } else {
                throw new IOException();
            }

            File file = new File(fileName);
            Scanner scanner = new Scanner(file, "UTF-8");
            ExecutorService executorService = Executors.newFixedThreadPool(10);

            while (scanner.hasNextLine()) {
                String str = scanner.nextLine();
                if (!str.isEmpty()) {
                    executorService.execute(new RequestTask(str.trim()));
                }
            }

            try {
                executorService.shutdown();
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }

            printResults();
        } catch (IOException e) {
            System.out.println("Input file not found or not specified. Please, provide correct file name.");
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
        System.out.println("\nStatistic results \n" + json);
    }
}
