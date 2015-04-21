/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.flickerdistribuidos;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;
import com.urjc.java.pruautorizacionesflickr.AutorizacionesFlickr;

/**
 *
 * @author JulioLopez
 */
public class Main {
    // TODO: Reestructurar clase para elegir archivos y subirlos.
    //se tendrá que actualizar cada 3 segundos para mostrar estado de subida
    //LLamaremos a la clase de subida desde aquí.
    public static void main(String[] args) {

        String userId;
        String username;

        AutorizacionesFlickr autorizacionesFlickr
                = new AutorizacionesFlickr();
        Flickr flickr
                = new Flickr(autorizacionesFlickr.getApi_key(),
                        autorizacionesFlickr.getSecret(),
                        new REST());

        userId = autorizacionesFlickr.getUserID();
        username = autorizacionesFlickr.getAuth().getUser().getUsername();
        System.out.println(userId + " . " + username);

    }


    // dir.list() -> recuperaremos todos los ficheros que componen una carpeta en un arraylist o lo que queramos
    // Los contamos y los subiriamos de manera asincrona.
    
}
