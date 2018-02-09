
package bftsmart.reconfiguration.views;

/**
 *
 * @author eduardo
 */
public interface ViewStorage {
    
    public boolean storeView(View view);
    public View readView();
    
}
