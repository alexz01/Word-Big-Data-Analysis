package edu.buffalo.mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
//import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
//import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Reducer;

public class CooccurrenceReducer
    extends Reducer<Text, IntWritable, Text, IntWritable> {

  @Override
  protected void reduce(Text map_key, Iterable<IntWritable> values,
      Context context) throws IOException, InterruptedException {
    // TODO Auto-generated method stub
    // super.reduce(arg0, arg1, arg2);

    int sum = 0;
    for (IntWritable val : values) {
      sum += val.get();
    }
    context.write(map_key, new IntWritable(sum));

  }

}
