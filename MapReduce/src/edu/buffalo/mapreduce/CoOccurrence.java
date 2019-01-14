package edu.buffalo.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class CoOccurrence {

  public CoOccurrence() {
    // TODO Auto-generated constructor stub
  }

  public static void main(String[] args) throws Exception{
    // TODO Auto-generated method stub

    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "cooccurrence");
    job.setJarByClass(CoOccurrence.class);
    job.setMapperClass(CooccurrenceMapper.class);
    job.setCombinerClass(CooccurrenceReducer.class);
    job.setReducerClass(CooccurrenceReducer.class);
//    job.setMapOutputKeyClass(MapWritable.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1); 
  }

}
