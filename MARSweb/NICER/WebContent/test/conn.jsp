<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
    <%@ page import = "my.util.ConnUtil" %>
    <%@ page import = "java.sql.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01

Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
 Connection conn = null;
 PreparedStatement ps = null;
 try{
  conn = ConnUtil.getConnection();
%>
<b>연결성공!!</b>
<%
 }catch(Exception e){
  e.printStackTrace();
%>
<b>연결실패!!</b>
<%
 }finally{
  ConnUtil.close(ps, conn);
 }
%>