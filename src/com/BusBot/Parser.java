package com.BusBot;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    public static Document GetPage(String Transport, String Number) throws IOException {
        Document d = Jsoup.connect("http://kpt.kiev.ua/information/passengers/timetable/"+ Transport +"-"+ Number +".html").get();
        return d;
    }

    public static String GetTitle(Document d){
        return d.title();
    }

    public static List<String> GetDirection(Document d){
        List<String> data = new ArrayList<>();
        data.add(d.getElementsByClass("direction").get(0).text());
        data.add(d.getElementsByClass("direction").get(1).text());
        return data;
    }

    public static List<String> GetTimesAB(Document d){
        List<String> data = new ArrayList<>();
        Elements tableHeaders = d.getElementsByClass("timetableHeader");
        Elements pointsAB = tableHeaders.get(0).getElementsByClass("t-header");

        Elements forwardTRow = d.getElementsByClass("forward").get(0).getElementsByClass("t-row");
        for (int i = 1; i < pointsAB.size(); i++) {
            data.add("\n<b>" + pointsAB.get(i).text() + "</b>");
            for (int i1 = 1; i1 < forwardTRow.size(); i1++) {
                data.add(forwardTRow.get(i1).select("[class*='tt-']").get(i).text());
                //System.out.println(forwardTRow.get(i1).select("[class*='tt-']").get(i).text());
            }
        }
        return data;
    }

    public static List<String> GetTimesBA(Document d){
        List<String> data = new ArrayList<>();
        Elements tableHeaders = d.getElementsByClass("timetableHeader");
        Elements pointsAB = tableHeaders.get(1).getElementsByClass("t-header");

        Elements forwardTRow = d.getElementsByClass("back").get(0).getElementsByClass("t-row");
        for (int i = 1; i < pointsAB.size(); i++) {
            data.add("\n<b>" + pointsAB.get(i).text() + "</b>");
            for (int i1 = 1; i1 < forwardTRow.size(); i1++) {
                data.add(forwardTRow.get(i1).select("[class*='tt-']").get(i).text());

            }
        }

        return data;
    }

}
