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

  // array of supported extensions (use a List if you prefer)
    static final String[] EXTENSIONS = new String[]{
        "gif", "png", "bmp", "jpeg", "jpg" // and other formats you need
    };
    // filter to identify images based on their extensions
    public static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

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
