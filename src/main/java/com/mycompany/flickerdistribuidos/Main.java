/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.flickerdistribuidos;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.RequestContext;
import com.urjc.java.pruautorizacionesflickr.AutorizacionesFlickr;
/**
 *
 * @author JulioLopez
 */
public class Main {

    AutorizacionesFlickr autorizacionesFlickr
            = new AutorizacionesFlickr();
    Flickr flickr
            = new Flickr(autorizacionesFlickr.getApi_key(),
                    autorizacionesFlickr.getSecret(),
                    new REST());
    RequestContext requestContext
            = RequestContext.getRequestContext();
    
    requestContext.setAuth(autorizacionesFlickr.getAuth());
    
}
