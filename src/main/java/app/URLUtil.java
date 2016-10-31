package app;

import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author nsoleeva
 *
 * This class used as a util class to work with URLs
 *
 */
public class URLUtil {
    private static final String yandexRssApiUrl = "http://blogs.yandex.ru/search.rss";

    // request parameters
    private static final String textQueryParamName = "text";
    private static final String limitQueryResultParam = "numdoc";
    private static final String resultLimit = "10";

    /**
     * This method is used to construct request URI to Yandex Blogs RSS API
     * URLUtil has inbuilt limitation to return only ten first results of search
     *
     * @param lineWord parameter used as a search criteria
     * @return the constructed URI
     * @throws URISyntaxException
     */
    public static URI buildRequestURI(String lineWord) throws URISyntaxException {
        return new URIBuilder(yandexRssApiUrl)
                .addParameter(textQueryParamName, lineWord)
                .addParameter(limitQueryResultParam, resultLimit)
                .build();
    }

    /**
     *
     * This method is used to figure out second level domain from host from url.
     *
     * @param url url to parse
     * @return second level domain parsed from url
     * @throws URISyntaxException
     */
    public static String getDomainName(String url) throws URISyntaxException {
        String host = new URI(url).getHost();
        if (host != null) {
            host = host.startsWith("www.") ? host.substring(4) : host;

            String[] domainLevels = host.split("\\.");
            if (domainLevels.length > 1) {
                return domainLevels[domainLevels.length - 2] + "." + domainLevels[domainLevels.length - 1];
            } else {
                throw new URISyntaxException(url, "Unable to determine the second level domain");
            }
        } else {
            throw new URISyntaxException(url, "Unable to determine the second level domain");
        }
    }
}
