/**
 * Написать код парсинга страницы Википедии “Список станций Московского метрополитена”,
 * который будет на основе этой страницы создавать JSON-файл со списком станций по линиям и списком линий
 * по формату JSON-файла из проекта SPBMetro (файл map.json, приложен к домашнему заданию)
 *
 * * Также пропарсить и вывести в JSON-файл переходы между станциями.
 *
 * Написать код, который прочитает созданный JSON-файл и напечатает количества станций на каждой линии.
 */


import com.google.gson.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main


{

    private static JsonArray ar;
    private static JsonObject obj;

    private static PrintWriter textFile;
    private static Writer writer;
    private static Gson gson;

    private static String jsonFile = "src/data/moscowMap.json";


    public static void main(String[] args) {

        ar = new JsonArray();
        obj = new JsonObject();



//      String url = "https://ru.wikipedia.org/wiki/%D0%A1%D0%BF%D0%B8%D1%81%D0%BE%D0%BA_%D1%81%D1%82%D0%B0%D0%BD%D1%86%D0%B8%D0%B9" +
//                "_%D0%9C%D0%BE%D1%81%D0%BA%D0%BE%D0%B2%D1%81%D0%BA%D0%BE%D0%B3%D0%BE" +
//                "_%D0%BC%D0%B5%D1%82%D1%80%D0%BE%D0%BF%D0%BE%D0%BB%D0%B8%D1%82%D0%B5%D0%BD%D0%B0";

        try {
            File input = new File("src/data/metro.html");
            Document doc1 = Jsoup.parse(input, "UTF-8");
            String textFileName = "moscowMap.txt";
            textFile = new PrintWriter("src/data/" + textFileName);
            writer = Files.newBufferedWriter(Paths.get(jsonFile));
            gson = new GsonBuilder().setPrettyPrinting().create();
            Elements object = doc1.select("table[class=standard sortable] > tbody > tr:has(td[data-sort-value])");

            stationParseTxt(object);

            textFile.flush();
            textFile.close();

            gson.toJson(stationParse(), writer);
            writer.close();

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        createStationIndex();


    }

    private static void stationParseTxt(Elements object) throws IOException {



        for (Element elements:object) {
            String line, nameLine, station, connections;
            line = elements.select("td:eq(0)").select("span:eq(0)").text();
            nameLine = elements.select("td:eq(0)").select("span:eq(1)").attr("title");
            station = elements.select("td:eq(1)").select("span:eq(0)").text();
            if(station.length() == 0){
                station = elements.select("td:eq(1)").select("a").text();
            }
            connections = connectionsParseTxt(elements);

            textFile.write("station" + "/" + line + "/" + nameLine + "/" + station + "/" + connections + "\n");
            line = elements.select("td:eq(0)").select("span[class=sortkey]:eq(3)").text();
            nameLine = elements.select("td:eq(0)").select("span:eq(4)").attr("title");
            if (line.length() > 0) {
                textFile.write("station" + "/" + line + "/" + nameLine + "/" + station + "/" + connections + "\n");
            }
        }


    }

    private static HashMap stationParse() throws IOException {
        LinkedHashMap<String, Line> line = new LinkedHashMap<>();
        LinkedHashMap<String, List<String>> stationList = new LinkedHashMap<>();
        ArrayList<Object> lineList = new ArrayList<>();
        HashMap<String, Object> file = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        List<String> lines = Files.readAllLines(Paths.get("src/data/moscowMap.txt"));
        lines.forEach(l -> builder.append(l + "\n"));

        line.putAll(textToLine(lines));
        line.forEach((k, v) -> {
            stationList.put(k, v.getStationName());
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", v.getNameLine());
            map.put("number", v.getNumberLine());
            lineList.add(map);
        });

        Connections connections = new Connections();
        connections.setStations(lines, stationList);

        file.put("stations", stationList);
        file.put("lines", lineList);
        file.put("connections", connections.getStations());

        return file;

    }


    private static String connectionsParseTxt(Element elements){
        String connName1, connNumber1, connName2, connNumber2, connName3, connNumber3, connection;

        connName1 = elements.select("td:eq(3)").select("span:eq(1)").attr("title");
        connNumber1 = elements.select("td:eq(3)").select("span:eq(0)").text();
        connName2 = elements.select("td:eq(3)").select("span:eq(3)").attr("title");
        connNumber2 = elements.select("td:eq(3)").select("span:eq(2)").text();
        connName3 = elements.select("td:eq(3)").select("span:eq(5)").attr("title");
        connNumber3 = elements.select("td:eq(3)").select("span:eq(4)").text();
        connection = connName1 + "/" + connNumber1 + "/" + connName2 + "/" + connNumber2 + "/" + connName3 + "/" + connNumber3;
        return connection;
    }

    private static HashMap textToLine(List<String> lines){
        LinkedHashMap<String, Line> line = new LinkedHashMap<>();

        for (String l:lines) {
            String[] text = l.split("/");
            if (!line.containsKey(text[1])){
                String numberLine = text[1];
                line.put(numberLine, new Line(numberLine));
                line.get(text[1]).setNameLine(text[2]);
            }
            line.get(text[1]).setStationName(text[3]);
        }
        return line;
    }

    private static void createStationIndex()
    {

        try
        {

            Gson gson = new Gson();

            Reader reader = Files.newBufferedReader(Paths.get(jsonFile));
            JsonObject parser = JsonParser.parseReader(reader).getAsJsonObject();

            Map<?, ArrayList> map = gson.fromJson(parser.get("stations").getAsJsonObject(), Map.class);

            List<Map> list = Arrays.asList(gson.fromJson(parser.get("lines").getAsJsonArray(), Map[].class));

            // Первый вариант
            map.forEach((k, v) -> System.out.println("на " + k + " линии - " + v.size() + " станции(й)"));
            System.out.println("\n\n");
            // Второй вариант
            list.forEach(l -> System.out.println("на " + l.get("number") + " " + l.get("name") + " - " + map.get(l.get("number")).size() + " станции(й)" ));

            reader.close();

        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }


}
