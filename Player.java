import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;



public class Player extends Tone implements Runnable {
	
    private Note myNote = Note.REST;
    private Thread myThread;
    public boolean playerTurn = false;
    public AudioFormat myAF;
    private NoteLength currentNLength;
    
    
    public Player(Note threadNote, AudioFormat af) {
    	super(af);
    	myNote = threadNote;
    	myAF = af;
    	myThread = new Thread(this);
    	myThread.start();
    	//make it say which thread started
    }

     
    public void run() {
        synchronized (this) {
        	while (true) { // go until interrrupted
        		try {
                    // Wait for my turn to begin   
        			while (playerTurn == false) 
        			{
        				  System.out.println(Thread.currentThread() + " is waiting.");
        	              wait();
        	              System.out.println(Thread.currentThread() + " just waited.");                
        	        }
                    // My turn!
        			playNote(line, new BellNote(myNote, currentNLength));        			
                    // Done, finished turn and now wake up one waiting thread
                    playerTurn = false;
                    notify();
                } 
        		catch (InterruptedException e) 
        		{
                	System.out.println("(run:) Interrupted "+Thread.currentThread());
                	break;
                }
            }
        }
    }




public void giveTurn(NoteLength passedInLength) { // usually, the main thread runs this to set private data playerTurn for this playerExample thread
	 
    synchronized (this) {
        if (playerTurn) {
            throw new IllegalStateException("Attempt to give a turn to a playerExample who's hasn't completed the current turn");
        }
        currentNLength = passedInLength;
        playerTurn = true;            	
        // I have set this playerExamples's playerTurn so now tell it to go (or eventually, go)
        notify();  
        if (playerTurn) 
        { // if playerExample thread is not done yet, 
        			  // this thread (probably main) should wait
            try {
                System.out.println("(giveTurn:) Now "+Thread.currentThread()+ " is waiting.");  
                System.out.println(Thread.currentThread() + " is waiting.");
                wait();
                System.out.println(Thread.currentThread() + " just waited.");
            } catch (InterruptedException exc) {
            }
            //eventually will be notified and can finish and return
        }
    }
}
}