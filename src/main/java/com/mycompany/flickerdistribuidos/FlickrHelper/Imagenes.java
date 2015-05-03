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

    public Imagenes(Flickr f) {
        flickr = f;
    }

    public String setContentType(int p) throws FlickrException {
        switch (p) {
            case 1:
                return Flickr.CONTENTTYPE_PHOTO;
            case 2:
                return Flickr.CONTENTTYPE_SCREENSHOT;
            case 3:
                return Flickr.CONTENTTYPE_OTHER;
            default:
                PrefsInterface prefi = flickr.getPrefsInterface();
                return prefi.getContentType();
        }
    }

    public void setPrivacy(int p, UploadMetaData m) {
        switch (p) {
            case 1:
                m.setPublicFlag(true);
            case 2:
                m.setFriendFlag(true);
            case 3:
                m.setFamilyFlag(true);
            case 4:
                m.setFamilyFlag(true);
                m.setFriendFlag(true);
            case 5:
                m.setHidden(Boolean.TRUE);

            default:
                m.setPublicFlag(true);
        }
    }

    public String setSafetyLevel(int p) throws FlickrException {
        switch (p) {
            case 1:
                return Flickr.SAFETYLEVEL_SAFE;
            case 2:
                return Flickr.SAFETYLEVEL_MODERATE;
            case 3:
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
        for (Ticket t : tickets) {
            if (t.hasCompleted()) {
                cont += 1;
                ids.add(t.getPhotoId());
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
    
    public void ComprobarSubida(TextArea ta) {
       
                RequestContext rContext = RequestContext.getRequestContext();
                rContext.setAuth(autorizacionesFlickr.getAuth());

                UploadInterface uploadInterface = flickr.getUploadInterface();

                try {
                    List<Ticket> tickets = uploadInterface.checkTickets(photosIds);
                    int completos = todosSubidos(tickets);

                    while (completos < tickets.size()) {
                        System.out.println("Progreso: " + completos + " ficheros subidos, " + (tickets.size() - completos) + " pendientes.");
                        ta.append("Progreso: " + completos + " ficheros subidos, " + (tickets.size() - completos) + " pendientes.\n");
                        Thread.sleep(3000);
                        completos = todosSubidos(uploadInterface.checkTickets(photosIds));
                        System.out.println("Progreso: " + completos + " ficheros subidos, " + (tickets.size() - completos) + " pendientes.");
                        ta.append("Progreso: " + completos + " ficheros subidos, " + (tickets.size() - completos) + " pendientes.\n");
                        
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
