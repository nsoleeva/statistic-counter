package app;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author Natali
 */

// TODO: !!! Don't forget to add java doc
// TODO: Will be cool to have some logger which we will can to switch on/off
// TODO: What about tests??? It will be great, but I'm not sure about it

public class Main {

    // TODO: think may be we can use documentation DB to store responses...
    //       No, it means we will need some installed DB. I don't think it's a good idea.
    //       We can think about Derby or somethink like IMDBs - it's like a Map, so we will need some keys... I don't know how we can do it


    //public static String yandexRSSApiUrlExample = "http://blogs.yandex.ru/search.rss?text=scala";

    // TODO: parse RSS response
    // TODO: create ThreadPool and ThreadExecutor
    // TODO: create queue
    // TODO: store responses


    public static void main( String[] args ) {
        try {
            File file;
            if (args.length > 0) {
                file = new File(args[0]);
            } else {
                //FIXME: System.out.println("Working Directory = " + System.getProperty("user.dir"));
                file = new File("src/main/java/app/inputFile.txt");
            }
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String str = scanner.nextLine();
                System.out.println(str);
                HttpClient.executeRequest(str);
                // TODO: try request to API
            }

            System.out.println("\nResult statistic");
            for (String domainName : HttpClient.domains.keySet()) {
                System.out.println(domainName + ": " + HttpClient.domains.get(domainName).size());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
