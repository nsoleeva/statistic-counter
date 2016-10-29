package app;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * @author nsoleeva
 */
public class RequestTask implements Runnable {

    private String requestWord;


    public RequestTask(String requestStr) {
        requestWord = requestStr;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        try {
            inputStream = Request.Get(URLHelper.buildRequestURI(requestWord))
                    .connectTimeout(1000)
                    .socketTimeout(1000)
                    .execute().returnContent().asStream();
            parseResult(inputStream);

        } catch (HttpResponseException e) {
            StringBuilder strb = new StringBuilder("Server response with error::");
            System.out.println(strb.append(e.getStatusCode()).append(" ").append(e.getMessage()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }

    public void parseResult(InputStream inputStream) {

        try {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new InputStreamReader(inputStream));

            // TODO: replace to foreach
            Iterator entryIter = feed.getEntries().iterator();
            while (entryIter.hasNext())
            {
                SyndEntry entry = (SyndEntry) entryIter.next();
                String link = entry.getLink();
                try {
                    String domainName = URLHelper.getDomainName(link);
                    // TODO: rewrite
                    ArrayList<String> savedLinks = Main.domains.get(domainName);
                    savedLinks = savedLinks != null ? savedLinks : new ArrayList<>();
                    if (!savedLinks.contains(link)) {
                        savedLinks.add(link);
                        Main.domains.put(domainName, savedLinks);
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            }
            System.out.println("Thread end execution");
        } catch (FeedException e) {
            e.printStackTrace();
        }
    }


}
