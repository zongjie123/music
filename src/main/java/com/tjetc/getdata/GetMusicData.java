package com.tjetc.getdata;


import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import com.tjetc.dao.MusicDAO;
import com.tjetc.dao.impl.MusicDAOImpl;
import com.tjetc.domain.Music;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GetMusicData {
    static List<Music> musics=new ArrayList<Music>();
    static Integer rank=1;
    public static List<Music> getData(String url, HttpClient client) throws IOException {
        String UserAgent ="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36 Edg/108.0.1462.54";
//        String Host="music.douban.com";
        String Accept="text/html";
        String AcceptCharset="UTF-8";
        String AcceptEncoding="gzip";
        String AcceptLanguage="en-US,en";
        //1.创建get请求
        HttpGet get  = new HttpGet(url);
        get.setHeader("User-Agent",UserAgent);
//        get.setHeader("Host",Host);
        get.setHeader("Accept",Accept);
        get.setHeader("AcceptCharset",AcceptCharset);
        //2.创建接受响应对象。
        HttpResponse response  = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK,"ok");
        response =  client.execute(get);
        int statusCode = response.getStatusLine().getStatusCode();
//        System.out.println(statusCode);
        if(statusCode==200){
            String s = EntityUtils.toString(response.getEntity(), "UTF-8");
            Document doc = Jsoup.parse(s);
//            System.out.println("aaa"+doc);
            Elements elements = doc.select("div[class=pl2]");
//            System.out.println("sss"+elements);
            for (Element element : elements) {
                String name = element.select("a").text();
                String desc = element.select("p[class=pl]").text();
                String score = element.select("div[class=star clearfix]").select("span[class=rating_nums]").text();
                String numb = element.select("div[class=star clearfix]").select("span[class=pl]").text();

                String[] de=desc.split("/ ");
                String age1=de[1];
                String age=age1.substring(0,4);
                String types=de[3];
                String type=types.substring(0,2);
                String num=numb.substring(2,7);

                Music music=new Music(rank++,name,type,age,Double.valueOf(score),Integer.valueOf(num));
                musics.add(music);
            }
//            System.out.println(musics);
            String nextUrl = doc.select("div[class=paginator]").select("span[class=next]").select("a").attr("href");
            if(nextUrl==null||"".equals(nextUrl)){

            }else {
                getData(nextUrl,client);
            }
        }
        return musics;
    }

    public static void main(String[] args) throws IOException {
        // 数据库连接信息
        String url = "jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=GMT&useUnicode=true&characterEncoding=utf8";
        String username = "root";
        String password = "bq020526";

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        HttpClient client = httpClientBuilder.build();
        List<Music> data = GetMusicData.getData("https://music.douban.com/top250", client);

        // 连接数据库并插入数据
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "INSERT INTO music (rank,name,type,year,score,numb) VALUES (?,?,?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            // 逐个插入数据
            for (Music datum : data) {
                statement.setInt(1,datum.getRank());
                statement.setString(2,datum.getName());
                statement.setString(3,datum.getType());
                statement.setString(4,datum.getYear());
                statement.setDouble(5,datum.getScore());
                statement.setInt(6,datum.getNumb());
                statement.executeUpdate();
            }

            System.out.println("Data uploaded successfully!");
        } catch (SQLException e) {
            System.out.println("Error uploading data to database: " + e.getMessage());
        }
    }

//        for (Music datum : data) {
//            System.out.println(datum);
//        }



    }

