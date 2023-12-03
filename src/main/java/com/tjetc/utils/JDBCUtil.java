package com.tjetc.utils;

import com.tjetc.domain.Music;

import java.sql.*;
import java.util.List;

public class JDBCUtil {
    //定义连接参数
    private static String url = "jdbc:mysql://localhost:3306/test?useSSL=true&serverTimezone=GMT&useUnicode=true&characterEncoding=utf8";
    private static String user = "root";
    private static String pwd = "bq020526";
    // 加载驱动类
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    // 获取Connection连接对象
    public static Connection getCon() throws SQLException {
        return DriverManager.getConnection(url,user,pwd);
    }
    // 关闭资源，查询
    public static void close(ResultSet rs, Statement st, Connection conn){
        try{
            if (rs != null){
                rs.close();
            }
            if (st != null){
                st.close();
            }
            if(conn != null){
                conn.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    // 关闭资源，增删改
    public static void close( Statement st,Connection conn){
        try{
            if (st != null){
                st.close();
            }
            if(conn != null){
                conn.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


}
