package com.tjetc.test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.util.Arrays;

//同一年中评分最高的音乐，评论人数最多的音乐
public class MusicScoreMax {
    public static class MyMapper extends Mapper<LongWritable, Text, Text, DoubleWritable> {
        public static final DoubleWritable temp = new DoubleWritable();

        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
           String [] dou=new String []{value.toString().replace("\"","")};
           for (String d:dou){
               String [] dou1=new String []{Arrays.toString(d.toString().split("\n"))};
               for (String d1:dou1){
                   String[] dou2=d1.toString().split(",");
                   String year=dou2[3];
                   String score=dou2[4];
                   temp.set(Double.valueOf(score));
                   context.write(new Text(year), temp);
               }
           }

        }
    }

    //reduce 输入key value
    public static  class MyReducer extends Reducer<Text, DoubleWritable,Text, DoubleWritable> {
        private static  final DoubleWritable theMax = new DoubleWritable();
        @Override
        protected void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
            Double max =Double.MIN_VALUE;
            for (DoubleWritable value : values) {
                if(value.get()>max){
                    max =value.get();
                }
            }
            theMax.set(max);
            context.write(key,theMax);
        }
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        conf.set("mapred.job.tracker", "192.168.100.110:9001");
        conf.set("fs.default.name", "hdfs://192.168.100.110:9000");
        Job job = Job.getInstance(conf, "MusicScoreMax");
        job.setJarByClass(MusicScoreMax.class);
        FileInputFormat.setInputPaths(job,new Path("/music/*"));
        job.setInputFormatClass(TextInputFormat.class);
        job.setMapperClass(MyMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(DoubleWritable.class);
        job.setNumReduceTasks(1);
        job.setReducerClass(MyReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        FileOutputFormat.setOutputPath(job,new Path("/musicoutput/"));
        job.setOutputFormatClass(TextOutputFormat.class);
        boolean result = job.waitForCompletion(true);
        System.exit(result ? 0 : 1);
    }
}
