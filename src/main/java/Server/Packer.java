package Server;

import Model.Char;
import java.math.BigInteger;
import java.util.ArrayList;

public class Packer {

    
    //Pacote login menssagem(login/senha)
    public String LoginMenssage(String title, String body){
        StringBuilder result = new StringBuilder();
        
        String lengthTitle = ReverseString(IntToHex(title.length(),"4"));
        String lengthBody = ReverseString(IntToHex(body.length(),"4"));
        
        result.insert(0, "50574e330500")
              .insert(result.length(), lengthTitle)
              .insert(result.length(), StringToHex(title))
              .insert(result.length(), lengthBody)
              .insert(result.length(), StringToHex(body));
        
        return result.toString();
    }
    
    //Pacote Loob(Server)
    public String LoginLoob(String idCode, String nick, String idAcc){
        StringBuilder result = new StringBuilder();
    
        String lengthCode = ReverseString(IntToHex(idCode.length(),"4"));
        String lengthNick = ReverseString(IntToHex(nick.length(),"4"));
        
        result.insert(0, "01"+ReverseString(IntToHex(Integer.valueOf(idAcc), "8")))
              .insert(result.length(),lengthCode)
              .insert(result.length(),StringToHex(idCode))
              .insert(result.length(),lengthNick)
              .insert(result.length(),StringToHex(nick))
              .insert(result.length(),"00");
       return  result.toString();
    }
    
    
    //Pacote de Seleção de personagem(Server)
    public String CharSelectOptions(ArrayList<Char> charas){
        StringBuilder result = new StringBuilder();
        
        result.insert(0, ReverseString(IntToHex(charas.size(), "4")));
              //.insert(result.length(), ReverseString(IntToHex(Integer.valueOf(charas.get(0).getCharId()), "8")));
        
        
        for(Char newChar : charas){
           String lengthnick = ReverseString(IntToHex(newChar.getCharName().length(),"4"));
           String lengthlocal = ReverseString(IntToHex(newChar.getCharLocal().length(),"4"));
           
           result.insert(result.length(), ReverseString(IntToHex(Integer.valueOf(newChar.getCharId()), "8")))
                 .insert(result.length(), lengthnick)
                 .insert(result.length(), StringToHex(newChar.getCharName()))
                 .insert(result.length(), lengthlocal)
                 .insert(result.length(), StringToHex(newChar.getCharLocal()))
                 .insert(result.length(), newChar.getCharSpec())
                 .insert(result.length(), "0000000000");
        }

                
        return result.toString();
        
    }
    
    //Pacoted e Confirmação de seleção de char
    public String PingPong(){
        StringBuilder result = new StringBuilder();
        
        result.insert(0, "0000000000000000");
        
        return result.toString();
    }
    
    //Pacote de envio de conteudo do personagem
    public String CharSpcsFinal(String charId, String dns, int port, String token, String nick, String team, String charStatus){
        StringBuilder result = new StringBuilder();
        
        String lengthDns = ReverseString(IntToHex(dns.length(),"4"));
        String portHex = ReverseString(IntToHex(port,"4"));
        String lengthToken = ReverseString(IntToHex(token.length(),"4"));
        String lengthNick = ReverseString(IntToHex(nick.length(),"4"));
        String lengthTeam = ReverseString(IntToHex(team.length(),"4"));
        
        result.insert(0, "0101")
              .insert(result.length(), lengthDns)
              .insert(result.length(), StringToHex(dns))
              .insert(result.length(), portHex)
              .insert(result.length(), lengthToken)
              .insert(result.length(), StringToHex(token))
              .insert(result.length(), lengthNick)
              .insert(result.length(), StringToHex(nick))
              .insert(result.length(), lengthTeam)
              .insert(result.length(), StringToHex(team))
              .insert(result.length(), "00")
              .insert(result.length(), charStatus);
//                      "010008004c6f7374436176650000000000000000"
//                      + "01001000477265617442616c6c734f6646697265"
//                      + "0100"
//                      + "00000000"
//                      + "00000000000000000000000000000000000000000000000000");

        return result.toString();
    }
            
     //Pacote de criação de conta
     public String CreatAcc(String accName, int newAccId, String teamHash){
         
         String md5Hash = teamHash;
        
         
         String idHex = ReverseString(IntToHex(newAccId,"8"));
         
         String lenTeamHash = ReverseString(IntToHex(md5Hash.length(),"4"));
         String lenTeamName =  ReverseString(IntToHex(accName.length(),"4"));
         
        StringBuilder result = new StringBuilder();
        
        result.insert(0, "01"+idHex)
              .insert(result.length(), lenTeamHash)
              .insert(result.length(), StringToHex(md5Hash))
              .insert(result.length(), lenTeamName)
              .insert(result.length(), StringToHex(accName))
              .insert(result.length(), "00");
         
        return result.toString();
     }
     
     //Pacote criação de personagem
     public String CreatChar(int newCharId){
         StringBuilder result = new StringBuilder();
         
         result.insert(0, "01"+ReverseString(IntToHex(newCharId, "8")));
         
         return result.toString();
         
     }
     public String Fail(String mensage){
         String lenStg = ReverseString(IntToHex(mensage.length(),"4"));
         String hexStg = StringToHex(mensage);
         
         return "00"+lenStg+hexStg;
     }       
            

    //Conversão basicas
    private String StringToHex(String text){
   
        byte[] bytes = text.getBytes();
        BigInteger bigInteger = new BigInteger(1, bytes);
        String hexadecimal = bigInteger.toString(16);
        
        return hexadecimal;
        
    }
    
    private String IntToHex(int num, String size){
        String hexadecimal = String.format("%0"+size+"X", num);
        
        return hexadecimal;
    }
    
    private String ReverseString(String Hex){
        String result = "";

        for (int i = Hex.length() - 3; i >= -2; i-=2) {
            result += Hex.charAt(i+1);
            result += Hex.charAt(i+2);
        }

        return result;
    }
}
