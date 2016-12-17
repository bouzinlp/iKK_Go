package com.example.nthucs.prototype.Jsouptest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by admin on 2016/12/17.
 */
public class JsoupUse {
    public static void main(String[] args) throws IOException {
        //        String[] getarray =  getMyFitnessPalDateBase("egg");

    }
    public static String[]  getMyFitnessPalDateBase( String getname) throws IOException  {
        String searchstring = "http://www.myfitnesspal.com/zh-TW/food/search?authenticity_token=Gj3Y5YIi9n5FC%2BT0Vo8l%2FVhcWXYFWWssam67OO2Gp%2BI%3D&search=".concat(getname).concat("&utf8=%E2%9C%93");
        Document doc1 = Jsoup.connect(searchstring).get();
        String title = doc1.title();
        System.out.println(title);
        /*
        File input = new File("/tmp/input.html");
        Document doc = Jsoup.parse(input, "UTF-8", "http://www.myfitnesspal.com/zh-TW/food/search?authenticity_token=Gj3Y5YIi9n5FC%2BT0Vo8l%2FVhcWXYFWWssam67OO2Gp%2BI%3D&search=egg&utf8=%E2%9C%93");
           */
        Element content = doc1.getElementById("new_food_list");

        Elements links = content.getElementsByClass("odd");
        //System.out.println(links);
        String nowtext = "";
        for (Element link : links) {
            //String linkHref = link.attr("href");
            String linkText = "\n";

            if (link.text() == linkText) {
                break;
            } else {
                nowtext = link.text();
            }
            //System.out.println(nowtext);
            break;
        }
        String putback = "";
        putback = nowtext;
        //System.out.println(putback);

        /* 切割字串 */

        String[] array = putback.split(",");
        for (int i = 0; i < array.length; i++) {
            System.out.println("array[" + i + "] = " + array[i]);
        }
        String[] name = array[0].split(" ");
        return array;
        // int load = Integer.parseInt(name[3]);
        //System.out.println(load);
        /*
        for(int i = 0 ; i < name.length ; i ++){
            System.out.println("name["+i+"] = "+name[i]);
        }
        */

        //Dictionary<key ,value >;

    }
}