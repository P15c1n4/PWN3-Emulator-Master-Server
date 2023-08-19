package Server;

import Model.Char;
import Model.Conta;
import Model.Item;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DAO {
    
    private final String driver = "com.mysql.cj.jdbc.Driver";
    private final String url = "jdbc:mysql://127.0.0.1:3306/pwn3?useTimezone=true&serverTimezone=UTC";
    private final String user = "root";
    private final String password = "";
    
    
    private Connection conectarMysql() {
        Connection con = null;
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, password);
            return con;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
    
    
    public boolean RegisterAcc(Conta conta){
        String sql = "insert into accont (accName, accPass, accTeamHash, accTeamName) values(?,?,?,?)";
        
        try{
            Connection con = conectarMysql();
            
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, conta.getAccName());
            pst.setString(2, conta.getAccPass());
            pst.setString(3, conta.getAccTeamHash());
            pst.setString(4, conta.getAccTeamName());
            pst.executeUpdate();
            
            sql = "select id from accont where accName = ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, conta.getAccName());
            
            ResultSet rs = pst.executeQuery();
            rs.next();
            
            conta.setAccId(String.valueOf(rs.getInt("id")));
            
            con.close();
            
            return true;
            
            
        }catch(SQLException e){
            System.out.println(e);
            return false;
        }


    } 
    
        public boolean RegisterChar(Char chara){
        String sql = "insert into accchar (accId, nick, charSpec) values(?,?,?)";
        
        try{
            Connection con = conectarMysql();
            
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, chara.getAccId());
            pst.setString(2, chara.getCharName());
            pst.setString(3, chara.getCharSpec());
            
            pst.executeUpdate();
            
            sql = "select charId from accchar where nick = ? and accId = ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, chara.getCharName());
            pst.setString(2, chara.getAccId());
            
            ResultSet rs = pst.executeQuery();
            rs.next();
            
            chara.setCharId(String.valueOf(rs.getInt("charId")));
            
            
            sql = "insert into charitens (charID,item,total,hand,bulletAta) values(?,?,?,?,?)";
            
            pst = con.prepareStatement(sql);
            pst.setString(1, chara.getCharId());
            pst.setString(2, "GreatBallsOfFire");
            pst.setInt(3, 1);
            pst.setBoolean(4, true);
            pst.setInt(5, 0);
            
            pst.executeUpdate();
            
            pst = con.prepareStatement(sql);
            pst.setString(1, chara.getCharId());
            pst.setString(2, "CowboyCoder");
            pst.setInt(3, 1);
            pst.setBoolean(4, true);
            pst.setInt(5, 6);

            pst.executeUpdate();
            
            pst = con.prepareStatement(sql);
            pst.setString(1, chara.getCharId());
            pst.setString(2, "RevolverAmmo");
            pst.setInt(3, 10);
            pst.setBoolean(4, false);
            pst.setInt(5, 0);
            
            pst.executeUpdate();
            
            con.close();
            
            return true;
            
            
        }catch(SQLException  e){
            System.out.println(e);
            return false;
        }

    } 
        
        public ArrayList<Item> GetCharInfo(Char chara){
        
            ArrayList<Item> itens = new ArrayList();
            String sql = "select * from accchar where charId = ?";
        
        
            try{
                Connection con = conectarMysql();

                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, chara.getCharId());

                ResultSet rs = pst.executeQuery();
                rs.next();

                chara.setCharId(rs.getString("charId"));
                chara.setAccId(String.valueOf(rs.getInt("accId")));
                chara.setCharName(rs.getString("nick"));
                chara.setCharSpec(rs.getString("charSpec"));
                chara.setCharStatus(rs.getString("charStatus"));
                chara.setCharLocal(rs.getString("charLocal"));


                sql = "Select * from charitens where charId = ?";

                pst = con.prepareStatement(sql);
                pst.setInt(1, Integer.valueOf(chara.getCharId()));

                rs = pst.executeQuery();

                while(rs.next()){
                    Item item = new Item();
                    
                    item.setItemName(rs.getString("item"));
                    item.setItemId(String.valueOf(rs.getInt("id")));
                    item.setItemQuant(rs.getInt("total"));
                    item.setCharID(String.valueOf(rs.getInt("charID")));
                    item.setHand(rs.getBoolean("hand"));
                    item.setBulletAta(rs.getInt("bulletAta"));
                    
                    itens.add(item);
                }
                
                con.close();
                
                
                return itens;
            
        }catch(SQLException e){
             System.out.println(e);
             return itens;
        }
        

    } 
        
        public boolean Login(Conta conta){
        String sql = "select * from accont where accName = ? and accPass = ?";
        
        try{
            Connection con = conectarMysql();
            
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, conta.getAccName());
            pst.setString(2, conta.getAccPass());
            
            ResultSet rs = pst.executeQuery();
            
            if(rs.next()){
                conta.setAccId(String.valueOf(rs.getInt("id")));
                conta.setAccTeamHash(rs.getString("accTeamHash"));
                conta.setAccTeamName(rs.getString("accTeamName"));
                
                return true;
            }
            
            con.close();
            
            return false;          

        }catch(SQLException e){
            System.out.println(e);
            return false;
        }
        

    } 
    public ArrayList<Char> GetAccChar(Conta conta){
        String sql = "select * from accchar where accId = ?";
        
        ArrayList<Char> charas = new ArrayList();
        
        try{
            Connection con = conectarMysql();
            
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, conta.getAccId());
            
            ResultSet rs = pst.executeQuery();
            
            
            
            while(rs.next()){
                
                Char newChar = new Char();
                
                newChar.setCharId(rs.getString("charId"));
                newChar.setAccId(String.valueOf(rs.getInt("accId")));
                newChar.setCharName(rs.getString("nick"));
                newChar.setCharSpec(rs.getString("charSpec"));
                newChar.setCharStatus(rs.getString("charStatus"));
                newChar.setCharLocal(rs.getString("charLocal"));
            
                charas.add(newChar);
            }

            con.close();
            
            return charas;
            
            
        }catch(SQLException e){
            System.out.println(e);
            return charas;
        }
        

    } 
     
    public boolean CharNameVerify(Char chara){
        String sql = "select * from accchar where nick = ? ";
        
        try{
            boolean result;
            
            Connection con = conectarMysql();
            
            PreparedStatement pst = con.prepareStatement(sql);
            pst = con.prepareStatement(sql);
            pst.setString(1, chara.getCharName());
            
            ResultSet rs = pst.executeQuery();
            
            if(rs.next()){
                result = true;
            }else{
                result = false;
            }
            
            con.close();
            
            return result;
            
            
        }catch(SQLException  e){
            System.out.println(e);
            return false;
        }
        

    } 
    public boolean AccNameVerify(Conta conta){
        String sql = "select * from accont where accName = ? ";
        
        try{
            boolean result;
            
            Connection con = conectarMysql();
            
            PreparedStatement pst = con.prepareStatement(sql);
            pst = con.prepareStatement(sql);
            pst.setString(1, conta.getAccName());
            
            ResultSet rs = pst.executeQuery();
            
            if(rs.next()){
                result = true;
            }else{
                result = false;
            }
            
            con.close();
            
            return result;
            
            
        }catch(SQLException  e){
            System.out.println(e);
            return false;
        }
        

    } 
    public boolean TeamNameVerify(Conta conta){
        String sql = "select * from accont where accTeamName = ? ";
        
        try{
            boolean result;
            
            Connection con = conectarMysql();
            
            PreparedStatement pst = con.prepareStatement(sql);
            pst = con.prepareStatement(sql);
            pst.setString(1, conta.getAccTeamName());
            
            ResultSet rs = pst.executeQuery();
            
            if(rs.next()){
                result = true;
            }else{
                result = false;
            }
            
            con.close();
            
            return result;
            
            
        }catch(SQLException  e){
            System.out.println(e);
            return false;
        }
        

    }
}
