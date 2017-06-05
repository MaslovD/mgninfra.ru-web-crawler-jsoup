import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * Created by dmaslov on 01/06/17.
 */
public class CrawlerMain {


    public static void main(String[] args) throws IOException {

        String urlTemplate = "http://tiflocentre.ru/magazin/view_cat.php?cat={0}&podcat={1}&page={2}";
        String shopRootURL = "http://tiflocentre.ru/magazin/";
        int productCounter = 0;
        String delimiter = "|";


        for (Integer cat = 1; cat < 100; cat++) {

            for (Integer subCat = 1; subCat < 30; subCat++) {

                for (Integer pageNum = 1; pageNum < 3; pageNum++) {

                    Object[] params = new Object[]{cat.toString(), subCat.toString(), pageNum.toString()};

                    //System.out.println(cat.toString());

                    String url = MessageFormat.format(urlTemplate, params);
                    // System.out.println(url);

                    Document doc = Jsoup.connect(url).get();

                    Elements links = doc.select(".wp-product-list");


                    if (links.size() > 3) {
                        //System.out.println(links.size());


                        links.stream().forEach(l -> {
                            System.out.println("[PRODUCT]");
                            System.out.print(url);
                            System.out.print(delimiter);

                            Elements prodITitle = l.select(".text-product-list .prod-title-list a");
                            System.out.print(prodITitle.html().toString());
                            System.out.print(delimiter);

                            Elements prodDescription = l.select(".text-product-list p");
                            System.out.print(prodDescription.get(1).html().toString());
                            System.out.print(delimiter);

                            Elements prodImage = l.select(".img-product-list");
                            System.out.print(shopRootURL + prodImage.attr("src"));
                            System.out.print(delimiter);

//                            System.out.println("");
                        });

                    }
                }

            }
        }


    }
}
