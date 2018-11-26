package ie.tcd.irws.searchengine;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.ArrayList;

public class RelevantTermsTest extends TestCase {

    /**
     * getIrrelevantTerms TESTS
     */
    public ArrayList<ArrayList<String>> getRelevantTermsHelper(String narr) throws IOException {
        ArrayList<ArrayList<String>> result = Utils.getRelevantTerms(narr);
        System.out.println();
        System.out.println("NARR:\n" + narr);
        System.out.println("RELEVANT TERMS: \n" + result.get(0).toString());
        System.out.println("IRRELEVANT TERMS: \n" + result.get(1).toString());
        return result;
    }



    public void testGetRelevantTerms401() throws IOException {
        String narr = "A relevant document will focus on the causes of the lack of\n" +
                "integration in a significant way; that is, the mere mention of\n" +
                "immigration difficulties is not relevant.  Documents that discuss\n" +
                "immigration problems unrelated to Germany are also not relevant.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        // The term "Germany" MUST occur
        assertTrue(result.get(0).toString().equals("[causes, lack, integration, immigration, problems, germany]"));
        assertTrue(result.get(1).toString().equals("[difficulties]"));
    }

    public void testGetRelevantTerms402() throws IOException {
        String narr = "Documents describing genetic or environmental factors relating\n" +
                "to understanding and preventing substance abuse and addictions\n" +
                "are relevant.  Documents pertaining to attention deficit disorders\n" +
                "tied in with genetics are also relevant, as are genetic disorders \n" +
                "affecting hearing or muscles.  The genome project is relevant\n" +
                "when tied in with behavior disorders (i.e., mood disorders,\n" +
                "Alzheimer's disease). ";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[genetic, environmental, factors, understanding, preventing, substance, abuse, addictions, pertaining, attention, deficit, disorders, tied, genetics, affecting, hearing, muscles, genome, project, behavior, mood, alzheimer's, disease]"));
        assertTrue(result.get(1).toString().equals("[]"));
    }

    public void testGetRelevantTerms403() throws IOException {
        String narr = "A relevant document may include one or more of the\n" +
                "dietary intakes in the prevention of osteoporosis.\n" +
                "Any discussion of the disturbance of nutrition and\n" +
                "mineral metabolism that results in a decrease in \n" +
                "bone mass is also relevant.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[dietary, intakes, prevention, osteoporosis, discussion, disturbance, nutrition, mineral, metabolism, results, decrease, bone, mass]"));
        assertTrue(result.get(1).toString().equals("[]"));
    }

    public void testGetRelevantTerms404() throws IOException {
        String narr = "Any interruptions to the peace process not directly\n" +
                "attributable to acts of violence are not relevant.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[interruptions, peace, process, directly, attributable, acts, violence]"));
        assertTrue(result.get(1).toString().equals("[]"));
    }

    // A lost cause..
    public void testGetRelevantTerms407() throws IOException {
        String narr = "A relevant document must discuss poaching in wildlife\n" +
                "preserves, not in the wild itself.  Also deemed relevant\n" +
                "is evidence of preventive measures being taken by local\n" +
                "authorities.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[discuss, poaching, wildlife, preserves, evidence, preventive, measures, local, authorities]"));
        assertTrue(result.get(1).toString().equals("[wild]"));
    }

    // RELEVANT TERMS ARE CORRECT
    public void testGetRelevantTerms408() throws IOException {
        String narr = "The date of the storm, the area affected, and the extent of \n" +
                "damage/casualties are all of interest.  Documents that describe\n" +
                "the damage caused by a tropical storm as \"slight\", \"limited\", or\n" +
                "\"small\" are not relevant. ";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[date, storm, area, affected, extent, damage, casualties, damage, caused, tropical, storm]"));
        assertTrue(result.get(1).toString().equals("[slight, limited, small]"));
    }

    public void testGetRelevantTerms409() throws IOException {
        String narr = "Documents describing any charges, claims, or fines \n" +
                "presented to or imposed by any court or tribunal are\n" +
                "relevant, but documents that discuss charges made in\n" +
                "diplomatic jousting are not relevant.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[charges, claims, fines, presented, imposed, court, tribunal]"));
        assertTrue(result.get(1).toString().equals("[made, diplomatic, jousting]")); // charges already occurs as a relevant term
    }

    public void testGetRelevantTerms410() throws IOException {
        String narr = "Relevant documents will contain any information about the\n" +
                "actions of signatories of the Schengen agreement such as:\n" +
                "measures to eliminate border controls (removal of traffic\n" +
                "obstacles, lifting of traffic restrictions); implementation\n" +
                "of the information system data bank that contains unified\n" +
                "visa issuance procedures; or strengthening of border controls\n" +
                "at the external borders of the treaty area in exchange for \n" +
                "free movement at the internal borders.  Discussions of border \n" +
                "crossovers for business purposes are not relevant.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[information, about, actions, signatories, schengen, agreement, measures, eliminate, border, controls, removal, traffic, obstacles, lifting, restrictions, implementation, system, data, bank, contains, unified, visa, issuance, procedures, strengthening, external, borders, treaty, area, exchange, free, movement, internal]"));
        assertTrue(result.get(1).toString().equals("[discussions, crossovers, business, purposes]"));
    }

    public void testGetRelevantTerms414() throws IOException {
        String narr = "A relevant document will provide information\n" +
                "regarding Cuba's sugar trade.  Sugar production\n" +
                "statistics are not relevant unless exports\n" +
                "are mentioned explicitly.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[information, regarding, cuba's, sugar, trade, exports, mentioned]"));
        assertTrue(result.get(1).toString().equals("[production, statistics]"));
    }

    public void testGetRelevantTerms418() throws IOException {
        String narr = "Documents mentioning quilting books, quilting classes,\n" +
                "quilted objects, and museum exhibits of quilts are all relevant.  \n" +
                "Documents that discuss AIDs quilts are irrelevant, unless \n" +
                "there is specific mention that the quilts are being used \n" +
                "for fundraising.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[quilting, books, classes, quilted, objects, museum, exhibits, quilts, fundraising]"));
        assertTrue(result.get(1).toString().equals("[aids]"));
    }

    public void testGetRelevantTerms420() throws IOException {
        String narr = "Relevant documents will contain data on\n" +
                "what carbon monoxide poisoning is, symptoms,\n" +
                "causes, and/or prevention.  Advertisements for\n" +
                "carbon monoxide protection products or services \n" +
                "are not relevant.  Discussions of auto emissions\n" +
                "and air pollution are not relevant even though\n" +
                "they can contain carbon monoxide.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[data, carbon, monoxide, poisoning, symptoms, causes, prevention]"));
        assertTrue(result.get(1).toString().equals("[advertisements, protection, products, services, discussions, auto, emissions, air, pollution]"));
    }

    public void testGetRelevantTerms421() throws IOException {
        String narr = "Documents that discuss the disposal, storage, or management\n" +
                "of industrial waste---both standard and hazardous---are\n" +
                "relevant.  However, documents that discuss disposal or\n" +
                "storage of nuclear or radioactive waste, or the illegal\n" +
                "shipment or dumping of waste to avoid legal disposal\n" +
                "methods are not relevant.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[disposal, storage, management, industrial, waste, standard, hazardous]"));
        assertTrue(result.get(1).toString().equals("[nuclear, radioactive, illegal, shipment, dumping, avoid, legal, methods]"));
    }

    // I don't even know what this means tbh... (ADDED 'decorative' TO NOT RELEVANT, DONT EVEN KNOW IF IT SHOULD BE THERE OR NOT)..
    public void testGetRelevantTerms422() throws IOException {
        String narr = "Instances of stolen or forged art in any media are relevant. \n" +
                "Stolen mass-produced things, even though they might be \n" +
                "decorative, are not relevant (unless they are mass-\n" +
                "produced art reproductions). Pirated software, music, movies, \n" +
                "etc. are not relevant.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[instances, stolen, forged, art, media, mass-produced, reproductions]"));
        assertTrue(result.get(1).toString().equals("[decorative, pirated, software, music, movies]"));
    }

    // Lost cause..
    public void testGetRelevantTerms423() throws IOException {
        String narr = "Any mention of the Serbian president's wife is\n" +
                "relevant, even if she is not named.  She may be referred to\n" +
                "by her nickname, Mira.  A general mention of his family, \n" +
                "without specifying his wife, is not relevant.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[serbian, president's, wife, nickname, mira]"));
        assertTrue(result.get(1).toString().equals("[..]"));
    }

    public void testGetRelevantTerms424() throws IOException {
        String narr = "The intent of this query is to find criminal murders that \n" +
                "are being disguised as suicide, but assisted suicides done\n" +
                "out of compassion would be relevant if someone refers to \n" +
                "them as murder.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[criminal, murders, disguised, suicide, assisted, suicides, compassion, murder]"));
        assertTrue(result.get(1).toString().equals("[]"));
    }

    public void testGetRelevantTerms425() throws IOException {
        String narr = "Relevant documents must cite actual instances\n" +
                "of counterfeiting.   Anti-counterfeiting measures by\n" +
                "themselves are not relevant.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[actual, instances, counterfeiting]"));
        assertTrue(result.get(1).toString().equals("[anti-counterfeiting, measures]"));
    }

    public void testGetRelevantTerms426() throws IOException {
        String narr = "Relevant items include specific information on the\n" +
                "use of dogs during an operation.  Training of dogs\n" +
                "and their handlers are also relevant.  ";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[information, use, dogs, during, operation, training, handlers]"));
        assertTrue(result.get(1).toString().equals("[]"));
    }

    // "U.S." -> "United States"
    // Another lost cause..
    public void testGetRelevantTerms428() throws IOException {
        String narr = "To be relevant, a document will name a country other than the U.S. \n" +
                "or China in which the birth rate fell from the rate of the\n" +
                "previous year.  The decline need not have occurred in more\n" +
                "than the one preceding year.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[name, country, birth, rates, fell, previous, year]"));
        assertTrue(result.get(1).toString().equals("[united, states, china]"));
    }

    public void testGetRelevantTerms429() throws IOException {
        String narr = "To be relevant, a document must discuss a specific outbreak\n" +
                "of Legionnaires' disease.  Documents that address prevention of\n" +
                "or cures for the disease without citing a specific case are \n" +
                "not relevant.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[outbreak, legionnaires', disease, address, prevention, cures, case]"));
        assertTrue(result.get(1).toString().equals("[]"));
    }


    public void testGetRelevantTerms430() throws IOException {
        String narr = "Relevant documents must cite a specific instance of a human\n" +
                "attacked by killer bees.  Documents that note migration patterns\n" +
                "or report attacks on other animals are not relevant unless they\n" +
                "also cite an attack on a human.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[human, attacked, killer, bees, attack]"));
        assertTrue(result.get(1).toString().equals("[note, migration, patterns, report, attacks, other, animals]"));
    }

    public void testGetRelevantTerms431() throws IOException {
        String narr = "A relevant document will contain information on \n" +
                "current applications of robotic technology.  Discussions\n" +
                "of robotics research or simulations of robots are\n" +
                "not relevant.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[information, current, applications, robotic, technology]"));
        assertTrue(result.get(1).toString().equals("[discussions, robotics, research, simulations, robots]"));
    }

    public void testGetRelevantTerms435() throws IOException {
        String narr = "A relevant document must describe an actual case in which \n" +
                "population measures have been taken and their results are known.\n" +
                "The reduction measures must have been actively pursued;\n" +
                "that is, passive events such as disease or famine\n" +
                "involuntarily reducing the population are not relevant.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[actual, case, population, measures, taken, results, known, reduction, actively, pursued]"));
        assertTrue(result.get(1).toString().equals("[passive, events, disease, famine, involuntarily, reducing]"));
    }

    public void testGetRelevantTerms436() throws IOException {
        String narr = "A relevant document provides data on railway accidents\n" +
                "of any sort (i.e., locomotive, trolley, streetcar) where\n" +
                "either the railroad system or the vehicle or pedestrian\n" +
                "involved caused the accident.  Documents that discuss\n" +
                "railroading in general, new rail lines, new technology\n" +
                "for safety, and safety and accident prevention \n" +
                "are not relevant, unless an actual accident is described. ";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[provides, data, railway, accidents, sort, locomotive, trolley, streetcar, railroad, system, vehicle, pedestrian, involved, caused, accident, actual]"));
        assertTrue(result.get(1).toString().equals("[railroading, general, new, rail, lines, technology, safety, prevention]"));
    }

    public void testGetRelevantTerms438() throws IOException {
        String narr = "A relevant document will name a country that\n" +
                "has experienced an increase in tourism.\n" +
                "The increase must represent the nation as a whole\n" +
                "and tourism in general, not be restricted to only\n" +
                "certain regions of the country or to some specific\n" +
                "type of tourism (e.g., adventure travel).  Documents\n" +
                "discussing only projected increases are not relevant.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[country, experienced, increase, tourism, represent, nation, whole, general, certain, regions, some, adventure, travel]"));
        // certain, regions, some, adventure, travel are still relevant terms (not ONLY these terms)
        assertTrue(result.get(1).toString().equals("[projected, increases]"));
    }

    public void testGetRelevantTerms442() throws IOException {
        String narr = "Relevant documents will contain a description of specific\n" +
                "acts.  General statements concerning heroic acts are not\n" +
                "relevant.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[description, acts]"));
        assertTrue(result.get(1).toString().equals("[general, statements, concerning, heroic]"));
    }

    public void testGetRelevantTerms449() throws IOException {
        String narr = "To be relevant, a document must discuss the reasons or \n" +
                "causes for the ineffectiveness of current antibiotics.\n" +
                "Relevant documents may also include efforts by pharmaceutical\n" +
                "companies and federal government agencies to find new cures, \n" +
                "updating current testing phases, new drugs being tested,\n" +
                "and the prognosis for the availability of new and effective\n" +
                "antibiotics. ";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[reasons, causes, ineffectiveness, current, antibiotics, efforts, pharmaceutical, companies, federal, government, agencies, new, cures, updating, testing, phases, drugs, tested, prognosis, availability, effective]"));
        assertTrue(result.get(1).toString().equals("[]"));
    }
}
