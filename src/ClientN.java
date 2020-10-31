import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ClientN {
    private  static final int    serverPort = 8082;
    private  static final String localhost  = "127.0.0.1";
    public static void main(String[] ar)
    {

        System.out.println("Введите имя клиента:");
        final Scanner readline = new Scanner(System.in);
        String name = readline.nextLine();
        Socket socket = null;
        try {
            try {
                System.out.println("Добро пожаловать " + name + "\n\t " +
                        "Соединение с сервером\n\t" +
                        "(IP address " + localhost +
                        ", port " + serverPort + ")");
                InetAddress ipAddress;
                ipAddress = InetAddress.getByName(localhost);
                socket = new Socket(ipAddress, serverPort);
                System.out.println(
                        "Соединение установлено.");
                // Получаем входной и выходной потоки
                // сокета для обмена сообщениями с сервером
                InputStream  socketClientInputStream  = socket.getInputStream();
                OutputStream socketClientOutputStream = socket.getOutputStream();

                DataInputStream  in ;
                DataOutputStream out;
                in  = new DataInputStream (socketClientInputStream );
                out = new DataOutputStream(socketClientOutputStream);

                InputStreamReader streamReader;
                streamReader = new InputStreamReader(System.in);
                BufferedReader keyboard;
                keyboard = new BufferedReader(streamReader);
                String line = null;
                System.out.println("Вводите сообщение и нажмите enter");
                System.out.println();
                while (true) {
                    line = keyboard.readLine();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                    out.writeUTF(name +  " [" + simpleDateFormat.format(new Date())+ "]" + ": " +line);
                    out.flush();
                    line = in.readUTF();

                    if (line.endsWith("EndWork")) {
                        break;
                    }
                    else {
                        System.out.println("Сервер отправил мне :\n\t" + line);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}