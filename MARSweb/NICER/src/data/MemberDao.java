package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import my.util.ConnUtil;
import java.sql.ResultSet;



public class MemberDao {
	public String user_email = "";
	  
 public int insertMember(MemberVo vo){
  int rst = 0;
  Connection conn = null;
  PreparedStatement ps = null;
  try{
   conn = ConnUtil.getConnection();
   String sql = "insert into data values(null,?,?,?,?,sysdate())";
   ps = conn.prepareStatement(sql);
   ps.setString(1, vo.getId());
   ps.setString(2, vo.getPass());
   ps.setString(3, vo.getName());
   ps.setString(4, vo.getEmail1());
   rst = ps.executeUpdate();
  }catch(Exception e){
   e.printStackTrace();
  }finally{
   ConnUtil.close(ps, conn);
  }
  return rst;
 }
 
 public int idCheck(String id){
	  int rst = 0;
	  Connection conn = null;
	  PreparedStatement ps = null;
	  ResultSet rs = null;
	  try{
	   conn = ConnUtil.getConnection();
	   String sql = "select * from data where id=?";
	   ps = conn.prepareStatement(sql);
	   ps.setString(1, id); 
	   rs = ps.executeQuery();
	   if(rs.next()){
	    rst = 1;
	   }
	  }catch(Exception e){
	   e.printStackTrace();
	  }finally{
	   ConnUtil.close(rs, ps, conn);
	  }
	  return rst;
	 }
 
 public int loginCheck(String id, String pass){
	  int rst = 0;
	  Connection conn = null;
	  PreparedStatement ps = null;
	  ResultSet rs = null;
	  try{
	   conn = ConnUtil.getConnection();
	   String sql = "select * from data where id=?";
	   ps = conn.prepareStatement(sql);
	   ps.setString(1, id);
	   rs = ps.executeQuery();
	   if(rs.next()){
	    String DBpass = rs.getString("pass");
	     if((DBpass.trim()).equals((pass.trim()))){
	      rst=2;  // login success
	      user_email = rs.getString("email1").trim();
	     }else{
	      rst=1;   // login fail
	     }
	    }  // no register
	  }catch(Exception e){
	   e.printStackTrace();
	  }finally{
	   ConnUtil.close(rs, ps, conn);
	  }
	  return rst;
	 }

}

