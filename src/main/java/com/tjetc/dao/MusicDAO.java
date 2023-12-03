package com.tjetc.dao;

import com.tjetc.domain.Music;

import java.sql.SQLException;
import java.util.List;

public interface MusicDAO {

    //音乐评分最大值
    public List<Music> findAll() throws SQLException;
    //音乐评分最小值
    public List<Music> scoreMinAll() throws SQLException;
    //音乐评论最大值
    public List<Music> numbAll() throws SQLException;
    //音乐类型个数
    public List<Music> typeAll() throws SQLException;
    //添加音乐

    int insertMusic(Music music);
}
