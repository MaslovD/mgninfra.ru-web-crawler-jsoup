import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by dmaslov on 01/06/17.
 */
public class CrawlerMain {

    public static void main(String[] args) throws IOException {
        String FILENAME = "/Users/dmaslov/Desktop/export.csv";
        String urlTemplate = "http://tiflocentre.ru/magazin/view_cat.php?cat={0}&podcat={1}&page={2}";
        String url = "";
        String shopRootURL = "http://tiflocentre.ru/magazin/";
        Integer prodId = 112345;
        int productCounter = 0;
        String doubleQuotes = "\"";
        String delimiter = doubleQuotes + "," + doubleQuotes;
        Object[] urlParams;
        Document doc;
        String catName;
        Integer pageNum;
        Integer curPage = 1;
        String subCatName = "";

        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            File file = new File(FILENAME);

            if (!file.exists()) {
                file.createNewFile();
            }

            // true = append file
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);

            String header = "ID,post_author,post_title,post_excerpt,post_content,post_name,product_categories,sku,featured_image,featured_image_name\n\r";
            bw.write(header);
            int[] catList = {1, 10, 11, 12, 13, 14, 15, 150, 16, 17,18,19,
                    2,20,21,22,25,27,28,29,
                    3,30,31,33,34,35,36,37,38,6,7,8,};

            Arrays.sort(catList);

            for (Integer cat : catList) {

                urlParams = new Object[]{cat.toString(), "0", "0"};
                url = MessageFormat.format(urlTemplate, urlParams);
                doc = Jsoup.connect(url).get();
                catName = doc.select("#header-content a").get(2).getAllElements().text();
                System.out.println(cat.toString() + "-" + catName);

                for (Integer subCat = 1; subCat < 200; subCat++) {

                    curPage = 1;
                    urlParams = new Object[]{cat.toString(), subCat.toString(), curPage.toString()};
                    url = MessageFormat.format(urlTemplate, urlParams);
                    doc = Jsoup.connect(url).get();

                    subCatName = doc.select("#header-content").get(0).textNodes().get(2).text().replace("➤", "").trim();

                    int pageQt = doc.select(".pstrnav li").size() - 1;

                    if ((pageQt < 0) || (pageQt == 0)) {
                        pageQt = 1;
                    }

                    Elements prodList = doc.select(".wp-product-list");
                    if (prodList.size() > 3) {
                        System.out.println("\t" + cat.toString()+"-"+subCat.toString() + ":" + subCatName);
                    }


                    while (curPage <= pageQt) {


                        if (prodList.size() > 3) {

                            Iterator<Element> crunchifyIterator = prodList.iterator();

                            while (crunchifyIterator.hasNext()) {

                                String prodID = prodId.toString();
                                String postAuthor = "1";
                                String postTitle = "";
                                String postExcerpt = "";
                                String postContent = "";
                                String postName = "";
                                String productCategories = catName.toString() + ">" + subCatName.toString();
                                String sku;
                                String imageURL;
                                String imageName;
                                String prodLine = "";


                                // 1149762324, 1, "title", "content", "post name", "product type", "SKU", "http://mgn.incorpics.ru/wp-content/uploads/2017/04/taktilnaja-poliuretanovaja-plitka-10131.jpg", "image name"
                                Element elem = crunchifyIterator.next();

                                Elements prodITitle = elem.select(".text-product-list .prod-title-list a");
                                postTitle = prodITitle.html().toString();
                                String prodSku = elem.select(".wrPrice-list .prod-title-list").text().replace("артикул ","");

                                Elements prodDescription = elem.select(".text-product-list p");
                                postContent = prodDescription.get(1).html().toString().replace("\"", "'");

                                Elements prodImage = elem.select(".img-product-list");
                                imageURL = shopRootURL + prodImage.attr("src");

                                //String header = "ID,post_author,post_title,post_excerpt,post_content,post_name,product_type,sku,featured_image,featured_image_name";
                                imageName = "Изображение товара";
                                sku = prodSku+prodId.toString();

                                prodLine = doubleQuotes + prodID + delimiter + postAuthor + delimiter + postTitle + delimiter + postExcerpt
                                        + delimiter + postContent + delimiter + postName + delimiter + productCategories + delimiter + sku + delimiter + imageURL + delimiter + imageName + doubleQuotes + "\r\n";

                                bw.write(prodLine);

                                //System.out.print(prodLine);

                                prodLine = "";

                                prodId++;
                            }

                            //System.out.println(cat.toString() + "-" + subCat.toString() + "-" + pageNum.toString());
                        } else {
                            //System.out.println("[NO-PRODUCTS]" + cat.toString() + "-" + subCat.toString() + "-" + pageNum.toString());
                        }

                        if (curPage < pageQt) {
                            curPage++;
                            urlParams = new Object[]{cat.toString(), subCat.toString(), curPage.toString()};
                            url = MessageFormat.format(urlTemplate, urlParams);
                            doc = Jsoup.connect(url).get();
                            prodList = doc.select(".wp-product-list");
                        } else {
                            break;
                        }
                    }
                }
            }
            System.out.println("[DONE]");


        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }


        }
    }
}
