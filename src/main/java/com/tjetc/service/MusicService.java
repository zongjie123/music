package com.tjetc.service;

import com.tjetc.domain.Music;

import java.sql.SQLException;
import java.util.List;

public interface MusicService {
    public List<Music> findAll() throws SQLException;
    public List<Music> scoreMinAll() throws SQLException;
    public List<Music> numbAll() throws SQLException;
    public List<Music> typeAll() throws SQLException;

    //添加
    int addMusic(int rank,String name,String type,String year,double score,int numb);
}
