import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private int clientId;

    public ClientHandler(Socket socket, int clientId) {
        this.clientSocket = socket;
        this.clientId = clientId;

        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getClientId() {
        return clientId;
    }

    @Override
    public void run() {
        try {
            // İstemciye bağlandığı bilgisini gönder
            out.println("Bağlantı başarıyla kuruldu. Cihaz: " + clientId);

            while (true) {
                String clientMessage = in.readLine();

                if (clientMessage == null || "exit".equalsIgnoreCase(clientMessage)) {
                    System.out.println("İstemci ayrıldı: " + clientId);
                    Sunucu.broadcastMessage("İstemci ayrıldı: " + clientId, this);
                    break;
                }

                System.out.println("İstemci (" + clientId + "): " + clientMessage);

                // Her mesajın ardından yarım saniye bekleyin
                Thread.sleep(500);

                // Tüm istemcilere sıradaki cihazın mesajını gönder
                Sunucu.broadcastMessage(clientId + ". cihazdan mesaj alındı: " + clientMessage, this);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
