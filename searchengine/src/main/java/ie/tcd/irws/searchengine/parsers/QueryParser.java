package ie.tcd.irws.searchengine.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ie.tcd.irws.searchengine.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class QueryParser {

    private String filepath;

    public QueryParser(String filepath){
        this.filepath = filepath;
    }

    public List<HashMap<String, String>> loadTopics() throws IOException {
        File file = new File(filepath);
        org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(file, "UTF-8");
        Element body = jsoupDoc.body();
        ArrayList<HashMap<String, String>> topics = new ArrayList<>();
        Elements tops = body.children();
        for(Element top : tops){
            HashMap<String, String> topic = new HashMap<>();
            Element num = top.child(0);
            Element title = num.child(0);
            Element desc = num.child(1);
            Element narr = desc.child(0);

            String numText = num.ownText();
            String titleText = title.ownText();
            String descText = desc.ownText();
            String narrText = narr.ownText();

            numText = numText.replace("Number: ", "");
            descText = descText.replace("Description: ", "");
            narrText = narrText.replace("Narrative: ", "");
            ArrayList<ArrayList<String>> relUtilResult = Utils.getRelevantTerms(narrText);
            String relText = stringArrayToString(relUtilResult.get(0));
            String nRelText = stringArrayToString(relUtilResult.get(1));

            topic.put("num", numText);
            topic.put("title", titleText);
            topic.put("desc", descText);
            topic.put("narr", narrText);
            topic.put("rel", relText);
            topic.put("nRel", nRelText);
            topics.add(topic);
        }
        return topics;
    }

    private String stringArrayToString(ArrayList<String> input){
        String result="";
        for(String string:input)
            result +=  " " + string; 
        return result;
    }

}
