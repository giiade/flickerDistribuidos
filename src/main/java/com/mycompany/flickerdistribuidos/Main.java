/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.flickerdistribuidos;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.groups.Group;
import com.flickr4java.flickr.groups.pools.PoolsInterface;
import com.flickr4java.flickr.photos.PhotoSet;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.upload.Ticket;
import com.flickr4java.flickr.photos.upload.UploadInterface;
import com.flickr4java.flickr.photosets.Photoset;
import com.flickr4java.flickr.photosets.PhotosetsInterface;
import com.flickr4java.flickr.prefs.PrefsInterface;
import com.flickr4java.flickr.uploader.UploadMetaData;
import com.flickr4java.flickr.uploader.Uploader;
import com.mycompany.flickerdistribuidos.FlickrHelper.Imagenes;
import com.urjc.java.pruautorizacionesflickr.AutorizacionesFlickr;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JulioLopez
 */
public class Main {


    // array of supported extensions (use a List if you prefer)
    static final String[] EXTENSIONS = new String[]{
        "gif", "png", "bmp", "jpeg", "jpg" // and other formats you need
    };
    // filter to identify images based on their extensions
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };

    static Flickr flickr;

    static Set<String> pids = new HashSet<>();
    final static AutorizacionesFlickr autorizacionesFlickr
            = new AutorizacionesFlickr();

    public static void main(String[] args) {

       

        flickr
                = new Flickr(autorizacionesFlickr.getApi_key(),
                        autorizacionesFlickr.getSecret(),
                        new REST());
        RequestContext rContext = RequestContext.getRequestContext();
        rContext.setAuth(autorizacionesFlickr.getAuth());
        Imagenes subir = new Imagenes(flickr);

        /*userId = autorizacionesFlickr.getUserID();
         username = autorizacionesFlickr.getAuth().getUser().getUsername();
         System.out.println(userId + " . " + username);*/
        Scanner scanner = new Scanner(System.in);
        String sPath = null;
        Path path;
        File dir;

        System.out.println("Indique la carpeta de la que quiere obtener las imagenes");
        sPath = scanner.nextLine();
        path = Paths.get(sPath);

        if ((Files.exists(path)) && (Files.isDirectory(path))) {
            dir = path.toFile();
            System.out.println(path.getParent().toString());
            String photoID;
                      
            String tags = ("Mola,mazo,Practica,SD");
            
            try {
                Set<String> photosIds = subir.Upload_photos(dir.listFiles(IMAGE_FILTER), 1, 1, 1, tags);
                subir.ComprobarSubida();
                
                while(!subir.isFinish()){
                    //TIEMPO HASTA QUE TERMINE, SE HACE ASÍ PARA PODER PINTAR COSAS EN LA INTERFAZ, con interfaz este while desaparece.
                    System.out.print("");
                }
                subir.publicarAlbum("Prueba2", "Esto es la prueba");
                
                ArrayList<Group> grupos =  subir.getGrupos();
                System.out.println(grupos.toString());
                subir.publicarEnGrupo(grupos.get(0));
            } catch (FlickrException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            
                    
            
          
            /* for (final File f : dir.listFiles(IMAGE_FILTER)) {
             System.out.println("image: " + f.getName());
             ArrayList<String> tag = new ArrayList<>();
             tag.add("dibujo");
             Uploader u = flickr.getUploader();
             UploadMetaData metadatos = new UploadMetaData();
             metadatos.setAsync(true);
             metadatos.setContentType(Flickr.CONTENTTYPE_OTHER);
             metadatos.setTags(tag);
             metadatos.setSafetyLevel(Flickr.SAFETYLEVEL_SAFE);
             metadatos.setPublicFlag(true);
             metadatos.setTitle("BALLENA");
             try {
             photoID = u.upload(f, metadatos);
             pids.add(photoID);
             } catch (FlickrException ex) {
             Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
             }
             }

             Thread t = new Thread() {
             public void run() {
             RequestContext rContext = RequestContext.getRequestContext();
             rContext.setAuth(autorizacionesFlickr.getAuth());
             UploadInterface uploadInterface = flickr.getUploadInterface();
             String photoID = "";
             try {
             List<Ticket> tickets = uploadInterface.checkTickets(pids);
             while (!tickets.get(0).hasCompleted()) {
             System.out.println("COMPLETADO: " + tickets.get(0).hasCompleted());
             try {
             this.sleep(3000);
             tickets = uploadInterface.checkTickets(pids);
             photoID = tickets.get(0).getPhotoId();
             } catch (InterruptedException ex) {
             Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
             }

             }

             PhotosetsInterface psInterface = flickr.getPhotosetsInterface();
             ArrayList<String> meme = new ArrayList<>();
             meme.addAll(pids);
             PhotosInterface p = flickr.getPhotosInterface();
             String o = p.getInfo(photoID,"").getId();
             Photoset ps = psInterface.create("Hola", "Mola Mucho", o);
             psInterface.addPhoto(ps.getId(), o);
                        
             PoolsInterface pools = flickr.getPoolsInterface();
             if(pools.getGroups()== null){
             System.out.println("No perteneces a ningún grupo");
             }else{
             ArrayList<Group> grupos = (ArrayList < Group >) pools.getGroups();
             pools.add(o, grupos.get(0).getId());
                            
             }
                        
             //Hacemos algo con las imagenes.
             //DEBUG
             } catch (FlickrException ex) {
             Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
             }
             }
             };
             t.start();*/
        } else {
            System.out.println("No es carpeta");
        }

    }

    // dir.list() -> recuperaremos todos los ficheros que componen una carpeta en un arraylist o lo que queramos
    // Los contamos y los subiriamos de manera asincrona.
}
