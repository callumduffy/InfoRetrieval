package ie.tcd.irws.searchengine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {

    // path specifying the file containing terms to filter out during relevant/irrelevant term identification
    final static String RELEVANT_TERMS_FILTER_PATH = "relevant-stop.txt";


    /**
     * Method for extracting phrases from the given string
     * @param text to delimit into phrases
     * @return ArrayList of phrase Strings (special chars removed! (e.g '?', '!', "'"))
     */
    public static ArrayList<ArrayList<String>> getPhrases(String text, Integer phraseLength) {
        ArrayList<ArrayList<String>> ret = new ArrayList<>();
        String[] terms = text.trim().replaceAll("[^A-Za-z0-9 ]", "").split("\\s+");
        for(int i = 0; i < terms.length-(phraseLength - 1); i++) {
            ArrayList<String> temp = new ArrayList<>();
            for(int j = 0; j < phraseLength; j++){
                temp.add(terms[i+j]);
            }
            
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

        // replace anachronyms (avoids sentence splitting on '.')
        text = text.replace("U.S.", "United States");

        // lower case the text
        text = text.toLowerCase();

        // remove special chars
        text = text.replace("\"", "");
        text = text.replace("\n", " ");
        text = text.replace(",", " ");
        text = text.replace("(", "").replace(")", "");
        text = text.replace(":", " ");

        // some weird uses of '-' in the narrs, this is just some clean up
        text = text.replaceAll("-{2,}", " "); // replace multiple '-''s with one
        text = text.replace(" -", "-").replace("- ", "-"); // sometimes hypenated words get seperated (especially at '\n')


        // remove special words
        text = text.replace("i.e.", " ");
        text = text.replace("e.g.", " ");
        text = text.replace("etc.", " ");

        // fix 401
        text = text.replace(";", ". ");

        // fix alternative words e.g. damage/casualties
        text = text.replace("/", " ");

        // replace 'but' as a sentence break
        text = text.replace(" but ", ". ");


        // split given text into sentences
        String[] sentences = text.split("\\.");

        boolean relevantSentence; // determines if the current sentence contains RELEVANT terms (as opposed to NOT relevant terms)
        for(int i = 0; i < sentences.length; i++) {
            // for each sentence
            relevantSentence = true;

            String[] words = sentences[i].trim().split("\\s+");

            if(sentences[i].contains("not relevant") || sentences[i].contains("irrelevant")) {
                // the current sentence is defining terms that SHOULD NOT appear in the results
                relevantSentence = false;

                if(sentences[i].contains("unrelated") || sentences[i].contains(" without ") || countSubstring(sentences[i], " not ") == 2) {
                    // double negative
                    relevantSentence = true;
                }
            }

            for(int j = 0; j < words.length; j++) {

                if(words[j].equals("unless")) {
                    // terms appearing after 'unless' SHOULD be included in results
                    relevantSentence = !relevantSentence;
                    continue;
                }

                if(!stopWordsForIrrelevantTerms.contains(words[j])) {
                    // the current word is not in the list of terms to filter out
                    if(!relevantTerms.contains(words[j]) && !words[j].equals("")) {

                        if(relevantSentence) {
                            // add term to relevant terms
                            relevantTerms.add(words[j]);
                        }
                        else if(!irrelevantTerms.contains(words[j])) {
                            irrelevantTerms.add(words[j]);
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
     *  Method for reading a file into a string
     *  @param filePath
     *  @return Contents of file as String
     *  @throws IOException
     */
    private static String readFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
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
