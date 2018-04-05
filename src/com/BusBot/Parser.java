package com.BusBot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Parser {
    public static void GetPage() throws IOException {
       Document document = Jsoup.connect("http://kpt.kiev.ua/information/passengers/timetable/").get();
       System.out.println(document.attr("href", "bus-2.html"));
    }
}
