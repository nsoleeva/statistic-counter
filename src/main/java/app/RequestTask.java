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

/**
 * @author nsoleeva
 *
 * RequestTask is a runnable task for request to Yandex Blogs RSS API
 *
 */
public class RequestTask implements Runnable {

    private String requestWord;

    /**
     * RequestTask constructor
     * @param requestStr requestStr is a string word used as a search criteria when task will be executed
     */
    public RequestTask(String requestStr) {
        requestWord = requestStr;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = Request.Get(URLUtil.buildRequestURI(requestWord))
                    .connectTimeout(1000)
                    .socketTimeout(1000)
                    .execute().returnContent().asStream();
            parseRssResult(inputStream);

        } catch (HttpResponseException e) {
            StringBuilder strb = new StringBuilder("Server response with error::");
            System.err.println(strb.append(e.getStatusCode()).append(" ").append(e.getMessage()));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (URISyntaxException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Parse response in RSS format and submit result to map with statistic
     * @param inputStream is a InputStream which contains response to request to Yandex API in RSS
     */
    private void parseRssResult(InputStream inputStream) {
        try {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new InputStreamReader(inputStream));

            for (SyndEntry entry : feed.getEntries()) {
                String link = entry.getLink();
                try {
                    // lets parse second level domain from the link
                    String domainName = URLUtil.getDomainName(link);

                    // filter out the repeated links and update statistic
                    ArrayList<String> savedLinks = DomainUsageStatisticApp.domains.get(domainName);
                    if (savedLinks != null) {
                        if (!savedLinks.contains(link)) {
                            savedLinks.add(link);
                        }
                    } else {
                        savedLinks = new ArrayList<>();
                        savedLinks.add(link);
                        DomainUsageStatisticApp.domains.put(domainName, savedLinks);
                    }

                } catch (URISyntaxException e) {
                    System.err.println(e.getMessage());
                }
            }
        } catch (FeedException e) {
            System.err.println(e.getMessage());
        }
    }


}
