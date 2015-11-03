package com.isis;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 11/2/2015.
 */
public class TestSentimentController {

    private String input;
    private String output;
    //"model-dev-0006-76.55.ser.gz"
    private String model;
    private SentimentAnalyzer analyzer;
    public  TestSentimentController(String input, String output, String model)
    {
        this.input = input;
        this.output = output;
        this.model = model;
        this.analyzer = new SentimentAnalyzer(this.model);
    }

    public void Process() throws IOException {
        List<String> lines = new ArrayList<String>();
        File dataFile = new File(this.input);
        FileReader fr = new FileReader(dataFile);
        BufferedReader bReader = new BufferedReader(fr);

        File fout = new File(this.output);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        String line;
        bReader.readLine();

        while ((line = bReader.readLine()) != null) {

            String datavalue[] = line.split("\t");
            String phraseId = datavalue[0];
            Integer sentenceId = Integer.parseInt(datavalue[1]);
            String phrase = datavalue[2];
            int sentiment = this.analyzer.findSentiment(phrase);
            String str = phraseId + "," + sentiment;
            System.out.println(str);
        }
        bReader.close();
        bw.close();
    }
}
