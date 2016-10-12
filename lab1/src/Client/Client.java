package Client;

import Commons.Encoding;
import DTO.Step1;
import DTO.Step2A;
import com.google.gson.Gson;

import javax.swing.*;

import static Commons.Commons.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

import static java.lang.StrictMath.pow;

public class Client {
   public static Integer p =23;
   public static Integer g = 5;
   public static Integer a;
   private static Integer A;
   private static Integer B;
   public static void main(String args[]) {
      try {
         Socket skt = new Socket("localhost", 9000);

         BufferedReader bufIn = new BufferedReader( new InputStreamReader( skt.getInputStream(),"UTF-8" ) );
         BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( skt.getOutputStream(),"UTF-8" ) );
         Random generator = new Random();
         a=generator.nextInt(40);
         step1(writer);
         step2(writer,bufIn);

      }
      catch(Exception e) {
         System.out.print("Whoops! It didn't work!\n");
      }
   }
   public static void step1(BufferedWriter writer){
      Step1 step1 = new Step1(p,g);
      Gson gson = new Gson();
      writeJson(writer,gson.toJson(step1));
   }
   public static void step2(BufferedWriter writer, BufferedReader reader){
      Step2A step2A=new Step2A(((int)pow(p,a))%g);
      Gson gson = new Gson();
      writeJson(writer,gson.toJson(step2A));
      String msg = readJson(reader);
      System.out.println("Received : "+msg);
   }

   
}
