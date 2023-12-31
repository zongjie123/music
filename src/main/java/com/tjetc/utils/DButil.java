package com.tjetc.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


//链接数据类型
public class DButil {
	//mysql 5 和  8
	//连接数据库四要素有没有变化？
	//driver   5  com.mysql.jdbc.Driver  8 com.mysql.cj.jdbc.Driver
	//url      5  可以不加任何参数       8 有的需要添加时区。 shanghai
	//username  root
	//password  root
	private static String driver="com.mysql.jdbc.Driver";
	private static String url="jdbc:mysql://192.168.100.100:3306/test?useSSL=false";
	private static String username="root";
	private static String password="bq020526";
	static { //只会执行一次。

		try {
			//直接进行类加载 加载驱动 只需要加载一次。
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//工厂模式 创建一个方法获取链接对象
	public static Connection getConnection() throws SQLException{
		return DriverManager.getConnection(url,username,password);
	}
	//1.加载驱动
	//2.创建连接对象
	//3.通过连接对象创建可以执行sql语句的对象  statement preparedstatement
	//4.通过statement preparedstatement执行sql查询后，得到查询结果resultset。
	//5.关闭资源
	//创建方法关闭资源
	//re  pstmt  conn
	public static void close(Connection conn,PreparedStatement pstmt,ResultSet rs) throws SQLException{
		if(rs !=null && !rs.isClosed())rs.close();
		if(pstmt !=null && !pstmt.isClosed())pstmt.close();
		if(conn !=null && !conn.isClosed())conn.close();
	}
	//创建一个方法用于进行增删改   1:数据库链接对象  2：sql语句 3:？赋值
	public static int update(Connection conn,String sql,Object...params){
		PreparedStatement pstmt=null;
		//获取预编译数据库操作对象
		// 不管是查询还是修改
		//预编译数据库操作对象
		//1.预处理不完整sql语句
		//2.给不完整sql 填值
		//3.执行  1.query  ==>executeQuery   增删改 ==>executeUpdate
		try {
			pstmt=getPreparedStatement(conn,sql,params);
			//执行sql
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}finally {
			try {
				close(conn, pstmt, null);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	//创建静态方法进行属性赋值
	private static PreparedStatement getPreparedStatement(Connection conn,String sql,Object...params) throws SQLException{
		PreparedStatement pstmt=null;
		pstmt=conn.prepareStatement(sql);
		//如果参数是空的 直接return pstmt
		if(params==null)return pstmt;
		//遍历params数据  为Sql语句中？进行赋值
		for (int i = 0; i < params.length; i++) {
			//?的位置是从1开始   数组的位置从0开始
			pstmt.setObject(i+1, params[i]);
		}
		return pstmt;
	}
	//创建一个方法用于获取查询结果集 里面字段
	private static String[] getCol(ResultSet rs) throws SQLException{
		//获取元数据
		ResultSetMetaData metaData = rs.getMetaData();
		//得到数据一共有几个字段
		int i = metaData.getColumnCount();
		//创建一个字符串数组用于存储字段名称
		String [] arr=new String[i];
		for (int j = 0; j < arr.length; j++) {
			//数据库 下标从1开始
			arr[j]=metaData.getColumnLabel(j+1);
		}
		return arr;
	}
	//创建一个查询的方法 单个数据返回
	public static <T> T query(Connection conn,Class<T> c,String sql,Object...params){
		//创建数据库操作对象
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		try {
			pstmt=getPreparedStatement(conn, sql, params);
			//调用
			rs=pstmt.executeQuery();
			//通过结果集调用方法获取所有的字段名称数组
			String[] arr = getCol(rs);
			//判断结果集是否有数据
			if(rs.next()){
				//创建一个Class类型对象
				Object obj = c.newInstance();
				//利用for循环遍历字节名称数组
				for (int i = 0; i < arr.length; i++) {//empno   Empno
					//利用字符串拼接 拼接setXX方法  转换字段名称首字母大写
					String methodName="set"+Character.toUpperCase(arr[i].charAt(0))+arr[i].substring(1);
					//利用结果集对象获取当前这条数据的值  通过字符串名称获取
					Object value = rs.getObject(arr[i]);
					//判断value是否是空的 如果是空格 跳出本次循环
					if(value==null)continue;
					//通过反射获取方法对象
					Method method = obj.getClass().getMethod(methodName, value.getClass());
					//执行方法
					method.invoke(obj, value);
				}
				return (T) obj;
			}
			return null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}finally {
			try {
				close(conn, pstmt, rs);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	//创建一个查询的方法 多个数据返回
	public static <T> List<T> queryList(Connection conn,Class<T> c,String sql,Object...params){
		//创建数据库操作对象
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		//创建一个List集合用于进行数据返回
		List<Object> list=new ArrayList<Object>();
		try {
			pstmt=getPreparedStatement(conn, sql, params);
			//调用
			rs=pstmt.executeQuery();
			//通过结果集调用方法获取所有的字段名称数组
			String[] arr = getCol(rs);
			//判断结果集是否有数据  多个数据结果
			while(rs.next()){
				//创建一个Class类型对象
				Object obj = c.newInstance();
				//利用for循环遍历字节名称数组
				for (int i = 0; i < arr.length; i++) {//empno   Empno
					//利用字符串拼接 拼接setXX方法  转换字段名称首字母大写
					String methodName="set"+Character.toUpperCase(arr[i].charAt(0))+arr[i].substring(1);
					//利用结果集对象获取当前这条数据的值  通过字符串名称获取
					Object value = rs.getObject(arr[i]);
					//判断value是否是空的 如果是空格 跳出本次循环
					if(value==null)continue;
					//通过反射获取方法对象
					Method method = obj.getClass().getMethod(methodName, value.getClass());
					//执行方法
					method.invoke(obj, value);
				}
				//将查询的对象放入到List集合里
				list.add(obj);
			}
			return (List<T>) list;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}finally {
			try {
				close(conn, pstmt, rs);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}












