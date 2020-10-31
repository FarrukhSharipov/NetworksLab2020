import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server extends Thread {
    private static int port = 8082;
    private String nameClient = "";
    private String string = "Я получил от клиента смс от клиента " + nameClient;
    private String string1 = "Клиент" + nameClient +  " закрыл доступ";
    private Socket socket;
    private int num;


    public Server() {
    }

    public void setSocket(int num, Socket socket) {
        this.num = num;
        this.socket = socket;
        setDaemon(true);
        setPriority(NORM_PRIORITY);
        start();
    }

    public void run() {
        try {
            InputStream socketInputStream = socket.getInputStream();
            OutputStream socketOutputStream = socket.getOutputStream();

            DataInputStream dataInputStream = new DataInputStream(socketInputStream);
            DataOutputStream dataOutputStream = new DataOutputStream(socketOutputStream);

            String line = null;
            while (true) {
                // Ожидание сообщения от клиента
                line = dataInputStream.readUTF();
                System.out.println(
                        String.format(string, num) + line);
                System.out.println("Я отправляю обратно...");
                // Отсылаем клиенту обратно эту самую
                // строку текста

                dataOutputStream.writeUTF("Сервер получил сообщение от клиента " + nameClient + line);
                // Завершаем передачу данных
                dataOutputStream.flush();
                System.out.println();


                // Вывод текущей даты и времени с использованием toString()
                if (line.equalsIgnoreCase("конец")) {
                    // завершаем соединение
                    socket.close();
                    System.out.println(
                            String.format(string1, num));
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Exception : " + e);
        }
    }

    public static void main(String[] ar) {
        ServerSocket serverSocket = null;
        try {
            try {

                int i = 0;
                // Подключение сокета к localhost
                InetAddress ia;
                ia = InetAddress.getByName("localhost");
                System.out.println(ia);
                serverSocket = new ServerSocket(port, 0, ia);

                System.out.println("Старт сервера\n\n");

                while (true) {
                    // ожидание подключения
                    Socket socket = serverSocket.accept();
                    System.err.println("ClientN accepted");
                    // Стартуем обработку клиента
                    new Server().setSocket(i++, socket);
                }
            } catch (Exception e) {
                System.out.println("Исключения : " + e);
            }
        } finally {
            try {
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }
}