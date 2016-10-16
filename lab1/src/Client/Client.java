package Client;

import Commons.Encoding;
import DTO.*;
import DTO.Message;
import com.google.gson.Gson;

import javax.swing.*;

import static Commons.Commons.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Random;

public class Client {
   public static Integer p =23;
   public static Integer g = 5;
   public static BigInteger a;
    public static BigInteger A;
   private static BigInteger secret;
    private static Encoding encoding = Encoding.none;
   public static void main(String args[]) {
      try {
         Socket skt = new Socket("localhost", 9000);

         InputStream input = skt.getInputStream();
         OutputStream output = skt.getOutputStream();
         Random generator = new Random();
         a=BigInteger.valueOf(generator.nextInt(40));
         step1(output);
         Step2B step2B = step2(output,input);
          secret=step2B.getB().modPow(a,BigInteger.valueOf(p));
          System.out.println("Secret u clienta: "+secret);
         createGUI(output,input);
      }
      catch(Exception e) {
         System.out.print("Whoops! It didn't work!\n");
      }
   }
   public static void step1(OutputStream output){
      Step1 step1 = new Step1(BigInteger.valueOf(p),BigInteger.valueOf(g));
      Gson gson = new Gson();
      writeJson(output,gson.toJson(step1));
   }
   public static Step2B step2(OutputStream output, InputStream input)throws SocketException{
      Step2A step2A=new Step2A(BigInteger.valueOf(g).modPow(a,BigInteger.valueOf(p)));
       A=step2A.getA();
      Gson gson = new Gson();
      writeJson(output,gson.toJson(step2A));
      String msg = readJson(input);
       Step2B step2B = gson.fromJson(msg, Step2B.class);
      System.out.println("Received : "+msg);
       return step2B;
   }

   public static void createGUI(OutputStream output, InputStream input){
      //KONFIGURACJA
      JComboBox encodingComboBox = new JComboBox();
      Arrays.stream(Encoding.values()).forEach(encoding -> encodingComboBox.addItem(encoding));

      encodingComboBox.setVisible(true);
      encodingComboBox.setPreferredSize(new Dimension(100, 50));
      encodingComboBox.setBorder(
              BorderFactory.createCompoundBorder(
                      BorderFactory.createCompoundBorder(
                              BorderFactory.createTitledBorder("Encoding"),
                              BorderFactory.createEmptyBorder(2,2,2,2)),
                      encodingComboBox.getBorder()));
      encodingComboBox.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            Control control = new Control(((Encoding)encodingComboBox.getSelectedItem()));
             encoding=control.getEncoding();
            writeJson(output,new Gson().toJson(control));
         }
      });

      //STATUS


       JPanel msgPanel = new JPanel();
      JTextArea msg = new JTextArea();
      msg.setVisible(true);
      msg.setEditable(true);
       msg.setPreferredSize(new Dimension(400,400));
      JScrollPane areaScrollStatusPane = new JScrollPane(msg);
      areaScrollStatusPane.setVerticalScrollBarPolicy(
              JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      areaScrollStatusPane.setPreferredSize(new Dimension(400, 400));
      areaScrollStatusPane.setBorder(
              BorderFactory.createCompoundBorder(
                      BorderFactory.createCompoundBorder(
                              BorderFactory.createTitledBorder("Status"),
                              BorderFactory.createEmptyBorder(5,5,5,5)),
                      areaScrollStatusPane.getBorder()));
      //FROM
        msgPanel.add(areaScrollStatusPane);
        msgPanel.setPreferredSize(new Dimension(400,400));
       JPanel configuration = new JPanel();
       configuration.setBorder(BorderFactory.createCompoundBorder(
               BorderFactory.createCompoundBorder(
                       BorderFactory.createTitledBorder("From"),
                       BorderFactory.createEmptyBorder(2,2,2,2)),
               configuration.getBorder()));
       JTextField from = new JTextField();
       from.setVisible(true);
       from.setPreferredSize(new Dimension(75,50));

      configuration.add(from,BorderLayout.CENTER);
      JButton save = new JButton("Send");
      save.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             Message message = new Message(encodeMsg(encoding,msg.getText(),secret.intValue()),from.getText());
             msg.setText(writeAndReadMessage(message,input,output));

         }
      });
      save.setVisible(true);
      configuration.add(save,BorderLayout.EAST);
       configuration.setPreferredSize(new Dimension(1,1));
      //KONF+STATUS
       JSplitPane splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
               msgPanel,configuration);
       JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
              encodingComboBox,splitPane1);
      //LOGI

      //FRAME
      JFrame f = new JFrame("Centrala");
       f.setPreferredSize(new Dimension(420,600));
      f.add(splitPane);
      f.pack();
      f.setVisible(true);
      f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
   }


   private static String writeAndReadMessage(Message message,InputStream input,OutputStream output){
       writeJson(output,new Gson().toJson(message));
       try{
           String json = readJson(input);
           Message receivedMessage = new Gson().fromJson(json,Message.class);
           receivedMessage.setMessage(decodeMsg(encoding,receivedMessage.getMessage(),secret.intValue()));
           if(receivedMessage.getMessage().isEmpty()) {
               return "nic nie odebrano";
           }
            return receivedMessage.toString();
       }catch (Exception e){
           e.printStackTrace();
           return "wystąpił błąd";
       }

   }
}
