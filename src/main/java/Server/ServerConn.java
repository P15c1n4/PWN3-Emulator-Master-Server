package Server;

import javax.net.ssl.*;
import java.security.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;

public class ServerConn {
    static Packer packer = new Packer();
    
    private static final String KEYSTORE_PATH = "./keystore.jks";
    private static final String KEYSTORE_PASSWORD = "123456";
    private static final int PORT = 3333;
    
    public static void main(String[] args) {
        try {
            // Carrega o arquivo de keystore
            KeyStore keyStore = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream(KEYSTORE_PATH);
            keyStore.load(fis, KEYSTORE_PASSWORD.toCharArray());

            // Configura o gerenciador de chaves
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, KEYSTORE_PASSWORD.toCharArray());

            // Configura o contexto SSL
            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            // Cria o servidor socket
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(PORT);

            // Aguarda e trata as conexões
            System.out.println("Aguardando conexão na porta:"+PORT+"...");
            System.out.println("$Server Ready$");
            while (true) {
                SSLSocket socket = (SSLSocket) serverSocket.accept();
                
                socket.startHandshake();
                System.out.println("Cliente:"+socket.getRemoteSocketAddress()+" conectado!");

                Responce responce = new Responce(socket.getInputStream(),socket.getOutputStream());
                responce.start();

            }
        } catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException |
                UnrecoverableKeyException | KeyManagementException e) {
            e.printStackTrace();
        }
    }
}