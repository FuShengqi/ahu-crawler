/**
 * Created by FuShengqi on 2017/7/17.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.Header;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Yasser Ganjisaffar
 */
public class BasicCrawler extends WebCrawler {

    private static final Pattern IMAGE_EXTENSIONS = Pattern.compile(".*\\.(bmp|gif|jpg|png)$");

    private ArrayList<News> newsList;
    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        if(href.equals("http://jwc.ahu.cn/main/notice.asp?page=5")){
            System.out.println("current url=http://jwc.ahu.cn/main/notice.asp?page=5");
        }
        // Ignore the url if it has an extension that matches our defined set of image extensions.
        if (IMAGE_EXTENSIONS.matcher(href).matches()) {
            return false;
        }

        // Only accept the url if it is in the "www.ics.uci.edu" domain and protocol is "http".
        //Pattern p1=Pattern.compile("http://jwc\\.ahu\\.cn/main/show\\.asp\\?id=\\d+$");
        Pattern p2=Pattern.compile("http://jwc\\.ahu\\.cn/main/index\\.asp$");
        Pattern p3=Pattern.compile("http://jwc\\.ahu\\.cn/main/notice\\.asp([?page=\\d+]*)$");
        //return href.startsWith("http://jwc.ahu.cn/main/");
        String ahu1="http://jwc.ahu.cn/main/notice.asp?page=5";
        String ahu2="http://jwc.ahu.cn/main/notice.asp";
        //System.out.println("ahu1------"+p3.matcher(ahu1).matches()+"-----------");
        /*System.out.println("ahu2------"+p3.matcher(ahu2).matches()+"-----------");*/
        if(p2.matcher(href).matches()||p3.matcher(href).matches()){
            System.out.println(href+"is ok");
        }
        return p2.matcher(href).matches()||p3.matcher(href).matches();
    }
    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    @Override
    public void visit(Page page) {

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            Document doc= Jsoup.parse(html);
            WebURL webUrl=page.getWebURL();
            String url=webUrl.getURL();

            /*if(url.endsWith("2")){
                for(WebURL webURL:links){
                    System.out.println(webURL.getURL());
                }
            }*/

            News news=new News();
            File file=new File("E:\\news.txt");
            try {
                FileOutputStream fos=new FileOutputStream(file,true);
                Pattern p2=Pattern.compile("http://jwc\\.ahu\\.cn/main/index\\.asp$");
                Pattern p3=Pattern.compile("http://jwc\\.ahu\\.cn/main/notice\\.asp([?page=\\d+]*)");

                if(p2.matcher(url).matches()) {
                    Elements elements = doc.select("body > table > tbody > tr:nth-child(2) > td:nth-child(2) > table > tbody > tr:nth-child(6) > td:nth-child(1) > table:nth-child(1) > tbody > tr:nth-child(1) > td:nth-child(3) > table > tbody > tr:nth-child(2) > td > table > tbody > tr:nth-child(2) > td > table > tbody > tr");
                    for (Element element : elements) {
                        Elements tds = element.select("td");
                        for (Element td : tds) {
                            String title=td.attr("title");
                            System.out.println(title+"-----"+url);

                            String date=td.select("font").text();
                            System.out.println(date);

                            date="Date:"+date+"\r\n";
                            title="Title:"+title+"\r\n\r\n";
                            String record=date+title;
                            fos.write(record.getBytes());
                            //fos.close();
                            //data=td.select("font").text().getBytes();

                            date="";
                            title="";
                        }
                    }
                }else if(p3.matcher(url).matches()){
                    Elements elementsInNextPage=doc.select("body > table > tbody > tr:nth-child(2) > td:nth-child(2) > table:nth-child(1) > tbody > tr:nth-child(5) > td > table > tbody > tr > td:nth-child(3) > table > tbody > tr");
                    for(Element element:elementsInNextPage){
                        Elements as=element.select("td > a");
                        Elements times=element.select("td.timecss");

                        String title="";
                        String newsUrl="";
                        for(Element a:as){
                            title=a.text();
                            newsUrl=a.attr("href");
                            System.out.println(a.text()+"-----"+url);
                        }

                        String date="";
                        for(Element time:times){
                            date=time.text();
                            System.out.println(time.text());
                        }

                        if(date==""||title=="")
                            continue;
                        date="Date:"+date+"\r\n";
                        title="Title:"+title+"\r\n";
                        newsUrl="URL:http://jwc.ahu.cn/main/"+newsUrl+"\r\n\r\n";
                        String record=date+title+newsUrl;
                        fos.write(record.getBytes());
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        logger.debug("=============");
    }

}