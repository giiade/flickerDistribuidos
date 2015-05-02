/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filterFile;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.*;

/**
 *
 * @author Alberto
 */
public class Filter implements FilenameFilter {

  @Override
  public boolean accept (File dir, String name) {
    return Pattern.matches(".*\\.(jpg|jpeg|gif|png|bmp)", name);
    //  if only one extension to check :  "\\.jpg"
  }
  
  public static String [] filtroImagenes (File dir) {
      String [] arcFiltrado;
      
      Filter filtro = new Filter();
      arcFiltrado = dir.list(filtro);
      
      return arcFiltrado;
  }
  
}
