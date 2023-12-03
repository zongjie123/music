package com.tjetc.test;

import com.tjetc.domain.Music;
import org.apache.commons.io.output.NullWriter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
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

//同一年中评分最低的音乐，评论人数最多的音乐
public class MusicScoreMin {
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
    public static  class MyReducer extends Reducer<Text, DoubleWritable,Music, DoubleWritable> {
        private static  final DoubleWritable theMax = new DoubleWritable();
        @Override
        protected void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
            Double min =Double.MAX_VALUE;
            for (DoubleWritable value : values) {
                if(value.get()<min){
                    min =value.get();
                }
            }

            Music music=new Music();
            music.setYear(key.toString());
            music.setScore(Double.valueOf(min.toString()));
//            theMax.set(max);
            context.write(music,null);
        }
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        conf.set("mapred.job.tracker", "192.168.100.110:9001");
        conf.set("fs.default.name", "hdfs://192.168.100.110:9000");
        DBConfiguration.configureDB(conf,"com.mysql.jdbc.Driver","jdbc:mysql://192.168.100.100:3306/test?useSSL=false","root","bq020526");
        Job job = Job.getInstance(conf, "MusicScoreMin");
        job.setJarByClass(MusicScoreMin.class);
        FileInputFormat.setInputPaths(job,new Path("/music/*"));
        job.setInputFormatClass(TextInputFormat.class);
        job.setMapperClass(MusicScoreMin.MyMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(DoubleWritable.class);
        job.setNumReduceTasks(1);
        job.setReducerClass(MusicScoreMin.MyReducer.class);
        job.setOutputKeyClass(Music.class);
        job.setOutputValueClass(NullWriter.class);
        job.setOutputFormatClass(DBOutputFormat.class);
        DBOutputFormat.setOutput(job,"music_scoremin","year","min");
        boolean result = job.waitForCompletion(true);
        System.exit(result ? 0 : 1);
    }
}
