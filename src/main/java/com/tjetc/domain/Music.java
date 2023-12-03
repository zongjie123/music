package com.tjetc.domain;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.lib.db.DBWritable;
import org.apache.hadoop.yarn.webapp.hamlet2.Hamlet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Music implements Writable, DBWritable ,Serializable{
    //音乐排名
    private  Integer rank;
    //音乐名称
    private String name;
    //音乐类型
    private  String type;
    //音乐年份
    private String year;
    //评分
    private  Double score;
    //评论人数
    private  Integer numb;

    //计数
    private Integer count;
    //最小值
    private Double min;

    @Override
    public String toString() {
        return "Music{" +
                "rank=" + rank +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", year='" + year + '\'' +
                ", score=" + score +
                ", numb=" + numb +
                '}';
    }
    public Music(){}
    public Music(Integer rank, String name, String type, String year, Double score, Integer numb) {
        this.rank = rank;
        this.name = name;
        this.type = type;
        this.year = year;
        this.score = score;
        this.numb = numb;

    }

    public Music(String type, Integer count) {
        this.type = type;
        this.count = count;
    }



    public Music(String year, Double min) {
        this.year = year;
        this.min = min;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getNumb() {
        return numb;
    }

    public void setNumb(Integer numb) {
        this.numb = numb;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(String.valueOf(this.rank));
        dataOutput.writeUTF(this.name);
        dataOutput.writeUTF(this.type);
        dataOutput.writeUTF(this.year);
        dataOutput.writeUTF(String.valueOf(this.score));
        dataOutput.writeUTF(String.valueOf(this.numb));

    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.rank= Integer.valueOf(dataInput.readUTF());
        this.name =dataInput.readUTF();
        this.type=dataInput.readUTF();
        this.year =dataInput.readUTF();
        this.score= Double.valueOf(dataInput.readUTF());
        this.numb= Integer.valueOf(dataInput.readUTF());

    }

    @Override
    public void write(PreparedStatement ps) throws SQLException {
//        ps.setString(1,this.name);

//        ps.setString(1,this.year);
//        ps.setDouble(2,this.score);
        ps.setString(1,this.type);
        ps.setInt(2,this.rank);
//        ps.setInt(2,this.numb);

    }

    @Override
    public void readFields(ResultSet re) throws SQLException {
        this.rank =re.getInt("rank");
        this.name =re.getString("name");
        this.type=re.getString("type");
        this.year=re.getString("year");
        this.score=re.getDouble("score");
        this.numb =re.getInt("numb");

    }
}
