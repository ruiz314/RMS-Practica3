/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 *
 * @author Inés Ruiz Sánchez
 */
public class ServidorUDP {
    public static void main(String[] args) throws IOException {
        int puerto=8767;
        // por si queremos especificar el puerto por la línea de comandos
        if(args.length>0){
            puerto=Integer.parseInt(args[0]);
        }
        
        // Creamos un objeto de esta clase, pasando como argumento el puerto donde debe escuchar
        new ServidorUDP(puerto);
    }
    
    

    // Constructor de esta clase
    private ServidorUDP(int puerto){
        try{
            // 1. Creamos el socket
            DatagramSocket soc = new DatagramSocket(puerto);
            System.out.println("Servidor listo y esperando solicitudes...");
            
            //2. Esperamos un mensaje
            while(true){
                try{
                    byte[] buffer = new byte[1024];
                    //3. Almacenamos dir. IP y puerto en el datagrama a enviar
                    DatagramPacket pa = new DatagramPacket(new byte[256], 256); 
                    soc.receive(pa);
                    
                    String received = new String(pa.getData(), 0, pa.getLength());
                    System.out.println("Solicitud recibida: " + received);
                    if (received.equals("REQUEST_IMAGE")) {
                        //Comprobar si el archivo existe
                        File imageFile = new File("received.png");
                        System.out.println("Ruta completa del archivo: "+ imageFile.getAbsolutePath());
                        if(!imageFile.exists() || !imageFile.isFile()){
                            System.err.println("El archivo image_to_send.png no existe o no es un archivo válido.");
                            continue;
                        }
                        
                        //4. Abrimos la cámara y capturamos una imagen
                        BufferedImage image = ImageIO.read(new File("received.png"));
                        if(image == null){
                            System.err.println("No se pudo leer la imagen.");
                            continue;
                        }
                        int rows = image.getHeight();
                        int cols = image.getWidth();
                
                        // Convertir la imagen a bytes
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        DataOutputStream dos = new DataOutputStream(baos);
                        dos.writeInt(rows);
                        dos.writeInt(cols);
                        for (int y = 0; y < rows; y++) {
                            for (int x = 0; x < cols; x++) {
                                dos.writeInt(image.getRGB(x, y));
                            }
                        }
                        byte[] imageBytes = baos.toByteArray();
                
                        // Dividir la imagen en datagramas
                        int numDatagrams = (int) Math.ceil(imageBytes.length / 1024.0);
                        dos = new DataOutputStream(new ByteArrayOutputStream(4));
                        dos.writeInt(numDatagrams);
                        //byte[] numDatagramsBytes = ((ByteArrayOutputStream) dos).toByteArray();
                        byte[] numDatagramsBytes = toBytes(numDatagrams); 
                        pa = new DatagramPacket(numDatagramsBytes, numDatagramsBytes.length, pa.getAddress(), pa.getPort());
                        soc.send(pa);

                        for (int i = 0; i < numDatagrams; i++) {
                            int start = i * 1024;
                            int length = Math.min(1024, imageBytes.length - start);
                            DatagramPacket pa = new DatagramPacket(imageBytes, start, length, pa.getAddress(), pa.getPort());
                            soc.send(pa);
                        }
                    }//If
                    
                }//fin segundo try
                catch (IOException e) {
                    e.printStackTrace();
                }
                
            }//While
        }catch (SocketException e){
            e.printStackTrace();
        }
        
    }//Fin Constructor
    /* 
        5. Copiamos la imagen a un stream (ByteArrayOutputStream)
        Para ello, añadimos primero el número de filas (getHeight),
        luego el número de columnas (getWidth) y por último los
        pixeles RGB (getRGB)
        6. Almacenamos en un array de bytes la información a
        transmitir
        7. Transmitimos el número de datagramas que se van a enviar
        8. Transmitimos los datagramas que contienen los datos de la
        imagen (se recomienda usar la función sleep entre cada
        envío) 
    */ 
        
        
        // se bloquea hasta que recibe un datagrama
        //p.setAddress(p.getAddress());
        //p.setPort(p.getPort());
        //s.send(p);
        //s.close();
    // Funcion que convierte un array de bytes a un entero
    public static int byteArrayToInt(byte[] b) {
        if (b.length == 4)
            return b[0] << 24 | (b[1] & 0xff) << 16 | (b[2] & 0xff) << 8 | (b[3] & 0xff);
        else if (b.length == 2)
            return 0x00 << 24 | 0x00 << 16 | (b[0] & 0xff) << 8 | (b[1] & 0xff);
        else if (b.length == 1)
            return b[0];
        else
            return 0;
        }
    }

    // Funcion que convierte un entero a bytes
    public static  byte[] toBytes(int i) {
        byte[] resultado = new byte[4];
        resultado[0] = (byte) (i >> 24);
        resultado[1] = (byte) (i >> 16);
        resultado[2] = (byte) (i >> 8);
        resultado[3] = (byte) (i /*>> 0*/);
        return resultado;
    }
}

