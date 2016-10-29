package app;

import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author nsoleeva
 */
public class URLHelper {

    private static final String yandexRssApiUrl = "http://blogs.yandex.ru/search.rss";

    public static URI buildRequestURI(String lineWord) throws URISyntaxException {
        URI uri = new URIBuilder(yandexRssApiUrl)
                .addParameter("text", lineWord)
                .addParameter("numdoc", "10")
                .build();
        System.out.println("buildRequestURI:: " + uri);
        return uri;
    }

    public static String getDomainName(String url) throws URISyntaxException {
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
