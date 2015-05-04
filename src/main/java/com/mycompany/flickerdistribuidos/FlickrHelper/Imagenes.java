/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.flickerdistribuidos.FlickrHelper;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.groups.Group;
import com.flickr4java.flickr.groups.pools.PoolsInterface;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.upload.Ticket;
import com.flickr4java.flickr.photos.upload.UploadInterface;
import com.flickr4java.flickr.photosets.Photoset;
import com.flickr4java.flickr.photosets.PhotosetsInterface;
import com.flickr4java.flickr.prefs.PrefsInterface;
import com.flickr4java.flickr.uploader.UploadMetaData;
import com.flickr4java.flickr.uploader.Uploader;
import com.mycompany.flickerdistribuidos.Main;
import com.urjc.java.pruautorizacionesflickr.AutorizacionesFlickr;
import java.awt.TextArea;
import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author Julio
 */
public class Imagenes {

    Flickr flickr;
    Set<String> photosIds = new HashSet<>(); //Para los tickets
    AutorizacionesFlickr autorizacionesFlickr = new AutorizacionesFlickr();
    private boolean finish = false;

    ArrayList<String> ids = new ArrayList<>();

    private int size = 0;

    public Imagenes(Flickr f) {
        flickr = f;
    }

    public String setContentType(int p) throws FlickrException {
        switch (p) {
            case 0:
                return Flickr.CONTENTTYPE_PHOTO;
            case 1:
                return Flickr.CONTENTTYPE_SCREENSHOT;
            case 2:
                return Flickr.CONTENTTYPE_OTHER;
            default:
                PrefsInterface prefi = flickr.getPrefsInterface();
                return prefi.getContentType();
        }
    }

    public void setPrivacy(int p, UploadMetaData m) {
        switch (p) {
            case 0:
                m.setPublicFlag(true);
                break;
                
            case 1:
                m.setPublicFlag(false);
                m.setFamilyFlag(false);
                m.setFriendFlag(true);
                break;
            case 2:
                m.setPublicFlag(false);
                m.setFriendFlag(true);
                m.setFamilyFlag(true);
                break;
            case 3:
                m.setPublicFlag(false);
                m.setFamilyFlag(true);
                m.setFriendFlag(true);
                break;
            case 4:
                m.setPublicFlag(false);
                m.setHidden(Boolean.TRUE);
                break;
            default:
                m.setPublicFlag(true);
        }
    }

    public String setSafetyLevel(int p) throws FlickrException {
        switch (p) {
            case 0:
                return Flickr.SAFETYLEVEL_SAFE;
            case 1:
                return Flickr.SAFETYLEVEL_MODERATE;
            case 2:
                return Flickr.SAFETYLEVEL_RESTRICTED;
            default:
                return Flickr.SAFETYLEVEL_MODERATE;
        }
    }

    //Al pasar los tags se pasan separados por comas.
    public ArrayList<String> SetTags(String tags) {
        ArrayList<String> resultado = new ArrayList<>();
        if (!tags.equals("")) {
            String[] lTag = tags.split(",");
            resultado = new ArrayList<>(Arrays.asList(lTag));
        } else {
            resultado.add("");
        }

        return resultado;
    }

    //TODO: CLASE PARA LAS FOTOS para poder separlo en file y metadata y meter asi titulos a cada imagen.
    public Set<String> Upload_photos(File[] files, int privacidad, int safety, int content, String tags) throws FlickrException {

        RequestContext rContext = RequestContext.getRequestContext();
        rContext.setAuth(autorizacionesFlickr.getAuth());
        size = files.length;
        for (final File f : files) {
            System.out.println("image: " + f.getName());
            ArrayList<String> tag = SetTags(tags);
            Uploader u = flickr.getUploader();
            UploadMetaData metadatos = new UploadMetaData();
            metadatos.setAsync(true);
            metadatos.setTitle(f.getName());
            metadatos.setContentType(setContentType(content));
            metadatos.setTags(tag);
            setPrivacy(privacidad, metadatos);
            metadatos.setSafetyLevel(setSafetyLevel(safety));
            String photoID = u.upload(f, metadatos);
            photosIds.add(photoID);//ids para comprobar despues.
        }

        return photosIds;
    }

    public int todosSubidos(List<Ticket> tickets) {
        int cont = 0;
        ids.clear();
        if (tickets != null) {
            for (Ticket t : tickets) {
                if (t.hasCompleted()) {
                    cont += 1;
                    ids.add(t.getPhotoId());
                }
            }
        }
        return cont;
    }

    public void ComprobarSubida() {
        Thread t = new Thread() {
            public void run() {
                RequestContext rContext = RequestContext.getRequestContext();
                rContext.setAuth(autorizacionesFlickr.getAuth());

                UploadInterface uploadInterface = flickr.getUploadInterface();

                try {
                    List<Ticket> tickets = uploadInterface.checkTickets(photosIds);
                    int completos = todosSubidos(tickets);

                    while (completos < tickets.size()) {
                        System.out.println("Progreso: " + completos + "ficheros subidos, " + (tickets.size() - completos) + "pendientes.");
                        sleep(3000);
                        completos = todosSubidos(uploadInterface.checkTickets(photosIds));
                        System.out.println("Progreso: " + completos + "ficheros subidos, " + (tickets.size() - completos) + "pendientes.");
                    }
                    finish = true;

                    //Hacemos algo con las imagenes.
                    //DEBUG
                } catch (FlickrException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Imagenes.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        t.start();

    }

    public void ComprobarSubida(JTextArea ta) {

        RequestContext rContext = RequestContext.getRequestContext();
        rContext.setAuth(autorizacionesFlickr.getAuth());

        UploadInterface uploadInterface = flickr.getUploadInterface();

        try {
            List<Ticket> tickets = uploadInterface.checkTickets(photosIds);
            int completos = todosSubidos(tickets);

            while (completos < size) {
                        //System.out.println("Progreso: " + completos + " ficheros subidos, " + (tickets.size() - completos) + " pendientes.");
                //ta.append("Progreso: " + completos + " ficheros subidos, " + (tickets.size() - completos) + " pendientes.\n");
                Thread.sleep(3000);
                completos = todosSubidos(uploadInterface.checkTickets(photosIds));
                System.out.println("Progreso: " + completos + " ficheros subidos, " + (size - completos) + " pendientes.");
                ta.append("Progreso: " + completos + " ficheros subidos, " + (size - completos) + " pendientes.\n");
                ta.repaint();

            }
            finish = true;
            System.out.println("Terminado");
            ta.append("Terminado");

                    //Hacemos algo con las imagenes.
            //DEBUG
        } catch (FlickrException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Imagenes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void publicarAlbum(String titulo, String descripcion) throws FlickrException {
        PhotosetsInterface psInterface = flickr.getPhotosetsInterface();
        PhotosInterface p = flickr.getPhotosInterface();
        String o = p.getInfo(ids.get(0), "").getId();
        Photoset ps = psInterface.create(titulo, descripcion, o);
        for (int i = 1; i < ids.size(); i++) {
            o = p.getInfo(ids.get(i), "").getId();
            psInterface.addPhoto(ps.getId(), o);
        }
        System.out.println("Album Creado");
    }

    public ArrayList<Group> getGrupos() throws FlickrException {
        PoolsInterface pools = flickr.getPoolsInterface();
        ArrayList<Group> grupos = new ArrayList<>();
        if (pools.getGroups() == null) {
            grupos = null;
        } else {
            grupos = (ArrayList< Group>) pools.getGroups();
        }
        return grupos;
    }

    public void publicarEnGrupo(Group grupo) throws FlickrException {
        PhotosInterface p = flickr.getPhotosInterface();
        PoolsInterface pools = flickr.getPoolsInterface();
        String o;
        for (int i = 0; i < ids.size(); i++) {
            o = p.getInfo(ids.get(i), "").getId();
            pools.add(o, grupo.getId());
        }
        Logger.getLogger(getClass().getName()).log(Level.INFO, "FOTOS AL GRUPO {0}", grupo.getName());
    }

    /**
     * @return the finish
     */
    public boolean isFinish() {
        return finish;
    }

}
