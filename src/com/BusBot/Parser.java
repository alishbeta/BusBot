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
    public static List<String> GetPage(String Transport, String Number) throws IOException {
        Document d = Jsoup.connect("http://kpt.kiev.ua/information/passengers/timetable/"+ Transport +"-"+ Number +".html").get();
        List<String> data = new ArrayList<>();
        data.add(d.title());
        Elements tableHeaders = d.getElementsByClass("timetableHeader");
        Elements pointsAB = tableHeaders.get(0).getElementsByClass("t-header");
        Elements pointsBA = tableHeaders.get(1).getElementsByClass("t-header");

        Elements forwardTRow = d.getElementsByClass("forward").get(0).getElementsByClass("t-row");
        for (int i = 1; i < pointsAB.size(); i++) {
            data.add("\n<b>" + pointsAB.get(i).text() + "</b>");
            System.out.println(pointsAB.get(i).text());
            for (int i1 = 1; i1 < forwardTRow.size(); i1++) {
                data.add(forwardTRow.get(i1).getElementsByClass("tt-f").get(i).text());
                System.out.println(forwardTRow.get(i1).getElementsByClass("tt-f").get(i).text());
            }
        }
        return data;
    }
}
