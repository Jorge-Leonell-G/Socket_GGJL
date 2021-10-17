package server;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/**
 *
 * @author leone
 */

//Para este juego, vamos a utilizar la libreria socket, que se basa en el protocolo TCP mayormente

//adios al UDP
public class Servidor {
    //inicializacion del puerto a manera de una constante
    private final int puerto = 56076;
    
    //Lista de sockets para el almacenamiento de los sockets de los jugadores
    private LinkedList<Socket> jugadores = new LinkedList<Socket>();
    
    //establecemos variables que definan algunas reglas del juego
    
    //conexiones maximas en el server
    private int connectMax = 2;
    //turnos
    private boolean turno = true;
    
    //array bidimensional (matriz) para almacenar los movimientos
    private int M[][] = new int[3][3];
    
    private int turnos = 1;
    
    public void recibir(){
        try{
            //Inicializamos la matriz del juego con -1
            for (int i = 0; i < 3; i++){
                for (int j = 0; j < 3; j++){
                    M[i][j] = -1;
                }
            }
            //Creacion del Socket para nuestro servidor
            ServerSocket servidor = new ServerSocket(puerto, connectMax);
            //Ciclo infinito para estar escuchando por nuevos jugadores
            System.out.println("Esperando a los jugadores....");
            while(true){
                //Cuando un jugador se conecte guardamos el socket en nuestra lista
                    Socket cliente = servidor.accept();
                    //Se agrega el socket a la lista
                    jugadores.add(cliente);
                    //Se le genera un turno X o O 
                    int xo = turnos % 2 == 0 ? 1 : 0;
                    turnos++;
                    //Instanciamos un hilo que estara atendiendo al cliente y lo ponemos a escuchar
                    Runnable run = new ServidorHilo(cliente, jugadores, xo, M);
                    Thread hilo = new Thread(run);
                    hilo.start();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
        //Funcion main para correr el servidor
        public static void main(String[] args) {
        Servidor servidor= new Servidor();
        servidor.recibir();
    
    }
}
