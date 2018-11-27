package ie.tcd.irws.searchengine;

import org.apache.lucene.queryparser.flexible.core.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Utils {

    // path specifying the file containing terms to filter out during relevant/irrelevant term identification
    final static String RELEVANT_TERMS_FILTER_PATH = "searchengine/relevant-stop.txt";
    final static String STOP_WORD_PATH = "searchengine/cranfield/stop-word-list.txt";


    /**
     * Method for extracting phrases from the given string
     * @param text to delimit into phrases
     * @return ArrayList of phrase Strings (special chars removed! (e.g '?', '!', "'"))
     */
    public static ArrayList<ArrayList<String>> getPhrases(String text) {
        ArrayList<ArrayList<String>> ret = new ArrayList<>();
        String[] terms = text.trim().replaceAll("[^A-Za-z0-9 ]", "").split("\\s+");
        for(int i = 0; i < terms.length-1; i++) {
            ArrayList<String> temp = new ArrayList<>();
            temp.add(terms[i]);
            temp.add(terms[i+1]);
            ret.add(temp);
        }
        return ret;
    }

    /**
     *  Method for finding irrelevant terms or terms that should not occur in the given string
     *  @param text to analyse
     *  @return list 0 - list of RELEVANT terms, these could likely be terms that MUST occur
     *          list 1 - list of IRRELEVANT terms, these terms MUST NOT occur
     *  @throws IOException
     */
    public static ArrayList<ArrayList<String>> getRelevantTerms(String text) throws IOException {
        ArrayList<String> relevantTerms = new ArrayList<>();
        ArrayList<String> irrelevantTerms = new ArrayList<>();

        // read list of terms to remove/filter
        List<String> stopWordsForIrrelevantTerms = Arrays.asList(readFile(RELEVANT_TERMS_FILTER_PATH).toLowerCase().split("\n"));

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

        boolean relevantSentence; // determines if the current sentence contains RELEVANT terms (as opposed to NOT relevant terms)
        boolean afterNegation; // e.g. 'unless' in not relevant sentence
        for(int i = 0; i < sentences.length; i++) {
            // for each sentence
            relevantSentence = true;
            afterNegation = false;

            String[] words = sentences[i].trim().split("\\s+");

            if(sentences[i].contains("not relevant") || sentences[i].contains("irrelevant")) {
                // the current sentence is defining terms that SHOULD NOT appear in the results
                relevantSentence = false;

                if(sentences[i].contains("unrelated") || countSubstring(sentences[i], " not ") == 2) {
                    // double negative, not occurs twice
                    relevantSentence = true;
                }
            }
            for(int j = 0; j < words.length; j++) {
                if(!stopWordsForIrrelevantTerms.contains(words[j])) {
                    // the current word is not in the list of terms to filter out
                    if(relevantSentence || afterNegation) {
                        // add term to relevant terms
                        if(!relevantTerms.contains(words[j]) && !words[j].equals("")) {
                            // avoid duplicate and empty entries
                            relevantTerms.add(words[j]);
                        }
                    }
                    else {
                        if(words[j].equals("unless")) {
                            // terms appearing after 'unless' SHOULD be included in results
                            afterNegation = true;
                            continue;
                        }
                        else {
                            if(!irrelevantTerms.contains(words[j]) && !words[j].equals("")) {
                                // avoid duplicate and empty entries
                                irrelevantTerms.add(words[j]);
                            }
                        }
                    }
                }
            }
        }

        // if a term appears in BOTH relevantTerms and irrelevantTerms, remove from irrelevantTerms
        List<String> common = new ArrayList<String>(relevantTerms);
        common.retainAll(irrelevantTerms);
        for(int i = 0; i < common.size(); i++) {
            irrelevantTerms.remove(common.get(i));
        }

        // return the lists
        ArrayList<ArrayList<String>> ret = new ArrayList<>();
        ret.add(relevantTerms);
        ret.add(irrelevantTerms);
        return ret;
    }


    /**
     *  Method for returning a list of terms with stop words removed from a String
     * @param text to analyse
     * @return list of terms
     */
    public static ArrayList<String> getTerms(String text) throws IOException {
        List<String> stopWords = Arrays.asList(readFile(STOP_WORD_PATH).toLowerCase().split("\r\n"));
        text = text.toLowerCase();
        String[] words = text.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
        ArrayList<String> terms = new ArrayList<>();

        for(String word : words){
            if(!stopWords.contains(word)) {
                terms.add(word);
            }
        }

        return terms;
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

    public static String pickUniqueTerms(ArrayList<String> terms, ArrayList<Double> idfs, int termLimt){
        while(terms.size() > termLimt)
        {
            int minIndex = idfs.indexOf(Collections.min(idfs));
            idfs.remove(minIndex);
            terms.remove(minIndex);
        }

        return String.join(" ", terms);

    }



    private static int countSubstring(String str, String substr) {
        int lastIndex = 0;
        int count = 0;
        while(lastIndex != -1){
            lastIndex = str.indexOf(substr,lastIndex);
            if(lastIndex != -1){
                count ++;
                lastIndex += substr.length();
            }
        }
        return count;
    }
}
