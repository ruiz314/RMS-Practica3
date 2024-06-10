/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.*;
import java.io.*;
import com.github.sarxos.webcam.Webcam;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
/**
 *
 * @author Inés Ruiz Sánchez
 */
public class ClienteUDP {
    public static void main(String[] args) throws IOException {
        int puerto=8767;
        String direccionServidor="127.0.0.1";
        // por si queremos especificar la dirección IP y el puerto por
        //la línea de comandos
        
        if(args.length==2){
            direccionServidor=args[0];
            puerto=Integer.parseInt(args[1]);
        }
        // Creamos un cliente
        new ClienteUDP(direccionServidor,puerto);
        
    }
    
    // Constructor de esta clase
    private ClienteUDP(String direccionServidor, int puerto) {
        try{
            // 1. Creamos el socket
            DatagramSocket soc = new DatagramSocket();
            try{
                //2. Enviamos un mensaje al servidor para pedir la imagen
                InetAddress dir = InetAddress.getByName(direccionServidor);//InetAddress dir = InetAddress.getByName("127.0.0.1");
                byte[] buf = "REQUEST_IMAGE".getBytes();

                DatagramPacket pa = new DatagramPacket(buf, buf.length,dir, puerto);//DatagramPacket pa = new DatagramPacket(buf, buf.length,dir, 7777);
                soc.send(pa);

                //3. Recibimos el número de datagramas que se va a recibir
                byte[] buffer = new byte[4]; // Suponiendo que el número de datagramas se envía en 4 bytes
                pa = new DatagramPacket(buffer, buffer.length);
                soc.receive(pa); // se bloquea hasta recibir un datagrama
                ByteArrayInputStream bais = new ByteArrayInputStream(pa.getData());
                DataInputStream dis = new DataInputStream(bais);
                int numDatagrams = dis.readInt();

                //4. Recibimos y almacenamos la información del servidor
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                for (int i = 0; i < numDatagrams; i++) {
                    pa = new DatagramPacket(new byte[1024], 1024);
                    soc.receive(pa);
                    baos.write(pa.getData(), 0, pa.getLength());
                }
                byte[] imageBytes = baos.toByteArray();
        
                //5. Creamos un stream de bytes ByteArrayInputStream para ir
                //leyendo la información recibida
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
                DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

                //6. Leemos el primer dato que es el número de filas
                int rows = dataInputStream.readInt();

                //7. Leemos el segundo dato que es el número de columnas
                int cols = dataInputStream.readInt();
                
                //8. Copiamos los pixeles recibidos a un objeto imagen
                BufferedImage image = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
                for (int y = 0; y < rows; y++) {
                    for (int x = 0; x < cols; x++) {
                        int rgb = dataInputStream.readInt();
                        image.setRGB(x, y, rgb);
                    }
                }
                //9. Representamos la imagen
                File outputfile = new File("received_image.png");
                ImageIO.write(image, "png", outputfile);
                
                //Cerrar el socket
                System.out.write(pa.getData());
                soc.close();
                
        
            }catch (IOException e) {
                e.printStackTrace();
            } //finally {
                //Cerrar el socket
                //System.out.write(pa.getData());
                //soc.close();
            //}
        }catch (SocketException e) {
            e.printStackTrace();
        }               
    }
}
