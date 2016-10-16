package Client;

import Commons.Encoding;
import DTO.Control;
import DTO.Message;
import DTO.Step1;
import DTO.Step2A;
import com.google.gson.Gson;
import com.sun.corba.se.spi.orbutil.fsm.Input;

import javax.swing.*;

import static Commons.Commons.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Random;

import static java.lang.StrictMath.pow;

public class Client {
   public static Integer p =23;
   public static Integer g = 5;
   public static Integer a;
   private static Integer A;
   private static Integer B;
    private static Encoding encoding;
   public static void main(String args[]) {
      try {
         Socket skt = new Socket("localhost", 9000);

         InputStream input = skt.getInputStream();
         OutputStream output = skt.getOutputStream();
         Random generator = new Random();
         a=generator.nextInt(40);
         step1(output);
         step2(output,input);
         createGUI(output);
      }
      catch(Exception e) {
         System.out.print("Whoops! It didn't work!\n");
      }
   }
   public static void step1(OutputStream output){
      Step1 step1 = new Step1(p,g);
      Gson gson = new Gson();
      writeJson(output,gson.toJson(step1));
   }
   public static void step2(OutputStream output, InputStream input)throws SocketException{
      Step2A step2A=new Step2A(((int)pow(p,a))%g);
      Gson gson = new Gson();
      writeJson(output,gson.toJson(step2A));
      String msg = readJson(input);
      System.out.println("Received : "+msg);
   }

   public static void createGUI(OutputStream output){
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
            Control control = new Control(((Encoding)encodingComboBox.getSelectedItem()).toString());
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
             Message message = new Message(msg.getText(),from.getText());
             writeJson(output,new Gson().toJson(message));
             msg.setText("");

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

   private static String encodeMsg(Encoding encoding,String msg){
       if(encoding.equals(Encoding.cezar)){

       }
        return msg;
   }
}
