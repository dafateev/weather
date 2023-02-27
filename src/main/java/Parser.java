import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private static Document getPage() throws IOException {
        String url = "https://www.pogoda.msk.ru/";
        Document page = Jsoup.parse(new URL(url), 3000);
        return page;
    }

    //27.02 Понедельник погода сегодня -> Only Date allowed -> 27/02
    // \d{2}\.\d{2}
    private static Pattern pattern = Pattern.compile("\\d{2}\\.\\d{2}");

    private static String getDateFromString(String stringData) throws Exception {
        Matcher matcher = pattern.matcher((stringData));
        if (matcher.find()) {
            return matcher.group();
        }
        throw new Exception("Can't extract date from string!");
    }

    private static int printPartValues(Elements values, int index) {
        int iterationCount = 4;
        if (index == 0) {
            Element valueLn = values.get(3);
            boolean isNight = valueLn.text().contains("Ночь");
            boolean isMorning = valueLn.text().contains("Утро");
            boolean isDay = valueLn.text().contains("День");
            boolean isEvening = valueLn.text().contains("Вечер");
            if (isNight) {
                iterationCount = 0;
            }
            if (isMorning) {
                iterationCount = 3;
            }
            if (isDay) {
                iterationCount = 2;
            }
            if (isEvening) {
                iterationCount = 1;
            }
        }

        for (int i = 0; i < iterationCount; i++) {
            Element valueLine = values.get(index + i);
            for (Element td : valueLine.select("td")) {
                System.out.print(td.text() + "    ");
            }
            System.out.println();
        }
        System.out.println();
        return iterationCount;
    }

    public static void main(String[] args) throws Exception {
        Document page = getPage();
        //css query language
        Element tableWTH = page.select("table[class=wt]").first();
        Elements names = tableWTH.select("tr[class=wth]");
        Elements values = tableWTH.select("tr[valign=top]");
//        System.out.println(tableWTH); //full page

        int index = 0;

        for (Element name : names) {
            String dateString = name.select("th[id=dt]").text();
            String date = getDateFromString(dateString);
            System.out.println(date + "    Явления    Температура    Давление    Влажность    Ветер");
            int iterationCount = printPartValues(values, index);
            index = index + iterationCount;
        }
    }
}
