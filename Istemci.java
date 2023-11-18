import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Istemci {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 12345;
    private static int clientId;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Sunucuya bağlandı.");

            // İstemciye verilen kimliği al
            clientId = Integer.parseInt(in.readLine());
            System.out.println("Istemci numarası: " + clientId);

            while (true) {
                // Sıradaki cihaz olduğunu kontrol et
                String serverResponse = in.readLine();
                System.out.println("Sunucu: " + serverResponse);

                // Mesajı gönder
                out.println(clientId + ". cihazdan mesaj alındı");

                // Bir sonraki cihazı bekleyin
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
