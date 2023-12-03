package com.tjetc.cotroller;

import com.alibaba.fastjson.JSON;
import com.tjetc.domain.Music;
import com.tjetc.service.MusicService;
import com.tjetc.service.impl.MusicServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/numb")
public class NumbMaxServlet extends HttpServlet{
    private MusicService musicService=new MusicServiceImpl();
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Music> all =null;
        try {
            all = musicService.numbAll();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        String s = JSON.toJSONString(all);
        resp.getWriter().write(s);
// json 和网页当中脚本语言javascript jquery 数据通信
//      json本质就字符串，但是有规则的字符串。

    }

}
