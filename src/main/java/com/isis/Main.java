package com.isis;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.BuildBinarizedDataset;
import edu.stanford.nlp.sentiment.SentimentUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;


public class Main {

    public static void main(String[] args) {


        /*Migrate database*/
//        Migrate();

        //SentimentAnalyzer s = new SentimentAnalyzer();

        //int sentiment = s.findSentiment("awesome");

        //System.out.println(sentiment);
//        GenerateTreeRepresentation();

        KagglerController controller = new KagglerController();
        controller.MigrateToTree();
        //controller.Migrate();
//        controller.MigrateToTree();
        //TestSentimentController controller =  new TestSentimentController("test.tsv", "test_salida.tsv", "model-dev-0006-76.55.ser.gz");
        //try {
          ///  controller.Process();

        //} catch (IOException e) {
          //  e.printStackTrace();
        //}
        System.out.println("print ok");
	// write your code here
    }
    public static void Train()
    {
     //   SentimentUtils.readTreesWithGoldLabels()

    }
    public static void GenerateTreeRepresentation()
    {
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream("train_output.txt"));
            System.setOut(out);
            String inputFile = "binarizedDataset_input.csv";
            String[] newargs = {"-input", inputFile};
            BuildBinarizedDataset.main(newargs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    ///
    public static void Migrate()
    {
        System.out.print("Processing. This will take about 30 mins or os...");

        // Input
        String dataFileName = "train.tsv";

        File dataFile = new File(dataFileName);

        try {
            FileReader fr = new FileReader(dataFile);
            BufferedReader bReader = new BufferedReader(fr);
            File fout = new File("binarizedDataset_input.csv");
            FileOutputStream fos = new FileOutputStream(fout);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            HashMap allPhrases = new HashMap();
            HashMap localPhrases = new HashMap();
            String line;
            // skip the first line.
            bReader.readLine();

            Integer currentSentenceId = 0;
            int countP = 0;
            boolean skip = false;
            String sentence = "";
            while ((line = bReader.readLine()) != null) {
                // Splitting the content of tabbed separated line
                // PhraseId	SentenceId	Phrase	Sentiment
                String datavalue[] = line.split("\t");
                String phraseId = datavalue[0];
                Integer sentenceId = Integer.parseInt(datavalue[1]);
                String phrase = datavalue[2];
                Integer sentiment = Integer.parseInt(datavalue[3]);
                if(!currentSentenceId.equals(sentenceId)){
                    // new sentence starting.
                    Iterator it = allPhrases.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pairs = (Map.Entry) it.next();

                        String key = pairs.getKey().toString().toLowerCase();
                        String[] words = sentence.split(" ");

                        HashMap doneWords = new HashMap();
                        if(key.split(" ").length == 1) {

                            for (String w : words) {

                                String wor = w.toLowerCase();
                                if (wor.equals(key)) {
                                    if (!doneWords.containsKey(w)) {
                                        doneWords.put(w, null);
                                        if (!localPhrases.containsKey(key)) {
                                            bw.write(pairs.getValue() + " " + w);
                                            bw.newLine();
                                        }
                                    }

                                }
                            }
                        }
                        else if(key.split(" ").length > 1){
                            if(sentence.contains(key)){
                                if(!localPhrases.containsKey(key)){
                                    if(pairs.getKey()==" "){
                                        System.out.println();
                                    }

                                    bw.write(pairs.getValue() + " " + pairs.getKey());
                                    bw.newLine();
                                }
                            }
                        }

                    }

                    sentence = phrase;
                    localPhrases = new HashMap();

                    if(skip==false){
                        bw.newLine();
                    }
                }


                currentSentenceId = sentenceId;


                if(skip==false) {

                    allPhrases.put(phrase,sentiment);
                    if (!phrase.equals(" ")) {


                        localPhrases.put(phrase, sentiment);
                        bw.write(sentiment + " " + phrase);
                        bw.newLine();
                        countP++;
                    }

                }

            }

            bReader.close();
            bw.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }






    }
}
