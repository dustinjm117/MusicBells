public class playerExample implements Runnable {
    private static final int NUM_TURNS = 3;
    enum playerExampleName {
       A,B,C,D;
    }
    public static void main(String[] args) {  
        // Create all the playerExamples, and give each a turn
        final int numplayerExamples = playerExampleName.values().length;

        System.out.println("This thread: "+Thread.currentThread()+" (name,priority,nameGroup)");              	
        System.out.println(" will create playerExamples (each of which creates its own thread)");              	
        playerExample[] playerExamples = new playerExample[numplayerExamples];
        for (playerExampleName pn : playerExampleName.values()) {
            playerExamples[pn.ordinal()] = new playerExample(pn);
        }

        for (int i = 1; i <= NUM_TURNS; i++) {
            System.out.println("(main:) "+Thread.currentThread()+" will tell playerExamples to take turn #"+i);              	
            for (playerExample p : playerExamples) {
                System.out.println("(main:) "+Thread.currentThread()+" heading into giveTurn function");              	
                p.giveTurn(); //run a function of playerExample p
            }
        }
        System.out.println("(main:) "+Thread.currentThread()+" will stop the playerExamples ");              	
        for (playerExample p : playerExamples) {
            p.stopplayerExample();
        }
        System.out.println(Thread.currentThread()+" will kill the playerExamples ");              	
        for (playerExample p : playerExamples) {
            p.killplayerExample();
        }
    }

    private final playerExampleName myName;
    private final Thread t;
    private boolean myTurn = false;
    private int turnCount;

    playerExample(playerExampleName myName) {
        this.myName = myName;
        turnCount = 1;
        t = new Thread(this, myName.name());
        t.start();
    }

    public void stopplayerExample() {
        t.interrupt();
    }
    
	public void killplayerExample() {
	     try {
	         t.join();
	         System.out.println("playerExample "+myName.name() + " is dead.");
	     } catch (InterruptedException e) {
	         System.err.println("Interrupted while trying to kill playerExample "+myName.name());
	     }
	 }


    public void giveTurn() { // usually, the main thread runs this to set private data myTurn for this playerExample thread
    	 
        synchronized (this) {
            if (myTurn) {
                throw new IllegalStateException("Attempt to give a turn to a playerExample who's hasn't completed the current turn");
            }
            System.out.println("(giveTurn:) "+Thread.currentThread()+" is setting playerExample " + myName.name() + " to take a turn");              	
            myTurn = true;            	
            // I have set this playerExamples's myTurn so now tell it to go (or eventually, go)
            notify();  
            if (myTurn) { // if playerExample thread is not done yet, 
            			  // this thread (probably main) should wait
                try {
                    System.out.println("(giveTurn:) Now "+Thread.currentThread()+ " is waiting.");              	
                    wait();
                } catch (InterruptedException exc) {
                	System.out.println("(giveTurn:) Interrupted while waiting for "+myName.name()+" to finish turn.");
                }
                //eventually will be notified and can finish and return
            }
        }
    }

    public void run() {
        synchronized (this) {
        	while (true) { // go until interrrupted
        		try {
                    // Wait for my turn to begin   
        			while (!myTurn) {
                        System.out.println("(run:) "+Thread.currentThread()+" for playerExample "+ myName.name() + " is waiting.");              	
                        wait();
                    }
                    // My turn!
                    doTurn();
                    turnCount++;

                    // Done, finished turn and now wake up one waiting thread
                    myTurn = false;
                    notify();
                } catch (InterruptedException exc) {
                	System.out.println("(run:) Interrupted "+myName.name());
                	break;
                }
            }
        }
    }

    private void doTurn() {
        System.out.println("(doTurn:) playerExample[" + myName.name() + "] taking turn " + turnCount);
    }
}
