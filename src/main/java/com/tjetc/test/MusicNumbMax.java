package com.tjetc.test;

import com.tjetc.domain.Music;
import org.apache.commons.io.output.NullWriter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.util.Arrays;

//同一年中评分最高的音乐，评论人数最多的音乐
public class MusicNumbMax {
    public static class MyMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        public static final IntWritable temp = new IntWritable();

        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
           String [] dou=new String []{value.toString().replace("\"","")};
           for (String d:dou){
               String [] dou1=new String []{Arrays.toString(d.toString().split("\n"))};
//               System.out.println(d);
               for (String d1:dou1){
                   String[] dou2=d1.toString().split(",");
                   String year=dou2[3];
                   String numb=dou2[5];
//                   String num=numb.substring(0,3);
                   temp.set( Integer.valueOf(numb.substring(0,numb.length()-1)));
                   context.write(new Text(year), temp);
               }
           }

        }
    }

    //reduce 输入key value
    public static  class MyReducer extends Reducer<Text, IntWritable,Music, IntWritable> {
//        private static  final DoubleWritable theMax = new DoubleWritable();
//        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            Integer max =Integer.MIN_VALUE;
            for (IntWritable value : values) {
                if(value.get()>max){
                    max =value.get();
                }
            }
            Music music=new Music();
            music.setYear(key.toString());
            music.setNumb(Integer.valueOf(max.toString()));
//            theMax.set(max);
            context.write(music,null);
        }
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        conf.set("mapred.job.tracker", "192.168.100.110:9001");
        conf.set("fs.default.name", "hdfs://192.168.100.110:9000");
        DBConfiguration.configureDB(conf,"com.mysql.jdbc.Driver","jdbc:mysql://192.168.100.100:3306/test?useSSL=false","root","bq020526");
        Job job = Job.getInstance(conf, "MusicScoreMax");
        job.setJarByClass(MusicNumbMax.class);
        FileInputFormat.setInputPaths(job,new Path("/music/*"));
        job.setInputFormatClass(TextInputFormat.class);
        job.setMapperClass(MusicNumbMax.MyMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setNumReduceTasks(1);
        job.setReducerClass(MusicNumbMax.MyReducer.class);
        job.setOutputKeyClass(Music.class);
        job.setOutputValueClass(NullWriter.class);
        job.setOutputFormatClass(DBOutputFormat.class);
        DBOutputFormat.setOutput(job,"music_numb","year","numb");
        boolean result = job.waitForCompletion(true);
        System.exit(result ? 0 : 1);
    }
}
