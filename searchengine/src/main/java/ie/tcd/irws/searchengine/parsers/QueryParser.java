package ie.tcd.irws.searchengine.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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


            topic.put("num", numText);
            topic.put("title", titleText);
            topic.put("desc", descText);
            topic.put("narr", narrText);
            topics.add(topic);
        }
        return topics;
    }



    /**
     *  Method for finding irrelevant terms or terms that should not occur in the given string
     *  @param text to analyse
     *  @return String array of terms that should not be included based on the given text
     *  @throws IOException
     */
    public static ArrayList<ArrayList<String>> getRelevantTerms(String text) throws IOException {
        ArrayList<String> relevantTerms = new ArrayList<>();
        ArrayList<String> irrelevantTerms = new ArrayList<>();

        // read list of terms to remove/filter
        List<String> stopWordsForIrrelevantTerms = Arrays.asList(readFile("irrelevant-stop.txt").toLowerCase().split("\n"));

        // lower case the text
        text = text.toLowerCase().replace(",", " ");

        // remove special chars
        text = text.replace("(", "").replace(")", "");

        // remove special words
        text = text.replace("i.e.", "");

        // fix 401
        text = text.replace(";", ". ");

        // split given text into sentences
        String[] sentences = text.split("\\.");

        boolean afterNegation = false; // e.g. 'unless'
        for(int i = 0; i < sentences.length; i++) {
            // for each sentence
            if(sentences[i].contains("not relevant") || sentences[i].contains("irrelevant")) {
                // the sentence is defining terms that are irrelevant
                afterNegation = false;
                String[] words = sentences[i].trim().split("\\s+");
                for(int j = 0; j < words.length; j++) {
                    // for each word in the sentence
                    if(words[j].equals("unless")) {
                        afterNegation = true;
                        continue;
                    }
                    if(!stopWordsForIrrelevantTerms.contains(words[j]) && !relevantTerms.contains(words[j])) {
                        // the current word is not in the list of terms to filter
                        if(afterNegation) {
                            relevantTerms.add(words[j]);
                        }
                        else {
                            irrelevantTerms.add(words[j]);
                        }
                    }
                }
            }
            else {
                String[] words = sentences[i].trim().split("\\s+");
                for(int j = 0; j < words.length; j++) {
                    // for each word in the sentence
                    if(!stopWordsForIrrelevantTerms.contains(words[j]) && !relevantTerms.contains(words[j])) {
                        relevantTerms.add(words[j]);
                    }
                }
            }
        }

        ArrayList<ArrayList<String>> ret = new ArrayList<>();
        ret.add(relevantTerms);
        ret.add(irrelevantTerms);
        return ret;
    }

    /**
     *  Method for reading a file into a string
     *  @param filePath
     *  @return Contents of file as String
     *  @throws IOException
     */
    private static String readFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
}
