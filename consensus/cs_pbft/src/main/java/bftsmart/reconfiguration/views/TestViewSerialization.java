
package bftsmart.reconfiguration.views;

import java.net.InetSocketAddress;

public class TestViewSerialization {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        int[] ids = {1,2,3,4};
        InetSocketAddress[] in = new InetSocketAddress[4];
        in[0] = new InetSocketAddress("127.0.0.1",1234);
        in[1] = new InetSocketAddress("127.0.0.1",1234);
        in[2] = new InetSocketAddress("127.0.0.1",1234);
        in[3] = new InetSocketAddress("127.0.0.1",1234);
        View v = new View(10, ids,1,in);
        ViewStorage st = new DefaultViewStorage();
        st.storeView(v);
        
        View r = st.readView();
        System.out.println(r);
    }

}
