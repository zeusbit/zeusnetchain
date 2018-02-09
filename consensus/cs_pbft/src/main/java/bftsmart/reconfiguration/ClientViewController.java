
package bftsmart.reconfiguration;

import java.net.InetSocketAddress;
import bftsmart.reconfiguration.views.View;

/**
 *
 * @author eduardo
 */
public class ClientViewController extends ViewController {

    public ClientViewController(int procId) {
        super(procId);
        View cv = getViewStore().readView();
        if(cv == null){
            reconfigureTo(new View(0, getStaticConf().getInitialView(), 
                getStaticConf().getF(), getInitAdddresses()));
        }else{
            reconfigureTo(cv);
        }
    }

    public ClientViewController(int procId, String configHome) {
        super(procId, configHome);
        View cv = getViewStore().readView();
        if(cv == null){
            reconfigureTo(new View(0, getStaticConf().getInitialView(), 
                getStaticConf().getF(), getInitAdddresses()));
        }else{
            reconfigureTo(cv);
        }
    }

    public void updateCurrentViewFromRepository(){
         this.currentView = getViewStore().readView();
    }
    
    private InetSocketAddress[] getInitAdddresses() {
        int nextV[] = getStaticConf().getInitialView();
        InetSocketAddress[] addresses = new InetSocketAddress[nextV.length];
        for (int i = 0; i < nextV.length; i++) {
            addresses[i] = getStaticConf().getRemoteAddress(nextV[i]);
        }

        return addresses;
    }
}