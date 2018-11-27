package ie.tcd.irws.searchengine.parsers;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeFilter;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

//Parser going to be used for the Federal Register dataset
//General structure for file in this dataset:
/*
<DOC>
<DOCNO></DOCNO>
<TEXT>
Inside the text tags, there is the possibility for the following fields
   USDEPT          The name of the department within the federal government 
                   that released the document.
   AGENCY          The name of the government agency within the department.
   USBUREAU        The name of the government service or bureau contributing 
                   the document.
   DOCTITLE        The title text of the contribution.
   ADDRESS         The address(es) of the contributing agency.
   FURTHER         The text detailing availability of further information.
   SUMMARY         A precis of the text of the full document. This text is
                   contributed by the agency and appears before the main
                   body of the text.
   ACTION          The function of the article; the reason it is in the
                   federal register. Usually just a few words.
   SIGNER          The signatory of the document.
   SIGNJOB         The title of the signatory.
   SUPPLEM         The supplementary information; the bulk of the document
                   text.
   BILLING         The Federal Register billing code for that section.
   FRFILING        The document's filing details.
   DATE            The effective date and/or time applicable to the text.
   CFRNO           The relevant section of the United States Code.
   RINDOCK         The docket or RIN number of the entry.
   TABLE           The text appearing in a table, often numeric.
   FOOTNOTE        The text appearing as a footnote.
   FOOTCITE        The text appearing that labels a footnote, usually just a 
                   superscripted integer.
   FOOTNAME        The text labelling the footnote, corresponding to the 
                   citation.

</TEXT
</DOC>
*/

public class FRParser {
	
	private String[] filepaths;
	
	public FRParser(String[] filepaths){
		this.filepaths = filepaths;
	}
	
	/**
	 * Method to load content of all files into Document list, to be parsed with Analyzer
     * @param IndexWriter
	 * @return void 
	 * @throws IOException 
	 */
	public void loadDocs(IndexWriter iwriter) throws IOException{
        
		File file;
		Document luceneDoc;
        org.jsoup.nodes.Document jsoupDoc; 
        String docno;
		
		// Create a new field type which will store term vector information
		FieldType ft = new FieldType(TextField.TYPE_STORED);
		ft.setTokenized(true); //done as default
		ft.setStoreTermVectors(true);
		ft.setStoreTermVectorPositions(true);
		ft.setStoreTermVectorOffsets(true);
		ft.setStoreTermVectorPayloads(true);
        
		for(String path : filepaths){
            
			file = new File(path);
			jsoupDoc = Jsoup.parse(file, "UTF-8");
			
			Elements jsoupDocs = jsoupDoc.getElementsByTag("DOC");
            System.out.println("Indexing file: " +  path);


			for(Element docElement : jsoupDocs){
                docno = docElement.getElementsByTag("DOCNO").text();
                luceneDoc = new Document();
                luceneDoc.add(new Field("docno", docno, ft));

                Element text = docElement.getElementsByTag("TEXT").first();
                if (text == null) {
                    //If no text, don't commit doc
                    continue;
                }
                //Remove comments from doc
                removeComments(text);
                //If a date tag exists, add it as a field
                if(text.getElementsByTag("DATE").text() != null){
                    luceneDoc.add(new Field("date", docElement.getElementsByTag("DATE").text(), ft));
                }
                //Remove the info in tags that contain relatively irrelevant info
                String[] bad_tags =new String[] {"BILLING", "FRFILING", "CFRNO", "RINDOCK", "DATE", "TABLE"};
                for(String tag : bad_tags){
                    for( Element element : text.getElementsByTag(tag) )
                    {
                        element.remove();
                    }
                }
                luceneDoc.add(new Field("title", text.getElementsByTag("DOCTITLE").text(), ft));
                luceneDoc.add(new Field("text", text.text(), ft));
                iwriter.addDocument(luceneDoc);

                
            }
            
        }
        
		
		
    }
    private void removeComments(Element article) {
        article.filter(new NodeFilter() {
            @Override
            public FilterResult tail(Node node, int depth) {
                if (node instanceof Comment) {
                    return FilterResult.REMOVE;
                }
                return FilterResult.CONTINUE;
            }
    
            @Override
            public FilterResult head(Node node, int depth) {
                if (node instanceof Comment) {
                    return FilterResult.REMOVE;
                }
                return FilterResult.CONTINUE;
            }
        });
    }
	
}
