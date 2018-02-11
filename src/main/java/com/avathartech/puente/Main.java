package com.avathartech.puente;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    static String host = "http://localhost:8080";

    public static void main(String[] args) throws Exception {
        if(args.length >= 1){
            host = args[0];
            System.out.println("Cambiando la URL =  "+host);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    servidorKeepAlive();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    servidorEntradaDinero();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();



    }

    private static void servidorKeepAlive() throws Exception {
        try (ServerSocket server = new ServerSocket(9900)) {
            System.out.println("Subiendo el servidor KeepAlive puerto: "+ server.getLocalPort());
            while (true) {
                final Socket accept = server.accept();
                System.out.println("Cliente: "+ accept.getInetAddress().getHostAddress());
                Thread hilo = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            BufferedReader br = new BufferedReader(new InputStreamReader(accept.getInputStream()));
                            String linea = br.readLine();
                            System.out.println("Imprimiendo linea: " + linea);
                            accept.close();
                            enviarTramaKeepServidor(linea);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                hilo.start();
            }
        }
    }

    private static void  servidorEntradaDinero() throws Exception {
        try (ServerSocket server = new ServerSocket(9901)) {
            System.out.println("Subiendo el servidor Entrada Dinero puerto:"+ server.getLocalPort());
            while (true) {
                final Socket accept = server.accept();
                System.out.println("Cliente: "+ accept.getInetAddress().getHostAddress());
                Thread hilo = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            BufferedReader br = new BufferedReader(new InputStreamReader(accept.getInputStream()));
                            String linea = br.readLine();
                            System.out.println("Imprimiendo linea: " + linea);
                            accept.close();
                            enviarTramaDinero(linea);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                hilo.start();
            }
        }
    }

    private static void enviarTramaKeepServidor(String trama) throws Exception{
        HttpResponse<String> stringHttpResponse = Unirest.post(host+"/api/keepAlive")
                .body(trama)
                .asString();
        System.out.println("Codigo: "+ stringHttpResponse.getStatus());
        System.out.println("Respuesta: "+ stringHttpResponse.getBody());


    }

    private static void enviarTramaDinero(String trama) throws Exception{
        HttpResponse<String> stringHttpResponse = Unirest.post(host+"/api/entradaSalidaDinero")
                .body(trama)
                .asString();
        System.out.println("Codigo: "+ stringHttpResponse.getStatus());
        System.out.println("Respuesta: "+ stringHttpResponse.getBody());


    }

}
