package Server;

import java.io.BufferedReader;
import javax.net.ssl.*;
import java.security.*;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

public class ServerConn {
    static Packer packer = new Packer();
    
    private static Map config;
    
    public static void main(String[] args) {
        try {
            if(args.length > 0 && args[0].equals("/?")){
                System.out.println("\nUso: server-xx.exe [caminho do arquivo de configuração (ServerConfig.prop)] ou vazio para ./ServerConfig.prop");
                return;
            }else if(args.length > 0)
                config = ServerConfigRead(args[0]);
            else{
                config = ServerConfigRead("./ServerConfig.prop");
            }

            // Carrega o arquivo de keystore
            KeyStore keyStore = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream(config.get("KeyStore").toString().replaceAll("\\\\", "\\\\\\\\"));
            keyStore.load(fis, config.get("KeyStorePass").toString().toCharArray());

            // Configura o gerenciador de chaves
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, config.get("KeyStorePass").toString().toCharArray());

            // Configura o contexto SSL
            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            // Cria o servidor socket
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(Integer.parseInt(config.get("ServerPort").toString()));

            // Aguarda e trata as conexões
            System.out.println("Aguardando conexão na porta:"+Integer.parseInt(config.get("ServerPort").toString())+"...");
            System.out.println("$Server Ready$");
            
            while (true) {
                SSLSocket socket = (SSLSocket) serverSocket.accept();
                
                socket.startHandshake();
                System.out.println("Cliente:"+socket.getRemoteSocketAddress()+" conectado!");

                Responce responce = new Responce(socket.getInputStream(),socket.getOutputStream(), config);
                responce.start();

            }
        } catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException |
                UnrecoverableKeyException | KeyManagementException e) {
            e.printStackTrace();
        }
    }
    
    
    public static Map ServerConfigRead(String filePath) {
        String ipAddress = null;
        Map<String, String> keyValueMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                
                if (parts[0] != null) {
                    
                    if(parts.length == 1){
                        keyValueMap.put(parts[0].trim(), "");
                    }else{
                        keyValueMap.put(parts[0].trim(), parts[1].trim());
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return keyValueMap;
    }
    
}