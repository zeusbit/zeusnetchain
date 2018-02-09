
package bftsmart.reconfiguration;

/**
 *
 * @author Andre Nogueira
 */

public class VMServices {
	public static void main(String[] args) throws InterruptedException {

		ViewManager viewManager = new ViewManager();


		if(args.length == 1){
			System.out.println("####Tpp Service[Disjoint]####");

			int smartId = Integer.parseInt(args[0]);

			viewManager.removeServer(smartId);
		}else if(args.length == 3){
			System.out.println("####Tpp Service[Join]####");

			int smartId = Integer.parseInt(args[0]);
			String ipAddress = args[1];
			int port = Integer.parseInt(args[2]);

			viewManager.addServer(smartId, ipAddress,port);

		}else{
			System.out.println("Usage: java -jar TppServices <smart id> [ip address] [port]");
			System.exit(1);
		}

		viewManager.executeUpdates();
		
		Thread.sleep(2000);//2s
		viewManager.close();

		System.exit(0);
	}
}