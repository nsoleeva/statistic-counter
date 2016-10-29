package app;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Natali on 25.10.2016.
 */
public class HttpClient {

    private static final String yandexRssApiUrl = "http://blogs.yandex.ru/search.rss";

    public static HashMap<String, ArrayList<String>> domains = new HashMap();

    private static URI buildRequestURI(String lineWord) throws URISyntaxException {
        URI uri = new URIBuilder(yandexRssApiUrl)
                .addParameter("text", lineWord)
                .addParameter("numdoc", "10")
                .build();
        System.out.println("buildRequestURI:: " + uri);
        return uri;
    }

    private static URL buildURL (String lineWord) throws URISyntaxException, MalformedURLException {
        return buildRequestURI(lineWord).toURL();
    }

    public static void executeRequest(String lineWord) {
        try {
            // TODO: I think it will be better to handle empty lines
/*            String str = Request.Get(buildRequestURI(lineWord))
                    .connectTimeout(1000)
                    .socketTimeout(1000)
                    .execute().returnContent().asString();
            System.out.println(str);*/

            InputStream inputStream = Request.Get(buildRequestURI(lineWord))
                    .connectTimeout(1000)
                    .socketTimeout(1000)
                    .execute().returnContent().asStream();

            parseResult(inputStream);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void parseResult(InputStream inputStream) {

        try {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new InputStreamReader(inputStream));
            //feed.setFeedType(outFormat); - use it if yandex API has some specific RSS format
            //System.out.println("feed:: " + feed.getDocs());

            // TODO: replace to foreach
            Iterator entryIter = feed.getEntries().iterator();
            while (entryIter.hasNext())
            {
                SyndEntry entry = (SyndEntry) entryIter.next();
                String link = entry.getLink();
                //System.out.println("link:: " + link);
                try {
                    String domainName = getDomainName(link);
                    //System.out.println("domainName:: " + domainName);

                    // TODO: rewrite
                    ArrayList<String> savedLinks = domains.get(domainName);
                    savedLinks = savedLinks != null ? savedLinks : new ArrayList<>();
                    if (!savedLinks.contains(link)) {
                        savedLinks.add(link);
                        domains.put(domainName, savedLinks);
                    }

                    //System.out.println("savedLinks:: " + savedLinks);


                    // TODO: need to asl about subdomains
                    //String str = "http://ru.google.com";
                    //domainName = getDomainName(str);
                    //System.out.println("domainName:: " + domainName);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            }
        } catch (FeedException e) {
            e.printStackTrace();
        } /*catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

/*    public static void getResult(String lineWord) {
        SyndFeedInput input = new SyndFeedInput();
        try {
            URL url = buildURL(lineWord);
            System.out.println(url);

            XmlReader xx = new XmlReader(url);
            System.out.println("xx" + xx.getEncoding());
            SyndFeed feed = input.build(xx);
        } catch (FeedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    private static String getDomainName(String url) throws URISyntaxException {
        // TODO: if you'll have time!
        //http://stackoverflow.com/questions/26938002/errors-when-using-guava-to-get-the-private-domain-name
/*        InternetDomainName domainName = InternetDomainName.from(host);
        this.domain = domainName.topPrivateDomain().name();*/


        String domain = new URI(url).getHost();
        if (domain != null) {
            domain = domain.startsWith("www.") ? domain.substring(4) : domain;

            String[] domainLevels = domain.split("\\.");
            if (domainLevels.length > 1) {
                domain = domainLevels[domainLevels.length - 2] + "." + domainLevels[domainLevels.length - 1];
            } else {
                throw new URISyntaxException(url, "Unable to determine the second level domain");
            }

        }
        return domain;
    }
}
