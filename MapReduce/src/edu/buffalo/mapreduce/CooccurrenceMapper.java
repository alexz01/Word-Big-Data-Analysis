package edu.buffalo.mapreduce;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;

import org.apache.hadoop.io.IntWritable;
//import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class CooccurrenceMapper extends Mapper<Object, Text, Text, IntWritable> {
  
  private Text word = new Text();
  private Text co_word = new Text();
  private BufferedReader f;
  private HashSet<String> stopWordSet;
  private BufferedReader newsWordsReader;
  ArrayList<String> topten = new ArrayList<String>(10);

  @Override
  protected void map(Object key, Text value, Context context)
      throws IOException, InterruptedException {
    // TODO Auto-generated method stub
    // super.map(key, value, context);
    String words = value.toString().replace('’', '\'');
    String[] tokens = words.split("[!'\"“”‘,.\\r\\n\\s)(:;|_?\\[\\]\\•#]");
    if (tokens.length > 1) {
      ArrayList<String> tokenList =
          new ArrayList<String>(Arrays.asList(tokens));
      for (String topTenWord : topten) {
        word.set(topTenWord);
        if (tokenList.contains(topTenWord)) {
          
          for (String string : tokenList) {
            if (string.length() > 0 && string.matches("[a-zA-Z]+"))
              
              if (!stopWordSet.contains(string.toLowerCase())) {
                if(topTenWord.equalsIgnoreCase(string)) continue;
                co_word.set(string.toLowerCase());
//                MapWritable map = new MapWritable();
//                map.put(word, co_word);
                context.write(new Text(word.toString()+","+co_word), new IntWritable(1));
              }
          }
        }
      }
    }

  }

  @Override
  protected void setup(Context context)
      throws IOException, InterruptedException {
    // TODO Auto-generated method stub
    f = new BufferedReader(new FileReader(new File("./StopWords.txt")));
    stopWordSet = new HashSet<String>();
    while (f.ready()) {
      stopWordSet.add(f.readLine());
    }
    newsWordsReader = new BufferedReader(new FileReader(
        new File("./data/NewsWords/OneDay/part-r-00000")));
    LinkedHashMap<String, Integer> wordmap =
        new LinkedHashMap<String, Integer>();

    while (newsWordsReader.ready()) {

      String word_count = newsWordsReader.readLine();
      String[] list = word_count.split("[\\t]");
      if (list[0].matches("[a-zA-Z]+"))
        wordmap.put(list[0], Integer.parseInt(list[1]));
    }
    newsWordsReader.close();
    for (int i = 0; i < 10; i++) {
      int max = 0;
      for (String key : wordmap.keySet()) {
        if (max < (int) wordmap.get(key)) {
          max = (int) wordmap.get(key);
          if (topten.size() <= i)
            topten.add(i, key);
          topten.set(i, key);
        }
      }
      wordmap.remove(topten.get(i));
    }

  }

}
