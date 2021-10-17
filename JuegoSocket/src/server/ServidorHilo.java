/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

/**
 *
 * @author leone
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.LinkedList;

//implementamos la clase Runnable para el uso de hilos mediante interfaz
public class ServidorHilo implements Runnable{
    
    //Declaramos las variables que utiliza el hilo para estar recibiendo y mandando mensajes
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    
    //Lista de los usuarios conectados al servidor
    private LinkedList<Socket> jugadores = new LinkedList<Socket>();
    
    //Varible para guardar que le toco al jugador X o O
    private int XO;
    //array bidimensional (matriz) para almacenar los movimientos
    private int M[][];
    //Turno
    private boolean turno;
    
    //Constructor que recibe el socket que atendera el hilo y la lista de los jugadores el turno y la matriz del juego
    public ServidorHilo(Socket sock, LinkedList players, int xo, int[][] Gato){
        socket = sock;
        jugadores = players;
        XO = xo;
        M = Gato;
    }

    @Override
    public void run() {
        //codigo logico dentro de la ejecucion de nuestro hilo
        try{
            //Inicializamos los canales de comunicacion y mandamos el turno a cada jugador
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            turno = XO == 1;
            String msg = "";
            msg += "Es turno de: " + (turno ? "X;":"O;");
            msg += turno;
            out.writeUTF(msg);
            
            //Ciclo infinito que estara escuchando por los movimientos de cada jugador
            //Cada que un jugador pone una X o O viene aca y le dice al otro jugador que es su turno
            while(true){
                //Leer los datos que se mandan cuando se selecciona un boton
                String recibidos = in.readUTF();
                String recibido [] = recibidos.split(";");
                
                /*
                    recibido[0] : fila del tablero
                    recibido[1] : columna del tablero
                
                */
                
                int f = Integer.parseInt(recibido[0]);
                int c = Integer.parseInt(recibido[1]);
                /*
                    Se guarda la jugada en la matriz
                    X : 1
                    O : 0
                
                */
                
                M[f][c] = XO;
                
                /*
                Se forma una cadena que se enviara a los jugadores, que lleva informacion del movimiento que se 
                acaba de hacer
                */
                String cad = "";
                cad += XO + ";";
                cad += f + ";";
                cad += c + ";";
                
                /*
                Se comprueba si alguien de los jugadores gano
                y si el tablero ya se lleno... En los dos casos se notifica a los jugadores para empezar de nuevo la partida
                */
                boolean ganador = win(XO);
                boolean completo = lleno();
                
                //validaciones
                if(!ganador && !completo){
                    cad += "No hay ganadores...";
                }
                else if(!ganador && completo){
                    cad += "¡Empate!";
                }
                else if(ganador){
                    vaciarMatriz();
                    cad += XO == 1 ? "X":"O";
                }
                
                //ciclo for each para un almacenamiento dinamico de los
                //datos almacenados en los sockets de los jugadores
                for(Socket jugador: jugadores){
                    out = new DataOutputStream(jugador.getOutputStream());
                    out.writeUTF(cad);
                }
            }
            
        }catch(Exception e){
            
            //En caso de que ocurran excepciones por si un jugador se desconectó del servidor
            for (int i = 0; i < jugadores.size(); i++){
                if(jugadores.get(i) == socket){
                    //si el valor de i es igual al socket
                    //simplemente removemos al jugador de la conexion
                    jugadores.remove();
                    break;
                }
            }
            //se reinicia el progreso de la partida
            vaciarMatriz();
        }
    }
    
    //Funcion para comprobar si algun jugador ha ganado
    public boolean win(int n){
        // evaluamos los 3 espacios o 'casos' a cumplirse para validar la existencia de una victoria
        for(int i = 0; i < 3; i++){
            boolean gano = true;
            for(int j = 0; j < 3; j++){
                gano = gano && (M[i][j] == n);
            }
            if(gano){
                return true;
            }
        }
        
        for (int i = 0; i < 3; i++){
            boolean gano = true;
            for (int j = 0; j < 3; j++){
                gano = gano && (M[i][j] == n);
            }
            if(gano){
                return true;
            }
        }
        
        if(M[0][0] == n && M[1][1] == n && M[2][2] == n)return true;
        
        return false;
        
    }
    
    //Funcion comprueba si el tablero ya esta lleno
    public boolean lleno(){
        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(M[i][j] == -1)return false;
            }
        }
        
        vaciarMatriz();
        return true;
    }
    
    //funcion para reinciar el tablero, o mejor dicho, la matriz del juego
    public void vaciarMatriz(){
        for(int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                M[i][j] = -1;
            }
        }
    }
    
    
}
