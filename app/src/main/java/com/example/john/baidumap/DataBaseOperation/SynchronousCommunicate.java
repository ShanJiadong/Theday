package com.example.john.baidumap.DataBaseOperation;


import baseclass.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by android学习 on 2017/12/12.
 */

public class SynchronousCommunicate {
    public static void SendToServer(final User user, final String address, final int port){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Socket socket = new Socket(address, port);
                    ObjectOutputStream out = null;
                    out = new ObjectOutputStream(socket.getOutputStream());

                    out.writeObject(user);
                    out.writeObject(null);
                    out.flush();
                    socket.shutdownOutput();

                    ObjectInputStream in = null;
                    in = new ObjectInputStream(socket.getInputStream());

                    socket.close();

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void AcceptFromServer(final User user, final String address, final int port){
        Thread thread = new Thread(new Runnable() {
            User us;
            @Override
            public void run() {
                try{
                    Socket socket = new Socket(address, port);
                    ObjectOutputStream out = null;
                    out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject(null);
                    socket.shutdownOutput();

                    ObjectInputStream in = null;
                    in = new ObjectInputStream(socket.getInputStream());

                    us = (User) in.readObject();
                    if (us != null)
                        user.copy(us);

                    socket.close();

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
