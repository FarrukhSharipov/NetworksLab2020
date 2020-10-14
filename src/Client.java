import java.net.*;
import java.io.*;

public class Client {
    private  static final int    serverPort = 8082;
    private  static final String localhost  = "127.0.0.1";
    private static final String name = "Farrukh";

    public static void main(String[] ar)
    {
        Socket socket = null;
        try{
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
                System.out.println("Вводите и нажмите enter");
                System.out.println();
                while (true) {
                    line = keyboard.readLine();
                    out.writeUTF(name + ": " + line);
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