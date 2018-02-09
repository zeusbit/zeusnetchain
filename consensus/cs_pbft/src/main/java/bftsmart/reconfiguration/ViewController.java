
package bftsmart.reconfiguration;

import java.net.SocketAddress;

import bftsmart.reconfiguration.util.TOMConfiguration;
import bftsmart.reconfiguration.views.DefaultViewStorage;
import bftsmart.reconfiguration.views.View;
import bftsmart.reconfiguration.views.ViewStorage;

/**
 *
 * @author eduardo
 */
public class ViewController {

    protected View lastView = null;
    protected View currentView = null;
    private TOMConfiguration staticConf;
    private ViewStorage viewStore;

    public ViewController(int procId) {
        this.staticConf = new TOMConfiguration(procId);
    }

    
    public ViewController(int procId, String configHome) {
        this.staticConf = new TOMConfiguration(procId, configHome);
    }

    
    public final ViewStorage getViewStore() {
        if (this.viewStore == null) {
            String className = staticConf.getViewStoreClass();
            try {
                this.viewStore = (ViewStorage) Class.forName(className).newInstance();
            } catch (Exception e) {
                this.viewStore = new DefaultViewStorage();
            }

        }
        return this.viewStore;
    }

    public View getCurrentView(){
        if(this.currentView == null){
             this.currentView = getViewStore().readView();
        }
        return this.currentView;
    }
    
    public View getLastView(){
        return this.lastView;
    }
    
    public SocketAddress getRemoteAddress(int id) {
        return getCurrentView().getAddress(id);
    }
    
    public void reconfigureTo(View newView) {
        this.lastView = this.currentView;
        this.currentView = newView;
    }

    public TOMConfiguration getStaticConf() {
        return staticConf;
    }

    public boolean isCurrentViewMember(int id) {
        return getCurrentView().isMember(id);
    }

    public int getCurrentViewId() {
        return getCurrentView().getId();
    }

    public int getCurrentViewF() {
        return getCurrentView().getF();
    }

    public int getCurrentViewN() {
        return getCurrentView().getN();
    }

    public int getCurrentViewPos(int id) {
        return getCurrentView().getPos(id);
    }

    public int[] getCurrentViewProcesses() {
        return getCurrentView().getProcesses();
    }
}