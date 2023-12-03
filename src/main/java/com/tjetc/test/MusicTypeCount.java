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

//评分在9.0以上的个数
public class MusicTypeCount {
    //mapper的泛型要是mapreduce的能认识的类型
    //mapper key value的位置（偏移量（数 long）） value（字符串 String）
    //输出key（String）  value(int)
    public static class MyMapper extends Mapper<LongWritable, Text,Text, IntWritable> {
        private static  final IntWritable theOne = new IntWritable(1);
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String [] dou=new String []{value.toString().replace("\"","")};
            for (String d:dou){
                String [] dou1=new String []{Arrays.toString(d.toString().split("\n"))};
//               System.out.println(d);
                for (String d1:dou1){
                    String[] dou2=d1.toString().split(",");
                    String type=dou2[2];
//                    String numb=dou2[5];
//                   String num=numb.substring(0,3);
//                    temp.set( Integer.valueOf(numb.substring(0,numb.length()-1)));
                    context.write(new Text(type), theOne);
                }
            }
        }
    }
    //reduce 输入key value
    public static  class MyReducer extends Reducer<Text, IntWritable, Music, NullWriter> {
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            Integer sum =0;
            for (IntWritable value : values) {
                sum+=value.get();
            }
            Music music=new Music();
            music.setType(key.toString());

            music.setRank(Integer.valueOf(sum.toString()));
            context.write(music,null);
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        conf.set("mapred.job.tracker", "192.168.100.110:9001");
        conf.set("fs.default.name", "hdfs://192.168.100.110:9000");
        DBConfiguration.configureDB(conf,"com.mysql.jdbc.Driver","jdbc:mysql://192.168.100.100:3306/test?useSSL=false&serverTimezone=GMT&useUnicode=true&characterEncoding=utf8","root","bq020526");
        Job job = Job.getInstance(conf, "MusicTypeCount");
        job.setJarByClass(MusicTypeCount.class);
        FileInputFormat.setInputPaths(job,new Path("/music/*"));
        job.setInputFormatClass(TextInputFormat.class);
        job.setMapperClass(MyMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setNumReduceTasks(1);
        job.setReducerClass(MyReducer.class);
        job.setOutputKeyClass(Music.class);
        job.setOutputValueClass(NullWriter.class);
        job.setOutputFormatClass(DBOutputFormat.class);
        DBOutputFormat.setOutput(job,"type_count","type","count");
        boolean result = job.waitForCompletion(true);
        System.exit(result ? 0 : 1);
    }
}
