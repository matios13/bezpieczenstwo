package Client;

import Commons.Encoding;
import DTO.*;
import DTO.Message;
import com.google.gson.Gson;

import javax.swing.*;

import static Commons.Commons.*;
import static Commons.Encode.decodeMsg;
import static Commons.Encode.encodeMsg;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Random;
/*
Klasa klienta z funkcją main
i GUI w singu (metoda createGUI)

 */
public class Client {
   public static BigInteger p ;
   public static BigInteger g ;
   public static BigInteger a;
    public static BigInteger A;
   private static BigInteger secret;
    private static Encoding encoding = Encoding.none;
    private static Gson gson = new Gson();

   public static void main(String args[]) {
      try {
         Socket skt = new Socket(HOST, PORT);
         InputStream input = skt.getInputStream();
         OutputStream output = skt.getOutputStream();
          System.out.println("Connected");
          generateMyAValue();
          System.out.println("Generated A");
         sendRequestForKeys(output,input);;
         Step2B step2B = sendMyAValueAndWaitForB(output,input);
          calculateSecret(step2B);
         createGUI(output,input);
      }
      catch(Exception e) {
         System.out.print("Whoops! It didn't work!\n");
          e.printStackTrace();
      }
   }

    private static void calculateSecret(Step2B step2B) {
        secret=step2B.getB().modPow(a,p);
        System.out.println("Secret u clienta: "+secret);
    }

    private static void generateMyAValue() {
        Random generator = new Random();
        a= BigInteger.valueOf(generator.nextInt(40));
    }

    public static void sendRequestForKeys(OutputStream outputStream, InputStream inputStream){
       Step0 step0 = new Step0("keys");
       try {
           String receivedJson=readJsonAndSendOne(inputStream,outputStream, new Gson().toJson(step0));
           System.out.println(receivedJson);
           Step1 step1 =  new Gson().fromJson(receivedJson,Step1.class);
           p=step1.getP();
           g=step1.getG();
           System.out.println("DOSTALEM P "+p+" G : " +g);
       } catch (SocketException e) {
           e.printStackTrace();
       }
   }

   public static Step2B sendMyAValueAndWaitForB(OutputStream output, InputStream input)throws SocketException{
      Step2A step2A=new Step2A(g.modPow(a,p));
       A=step2A.getA();

      String msg = readJsonAndSendOne(input,output,gson.toJson(step2A));
       Step2B step2B = gson.fromJson(msg, Step2B.class);
      System.out.println("Received : "+msg);
       return step2B;
   }

 


   private static String writeAndReadMessage(Message message,InputStream input,OutputStream output){
       try{
           String json = readJsonAndSendOne(input,output,new Gson().toJson(message));
           Message receivedMessage = new Gson().fromJson(json,Message.class);
           receivedMessage.setMsg(decodeMsg(encoding,receivedMessage.getMsg(),secret.intValue()));
           if(receivedMessage.getMsg().isEmpty()) {
               return "nic nie odebrano";
           }
            return receivedMessage.toString();
       }catch (Exception e){
           e.printStackTrace();
           return "wystąpił błąd";
       }

   }

    //----------------------------------------------GUI-------------------------------------------------------------

    public static void createGUI(OutputStream output, InputStream input){
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
        encodingComboBox.addActionListener(a-> sendControlMessageFromSelectedItem(encodingComboBox, output));

        //Message
        JPanel msgPanel = new JPanel();
        JTextArea msg = createMessagePanel(msgPanel);
        //FROM
        JPanel configuration = new JPanel();
        createBottomPane(output, input, msg, configuration);


        JSplitPane splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                msgPanel,configuration);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                encodingComboBox,splitPane1);

        //FRAME
        JFrame f = new JFrame("Centrala");
        f.setPreferredSize(new Dimension(420,600));
        f.add(splitPane);
        f.pack();
        f.setVisible(true);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


    }

    private static void createBottomPane(final OutputStream output, final InputStream input, final JTextArea msg, JPanel configuration) {
        configuration.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("From"),
                        BorderFactory.createEmptyBorder(2,2,2,2)),
                configuration.getBorder()));
        JTextField from = new JTextField();
        from.setVisible(true);
        from.setPreferredSize(new Dimension(75,50));

        configuration.add(from,BorderLayout.CENTER);
        JButton save = createSaveButton(output, input, msg, from);
        configuration.add(save,BorderLayout.EAST);
        configuration.setPreferredSize(new Dimension(1,1));
    }

    private static JButton createSaveButton(OutputStream output, InputStream input, JTextArea msg, JTextField from) {
        JButton save = new JButton("Send");
        save.addActionListener(a->msg.setText(
                writeAndReadMessage(
                        new Message(encodeMsg(encoding,msg.getText(),secret.intValue()),from.getText()),
                        input,
                        output)
        ));
        save.setVisible(true);
        return save;
    }

    private static JTextArea createMessagePanel(JPanel msgPanel) {
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

        msgPanel.add(areaScrollStatusPane);
        msgPanel.setPreferredSize(new Dimension(400,400));
        return msg;
    }

    private static void sendControlMessageFromSelectedItem(JComboBox encodingComboBox, OutputStream output) {
        Control control = new Control(((Encoding)encodingComboBox.getSelectedItem()));
        encoding=control.getEncoding();
        writeJson(output,new Gson().toJson(control));
    }
}
