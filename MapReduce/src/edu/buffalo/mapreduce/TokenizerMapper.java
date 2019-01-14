package edu.buffalo.mapreduce;

import org.apache.hadoop.mapreduce.Mapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
//import java.util.StringTokenizer;
import java.util.HashSet;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

public class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {
  private final static IntWritable one = new IntWritable(1);
  private Text word = new Text();

  BufferedReader f;
  HashSet<String> stopWordSet;

  @Override
  protected void map(Object key, Text value, Context context)
      throws IOException, InterruptedException {
    // TODO Auto-generated method stub
    // super.map(key, value, context);
    System.out.println("newLine: " + value.toString());
    String words = value.toString().replace('’', '\'');
    String[] string_list = words.split("[!'\"“”‘,.\\r\\n\\s)(:;|_?\\[\\]\\•#]");
    for (String string : string_list) {
      if (string.length() > 0 && string.matches("[a-zA-Z]+"))
        if (!stopWordSet.contains(string.toLowerCase())) {
          word.set(string.toLowerCase());
          context.write(word, one);
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
  }
}
