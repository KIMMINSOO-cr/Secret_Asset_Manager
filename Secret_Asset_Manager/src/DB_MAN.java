import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DB_MAN {

    String strDriver = "com.mysql.cj.jdbc.Driver";
    String strURL = "jdbc:mysql://localhost:3306/secret_asset?characterEncoding=UTF-8&serverTimezone=UTC";
    String strUser = "root";
    String strPWD = "inha1958"; 

    Connection DB_con; 
    Statement DB_stmt;
    ResultSet DB_rs;   

    public void dbOpen() throws IOException {
        try {
            HG_ErrorMessage();
            Class.forName(strDriver); 
            DB_con = DriverManager.getConnection(strURL, strUser, strPWD); 
            DB_stmt = DB_con.createStatement(); 
        } catch (Exception e) {
            System.out.println("SQLException : " + e.getMessage());
        }
    }

    public void dbClose() throws IOException {
        try {
            if (DB_stmt != null) DB_stmt.close();
            if (DB_con != null) DB_con.close();
        } catch (Exception e) {
            System.out.println("SQLException : " + e.getMessage());
        }
    }

    public void HG_ErrorMessage() throws java.io.UnsupportedEncodingException {
       try {
           System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
       } catch (java.io.UnsupportedEncodingException e) {
           e.printStackTrace();
       }
    }
}