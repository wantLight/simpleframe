package com.imooc;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by xyzzg on 2020/4/7.
 */
@WebServlet("hello")
public class HelloServlet extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest request,HttpServletResponse response){
        String name = "我的简易框架鸭";
        request.setAttribute("name",name);
        try {
            request.getRequestDispatcher("WEB-INF/jsp/hello.jsp").forward(request,response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
