package com.example.nthucs.prototype.Jsouptest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by admin on 2016/12/17.
 */
public class JsoupUse {
    public static void main(String[] args) throws IOException {
                String getarray =  getMyFitnessPalDateBase("蘋果" , 2);
                //System.out.print(getarray);
                String[] eachinfor = splitEveryInfor(getarray);
                //System.out.print(eachinfor);
    }
    public static String  getMyFitnessPalDateBase( String getname ,  int page) throws IOException  {
        String foodname = toUtf8(getname);
        String pagenum = Integer.toString(page);
        String searchstring = "http://www.myfitnesspal.com/zh-TW/food/search?authenticity_token=aykb4Sm9nI5Xl1ccrJf4FryP2jDOjgKBCSJURkMwkhE%3D&page=".concat(pagenum).concat("&search=").concat(foodname).concat("&utf8=%E2%9C%93");
        //中文網址問題
        String url = new String(); // 重新組成網址字串
        for (int j = 0; j < searchstring.length(); j++) // str是你的網址字串
        {
            if (searchstring.substring(j, j + 1).matches("[\\u4e00-\\u9fa5]+")) {
                url = url + URLEncoder.encode(searchstring.substring(j, j + 1),"UTF-8");
            } else {
                url = url + searchstring.substring(j, j + 1).toString();
            }
        }

        System.out.println(url);
        //http://www.myfitnesspal.com/zh-TW/food/search?authenticity_token=aykb4Sm9nI5Xl1ccrJf4FryP2jDOjgKBCSJURkMwkhE%3D&page=1&search= &utf8=%E2%9C%93
        Document doc1 = Jsoup.connect(url).get();
        String title = doc1.title();
        System.out.println(title);
        /*
        File input = new File("/tmp/input.html");
        Document doc = Jsoup.parse(input, "UTF-8", "http://www.myfitnesspal.com/zh-TW/food/search?authenticity_token=Gj3Y5YIi9n5FC%2BT0Vo8l%2FVhcWXYFWWssam67OO2Gp%2BI%3D&search=egg&utf8=%E2%9C%93");
           */
        Element content = doc1.getElementById("new_food_list");

        Elements links = content.getElementsByClass("odd");
        //System.out.println(links);
        String alltext = "";
        String nowtext = "";
        for (Element link : links) {
            /*String linkHref = link.attr("href");
            String linkText = "\n";
            if (link.text() == linkText) {
                break;
            } else {
                nowtext = link.text();

            }*/
            //System.out.println(nowtext);
             nowtext = link.text();
             alltext = alltext.concat("@").concat(nowtext);
        }
        //System.out.println(nowtext);
        //System.out.println(alltext);
        Elements linkss = content.getElementsByClass("even");
        for (Element link : linkss) {
            /*String linkHref = link.attr("href");
            String linkText = "\n";
            if (link.text() == linkText) {
                break;
            } else {
                nowtext = link.text();

            }*/
            //System.out.println(nowtext);
            nowtext = link.text();
            alltext = alltext.concat("@").concat(nowtext);
        }

        return alltext;
        // int load = Integer.parseInt(name[3]);
        //System.out.println(load);
        /*
        for(int i = 0 ; i < name.length ; i ++){
            System.out.println("name["+i+"] = "+name[i]);
        }
        */

        //Dictionary<key ,value >;

    }

    public static String[]  splitEveryInfor( String getname ) throws IOException  {
        /* 切割字串 */
        String putback = getname;
        String[] array = putback.split("@");
        for (int i = 0; i < array.length; i++) {
            System.out.println("array[" + i + "] = " + array[i]);
        }
        //String[] name = array[0].split(" ");
        return array;
    }

    public static String toUtf8(String str)throws IOException {
        return new String(str.getBytes("UTF-8"),"UTF-8");
    }
}