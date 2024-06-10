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
public class ServidorTCP {
    public static void main(String args[]) throws UnknownHostException, IOException {
        ServerSocket ss = new ServerSocket(7777);
        Socket s = ss.accept();
        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
        out.println("Bienvenido a la asignatura RMS");
        s.close();
        ss.close();
        
        ServerSocket ss2 = new ServerSocket(7777);
        System.out.println("Servidor listo y esperando conexiones...");
        Socket s2 = ss2.accept();
        InputStream is = s2.getInputStream();
        BufferedImage image = ImageIO.read(is);
        ImageIO.write(image, "PNG", new File("received.png"));
        s2.close();
        ss2.close();
    }
}
