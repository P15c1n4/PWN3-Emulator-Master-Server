
package Server;

import Model.Char;
import Model.Conta;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.net.ssl.SSLSocket;
import org.apache.commons.codec.digest.DigestUtils;

public class Responce extends Thread{
    Packer packer = new Packer();
    DAO dao =new DAO();
    Char chara = new Char();
    Conta conta = new Conta();

    
    InputStream inputStream;
    OutputStream outputStream;
            
    public Responce(InputStream inputStream,OutputStream outputStream){
        this.inputStream = inputStream;
        this.outputStream = outputStream;
 
    }
    
    @Override
    public void run() {
        
        byte[] buffer = new byte[4096];
        int bytesRead;
              
        try{
            //pacote de login
            SendData(packer.LoginMenssage("Servidor Piscina", "Este servidor foi baseado no servidor original do jogo PWN3"));
            
            while((bytesRead = inputStream.read(buffer)) != -1){
                
                
                String Hex = bytesToHex(buffer, bytesRead).replaceAll(" ", "");
                
                String magicByte = Hex.substring(0,2);
                
                switch(magicByte){
                    
                    //Pacote de Login(User/Senha)(00)
                    case "00":
                        SendData(LoginLoob(Hex));
                        break;
                        
                    //Pacote de Seleção de personagem(0a)    
                    case "0A":
                        SendData(CharSelectOptions(Hex));
                        break;
                    
                    //Pacote de PingPong(02)
                    case "02":
                        SendData(PingPong(Hex));
                        break;
                    
                    //Pacote com dados do personagem(0D)
                    case "0D":
                        SendData(CharSpcsFinal(Hex));
                        break;
                    
                    //Pacote de criação de conta
                    case "01":
                        SendData(CreatAcc(Hex));
                        break;
                        
                    //pacote de Criação de personagem(0B)
                    case "0B":
                        SendData(CreatChar(Hex));
                }
                
                
                
                
                
            }
    

        }catch (IOException ex) {
            Logger.getLogger(Responce.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
    
    //Pacote de criação de personagem
    private String CreatChar(String Hex){
        
        if(Hex.substring(2,6).equals("0000")){
            return packer.Fail("O nome nao pode estar vazio!");
        }
        
        int stg1 = (Integer.valueOf(TradutorInt(2, 6, Hex, true))*2)+6;
        
        String stgNick = TradutorString(6, stg1, Hex);
        String stgCharSpec = Hex.substring(stg1,Hex.length());
        
        chara.setAccId(conta.getAccId());
        chara.setCharName(stgNick);
        chara.setCharSpec(stgCharSpec);
       
        if(dao.CharNameVerify(chara)){
            return packer.Fail("Nome em uso, escolha outro!");
        }
                
        dao.RegisterChar(chara);
        
        return packer.CreatChar(Integer.valueOf(chara.getCharId()));
        
    }
    
    //Pacote de Login(User/Senha)(00)
    private String LoginLoob(String Hex){
         
        int stg1 = (Integer.valueOf(TradutorInt(2, 6, Hex, true))*2)+6;
        int stg2 = (Integer.valueOf(TradutorInt(stg1, stg1+4, Hex, true))*2)+4+stg1;

        int t1 = Integer.valueOf(TradutorInt(2, 6, Hex, true));
        int t2 = Integer.valueOf(TradutorInt(stg1, stg1+4, Hex, true));
        
        if(t1 <= 0 || t2 <= 0){
            return "00";
        }
        
        String login = TradutorString(6, stg1, Hex);
        String senha = TradutorString(stg1+4, stg2, Hex);
        
        conta.setAccName(login);
        conta.setAccPass(senha);
        
        boolean vall = dao.Login(conta);
        
        if(vall){
            return packer.LoginLoob(conta.getAccTeamHash(),conta.getAccTeamName(),conta.getAccId());
        }else{
            return "00";
        }
        
       
        
    }
    //Pacote de Seleção de personagem(0a)
    private String CharSelectOptions(String Hex){
        
        ArrayList<Char> charas = dao.GetAccChar(conta);
        
        if(charas.size() > 0){
            return packer.CharSelectOptions(charas);
        }else{
            return "0000";
        }
        
    }  
    
    //Pacote de Confirmação de selecção de personagem(02?)
    private String PingPong(String Hex){
        return packer.PingPong();
    }
    
    //Pacote de login no personagem(envia conteudo)(0D)
    private String CharSpcsFinal(String Hex){
        
        String charId = TradutorInt(2, Hex.length(), Hex, true);
        
        String md5HashLoginGame = DigestUtils.md5Hex(charId);
        
        chara.setCharId(charId);        
        dao.GetCharInfo(chara);
        
        return packer.CharSpcsFinal(charId,"game.pwn3", 3001, md5HashLoginGame, chara.getCharName(), conta.getAccTeamName(),chara.getCharStatus());
    }
    
    private String CreatAcc(String Hex){

        int posAcc = (Integer.valueOf(TradutorInt(2, 6, Hex, true))*2)+6;
        int posTeamHash = (Integer.valueOf(TradutorInt(posAcc, posAcc+4, Hex, true))*2)+4+posAcc;
        int pospass = (Integer.valueOf(TradutorInt(posTeamHash, posTeamHash+4, Hex, true))*2)+4+posTeamHash;

        if(Hex.substring(2,6).equals("0000")){
            return packer.Fail("O nome da CONTA nao pode estar em branco!");
            
        }else if (Hex.substring(posAcc,posAcc+4).equals("0000")){
            return packer.Fail("O nome do TIME nao pode estar em branco!");
            
        }else if (Hex.substring(posTeamHash,posTeamHash+4).equals("0000")){
            return packer.Fail("A SENHA nao pode estar em branco!");
            
        }
        
        String accName = TradutorString(6, posAcc, Hex);
        String TeamName = TradutorString(posAcc+4, posTeamHash, Hex);
        String accpass = TradutorString(posTeamHash+4, pospass, Hex);

        if(!ValidadorDados(accName)){
            return packer.Fail("Nome da CONTA possui caracteres invalidos!");
            
        }else if(!ValidadorDados(TeamName)){
            return packer.Fail("Nome do TIME possui caracteres invalidos!");
            
        }
        
        String md5HashTeam = DigestUtils.md5Hex(TeamName);
        
        conta.setAccTeamHash(md5HashTeam);
        conta.setAccTeamName(TeamName);
        conta.setAccName(accName);
        conta.setAccPass(accpass);
        
        
        if(dao.AccNameVerify(conta)){
            return packer.Fail("Nome da CONTA ja em uso, escolha outro!");
            
        }else if(dao.TeamNameVerify(conta)){
            return packer.Fail("Nome do TIME ja em uso, escolha outro!");
        }

        dao.RegisterAcc(conta);

        return packer.CreatAcc(conta.getAccName(), conta.getAccTeamName(), Integer.valueOf(conta.getAccId()));
        
    }
    
    
    //=====================================================================================================================
    
    //Conversão basicas
   private String TradutorInt(int inicio, int fim, String Hex, boolean big){
        String result = "";
        
        if(inicio != 0 && fim != 0){
            result = Hex.substring(inicio, fim);
        }else{
            result = Hex;
        }
        
        if(big){
           result = ReverseString(result); 
        }
        
        int i = Integer.parseInt(result, 16);
        result = String.valueOf(i);
        return result;
   }
   
   private String TradutorString(int inicio, int fim, String Hex){
        String result = "";
        
        result = Hex.substring(inicio, fim);
        result = new String(new BigInteger(result, 16).toByteArray());
        return result;
   }
   //Separador de pacote (separa de 2 em 2 bytes)
   private String DataSplit(String Hex){
       String result = ""; 
       
        if(Hex.length() % 4 == 0){
            for(int i = 4; i <= Hex.length(); i += 4){
                result = result + " "+Hex.substring(i-4,i);

            }
         }else if(Hex.length() % 2 == 0){
            for(int i = 2; i <= Hex.length(); i += 2){
                result = result + " "+Hex.substring(i-2,i);
            } 
         }else{
             result = Hex;  
         }
       
       return result.trim();
       
   }
   //Inversor de String
   private String ReverseString(String Hex){
        String result = "";

        for (int i = Hex.length() - 3; i >= -2; i-=2) {
            result += Hex.charAt(i+1);
            result += Hex.charAt(i+2);
        }

        return result;
   }
    
    private static String bytesToHex(byte[] bytes, int length) {
       StringBuilder sb = new StringBuilder();

       for (int i = 0; i < length; i++) {
           sb.append(String.format("%02X ", bytes[i]));
       }

       return sb.toString().trim();
   }
    
    private void SendData(String Hex){
        byte[] bytes = HexFormat.of().parseHex(Hex);
        int bytesTotal = bytes.length;
        try {
            
            
            outputStream.write(bytes, 0, bytesTotal);
        } catch (IOException ex) {
            Logger.getLogger(Responce.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private String StringToHex(String text){
   
        byte[] bytes = text.getBytes();
        BigInteger bigInteger = new BigInteger(1, bytes);
        String hexadecimal = bigInteger.toString(16);
        
        return hexadecimal;
        
    }
    public static boolean ValidadorDados(String str) {
        
        return Pattern.matches("[a-zA-Z0-9]+", str);
    }
}
