/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.*;
import java.io.*;
import com.github.sarxos.webcam.Webcam;
import javax.imageio.ImageIO;

/**
 *
 * @author Inés Ruiz Sánchez
 */
public class ClienteTCP {
    public static void main(String args[]) throws UnknownHostException, IOException {
        Socket s = new Socket("127.0.0.1", 7777);
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        System.out.println(in.readLine());
        s.close();
        
        Socket s2 = new Socket("127.0.0.1", 7777);
        OutputStream os = s2.getOutputStream();
        Webcam webcam = Webcam.getDefault();
        webcam.open();
        ImageIO.write(webcam.getImage(), "PNG", os);
        webcam.close();
        s2.close();
    }
}
