package com.tjetc.service.impl;

import com.tjetc.dao.MusicDAO;
import com.tjetc.dao.impl.MusicDAOImpl;
import com.tjetc.domain.Music;
import com.tjetc.service.MusicService;

import java.sql.SQLException;
import java.util.List;

public class MusicServiceImpl  implements MusicService {
    MusicDAO musicDAO=new MusicDAOImpl();

    @Override
    public List<Music> findAll() throws SQLException {
        return musicDAO.findAll();
    }

    @Override
    public List<Music> scoreMinAll() throws SQLException {
        return musicDAO.scoreMinAll();
    }

    @Override
    public List<Music> numbAll() throws SQLException {
        return musicDAO.numbAll();
    }

    @Override
    public List<Music> typeAll() throws SQLException {
        return musicDAO.typeAll();
    }

    //添加
    @Override
    public int addMusic(int rank, String name, String type, String year, double score, int numb) {
        Music music=new Music();
        music.setRank(rank);
        music.setName(name);
        music.setType(type);
        music.setYear(year);
        music.setScore(score);
        music.setNumb(numb);

        int i=musicDAO.insertMusic(music);
        return i;
    }
}
