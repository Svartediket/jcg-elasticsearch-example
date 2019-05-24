package com.javacodegeeks.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetArticles {
    private static final String EVENT = "(митинг[а-я]{0,3})|(акци[а-я]{0,3} протеста)|(протест[а-я]{0,3})";

    private static final String DATE = "((\\s(с|со|в|до|к|через|на)\\s([а-я]+\\s)*(выходн([а-я])*,|воскресень([а-я])*,|понедельник([а-я])*,|вторник([а-я])*,|сред([а-я])*,|четверг([а-я])*|пятниц([а-я])*,|суббот([а-я])*,|недел([а-я])*,)"
            + "(( [0-9]{1,2}( (января|февраля|марта|мая|апреля|июня|июля|августа|сентября|октября|ноября|декабря)){0,1}( [0-9]{4} год([а-я])*)*)|(( (с|c) )*[0-9]{1,2}( (января|февраля|марта|мая|апреля|июня|июля|августа|сентября|октября|ноября|декабря)){0,1}( [0-9]{4} год([а-я])*)*( по [0-9]{1,2}( (января|февраля|марта|мая|апреля|июня|июля|августа|сентября|октября|ноября|декабря))( [0-9]{4} год([а-я])*)*)*))*)|"
            + "(( [0-9]{1,2}( (января|февраля|марта|мая|апреля|июня|июля|августа|сентября|октября|ноября|декабря)){1}( [0-9]{4} год([а-я])*)*)|"
            + "((^| )(с|c) [0-9]{1,2}( (января|февраля|марта|мая|апреля|июня|июля|августа|сентября|октября|ноября|декабря)){0,1}( [0-9]{4} год([а-я])*)*( по [0-9]{1,2}( (января|февраля|марта|мая|апреля|июня|июля|августа|сентября|октября|ноября|декабря))( [0-9]{4} год([а-я])*)*)*)))";

    private static final String url = "https://www.m24.ru/news";

    public List<Article> getArticles(){
        //это реальные статьи, взятые с сайта
        /*List<Article> listOfJSON = articleInfoFromSite();
        return listOfJSON;*/

        //это тестовые статьи для проверки работоспособности программы
        List<Article> listOfTest = JSONarticleInfoFromTestData();
        return listOfTest;
    }

    private List<Article> JSONarticleInfoFromTestData() {
        List<Article> listOfTest = new ArrayList<Article>();

        String href = "www.testHref1.ru";
        String title = "Тестовый заголовок 1";
        String text = "Новости дня. Сегодня, 30 сентября 2019 года, пройдет митинг на улице Никольской. Митинг посвящен какому-то непонятному событию. Можно приходить 26 мая, а можно не приходить. До встречи.";
        String[] info = analis(text);
        listOfTest.add(new Article(href, title, text, info[0], info[1]));

        href = "www.testHref2.ru";
        title = "Тестовый заголовок 2";
        text = "Новости дня. Не пропустите, в эту субботу, 25 мая, пройдут акции протеста, направленные на бла бла бла. Акции посвящены неизвестно чему. Можно приходить 27 мая, а можно не приходить. До встречи.";
        info = analis(text);

        listOfTest.add(new Article(href, title, text, info[0], info[1]));

        return listOfTest;
    }

    private List<Article> articleInfoFromSite() {
        List<Article> listOfJSON = new ArrayList<Article>();
        try {
            Document docHref;
            Document doc = Jsoup.connect(url).get();

            Elements cards, texts;
            String href = "", title = "", text = "";
            Elements listOfNews = doc.select("div");
            for (Element element : listOfNews){
                if (element.attr("class").equals("b-materials-list b-list_infinity")){
                    cards = element.getElementsByTag("li");
                    for(Element card : cards){
                        href = card.getElementsByTag("a").attr("href");
                        title = card.text();
                        docHref = Jsoup.connect("https://www.m24.ru" + href).get();
                        text = docHref.select("p").text() + "\n";
                        String[] info = analis(text);
                        listOfJSON.add(new Article(href, title, text, info[0], info[1]));

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listOfJSON;
    }

    private String[] analis(String text) {
        String[] res = new String[2];
        String[] txt = text.split("\\.");

        //System.out.println(txt.length);
        for (int i = 0; i < txt.length; i++) {
            //System.out.println(txt[i]);
            String[] result = new String[2];
            result[0] = findPattern(txt[i].toLowerCase(), EVENT);
            if(result[0] != null){
                System.out.println("CONTAINS!!!!");
                System.out.println(txt[i]);
                res[0] = result[0];
                result[1] = findPattern(txt[i], DATE);
                System.out.println("res1 = " + result[1]);
                if(result[1] != null) {
                    res = result;
                    break;
                }

            }

        }
        return res;
    }

    private static String findPattern(String txt, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(txt);
        if (m.find()) {
            return m.group();
        }else {
            return null;
        }
    }
}
