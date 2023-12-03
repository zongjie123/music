package com.tjetc.dao.impl;

import com.tjetc.dao.MusicDAO;
import com.tjetc.domain.Music;
import com.tjetc.utils.DButil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.tjetc.utils.JDBCUtil.getCon;

public class MusicDAOImpl implements MusicDAO {
    private Connection conn = null;
    @Override
    public List<Music> findAll() throws SQLException {
        this.conn = DButil.getConnection();
        return DButil.queryList(this.conn,Music.class,"select * from music_score");
    }

    @Override
    public List<Music> scoreMinAll() throws SQLException {
        this.conn = DButil.getConnection();
        return DButil.queryList(this.conn,Music.class,"select * from music_scoremin");
    }

    @Override
    public List<Music> numbAll() throws SQLException {
        this.conn = DButil.getConnection();
        return DButil.queryList(this.conn,Music.class,"select * from music_numb");
    }

    @Override
    public List<Music> typeAll() throws SQLException {
        this.conn = DButil.getConnection();
        return DButil.queryList(this.conn,Music.class,"select * from type_count");
    }

    //添加
    @Override
    public int insertMusic(Music music) {
        try {
            Connection con = getCon();
//            Statement st = con.createStatement();
            String sql = "INSERT INTO music (rank,name,type,year,score,numb) VALUES (?,?,?,?,?,?)";
            // prepareStatement 预编译对象，使用? 占位符。解决 SQL注入
            PreparedStatement pst = con.prepareStatement(sql);

            //参数1：表示第几个问号
            //参数2：表示值
            pst.setInt(1,music.getRank());
            pst.setString(2,music.getName());
            pst.setString(3,music.getType());
            pst.setString(4,music.getYear());
            pst.setDouble(5,music.getScore());
            pst.setInt(6,music.getNumb());
            // 执行SQL 注意：方法的参数不要再传 sql 语句了
            int i = pst.executeUpdate();
            return i;
        } catch (SQLException e) {
            System.out.println("添加音乐出错啦："+e.getErrorCode()+":"+e.getMessage());
        }
        return 0;
    }
}
