package com.isis;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.BuildBinarizedDataset;
import edu.stanford.nlp.sentiment.SentimentTraining;

import java.io.*;
import java.util.*;


public class KagglerController {

    private String original_file = "train.tsv";
    private String train_file = "base_train.tsv";
    private String binary_train_dataset__file = "binary_train_dataset__file.csv";
    private String binary_dev_dataset_file = "binary_dev_dataset_file.csv";
    private String dev_file = "base_dev.tsv";
    private String test_file = "test.tsv";
    private String result_file = "result.tsv";

    private StanfordCoreNLP train_pipeline;

    public KagglerController()
    {

    }

    class KagglerSentence {
        public String phraseId;
        public Integer sentenceId;
        public String phrase;
        public Integer sentiment;

        public KagglerSentence(String line)
        {
            String datavalue[] = line.split("\t");
            this.phraseId = datavalue[0];
            this.sentenceId= Integer.parseInt(datavalue[1]);
            this.phrase = datavalue[2];
            this.sentiment = Integer.parseInt(datavalue[3]);
        }
    }



    public  void Migrate()
    {
        // Input
        File dataFile = new File(this.original_file);

        try {

            FileReader fr = new FileReader(dataFile);

            BufferedReader bReader = new BufferedReader(fr);

            File fout = new File(this.binary_train_dataset__file);

            FileOutputStream fos = new FileOutputStream(fout);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            File foutdev = new File(this.binary_dev_dataset_file);

            FileOutputStream  fodevstream = new FileOutputStream(foutdev);

            BufferedWriter bwdev = new BufferedWriter(new OutputStreamWriter(fodevstream));

            String line;
            // skip the first line.
            bReader.readLine();

            Integer currentSentenceId = 0;

            Integer train_index = 0;

            Integer dev_index = 0;

            while ((line = bReader.readLine()) != null) {
                KagglerSentence ksentence = new KagglerSentence(line);

                //if it is not the same sentence id
                if (ksentence.sentenceId % 10 == 0)
                {
                    if(!currentSentenceId.equals(ksentence.sentenceId) && dev_index != 0)
                    {
                        bwdev.newLine();
                    }
                    bwdev.write(ksentence.sentiment + " " + ksentence.phrase);
                    bwdev.newLine();
                    dev_index +=1;
                }
                else
                {
                    if(!currentSentenceId.equals(ksentence.sentenceId) && train_index != 0)
                    {
                        bw.newLine();
                    }
                    bw.write(ksentence.sentiment + " " + ksentence.phrase);
                    bw.newLine();
                    train_index +=1;
                }
                currentSentenceId = ksentence.sentenceId;
            }
            bReader.close();
            bw.close();
            bwdev.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void MigrateToTree()
    {
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream(this.train_file));
            System.setOut(out);
            String[] newargs = {"-input", this.binary_train_dataset__file};
            BuildBinarizedDataset.main(newargs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            out = new PrintStream(new FileOutputStream(this.dev_file));
            System.setOut(out);
            String[] newargs = {"-input", this.binary_dev_dataset_file};
            BuildBinarizedDataset.main(newargs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
    public void TrainModel()
    {
        String[] newargs = {"-numHid", "25", "-trainPath", this.train_file,"-model", "kaggler-model.ser.gz" , "-train" };
        SentimentTraining.main(newargs);
        //:java -cp "*" -mx8g edu.stanford.nlp.sentiment.SentimentTraining -numHid 25 -trainPath manual.txt -devPath dev.txt -train -model model-dev.ser.gz
    }
}
