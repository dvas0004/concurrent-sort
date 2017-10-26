package practice_code;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class mainProgram {
	
	static ConcurrentHashMap<Integer, Integer> initialMap;
	static Set<Integer> initialSet;
	static BlockingQueue<Integer> queue;
	
	// Class to write sorted results to file, and calculate the MD5 hash while doing so.
	public static void writeToFile (String filename, Integer[]x) throws IOException, NoSuchAlgorithmException{
		  BufferedWriter outputWriter = null;
		  outputWriter = new BufferedWriter(new FileWriter(filename));
		  MessageDigest md = MessageDigest.getInstance("MD5");
		  
		  for (int i = 0; i < x.length; i++) {
			  String toWrite = String.format ("%07d", x[i]);
			  
			  md.update((toWrite+"\n").getBytes());
			  
			  outputWriter.write(toWrite);
			  outputWriter.newLine();
			  
		  }
		  
		  outputWriter.flush();  
		  outputWriter.close();  
		  
		  StringBuilder sb = new StringBuilder();
          for (byte b : md.digest()) {
          	sb.append(String.format("%02x", b & 0xff));
          }
          System.out.println(sb.toString()); 
          
		}
	
	public static void main(String[] args) {
		
		
		long startTime = System.currentTimeMillis();
		String inputFile = null;
		String outputFile = null;
		
		// read and check arguments
		if (args.length < 2) {
			System.out.println("Not Enough Arguments - consult help!");
			System.exit(1);
		} else {
			inputFile = args[0];
			outputFile = args[1];
		}
		
		//Check that files exist
		File f = new File(inputFile);
		if(!f.exists()) { 
			System.out.println("Input file does not exist - consult help!");
			System.exit(1);
		}
		
		int maximum_numbers = 9999999;
					
		// according to instructions, initial list has 10% missing, so the initialCapacity should be about 90%
		int initialCapacity = (int) Math.ceil(0.9*maximum_numbers);
		
		// I have a quad core so one thread per CPU for now
		// TODO auto detect number of cores
		int concurrencyLevel = 4;
		
		// Creating new thread pool, where we'll place the "reader" and "worker" threads
		// reader thread will read numbers into queue
		// worker threads then consume queue and place numbers concurrent into a set which is made thread-safe because
		// it is backed up by a "ConcurrentHashMap". Here we set number of threads to concurrencyLevel + 1 
		// because we assume the "read" thread will be idle most of the time, while the rest of the threads are busy worker threads
		
		ExecutorService executor = Executors.newFixedThreadPool(concurrencyLevel+1);
		
		// Here we create the thread-safe Set backed up by the ConcurrentHashMap
		// ConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel)
		initialMap = new ConcurrentHashMap<Integer, Integer>(initialCapacity, 0.9f, concurrencyLevel);
		initialSet = initialMap.newKeySet();
				
		// Read file and process in multithreaded manner
		queue = new ArrayBlockingQueue<Integer>(maximum_numbers);

		// Only one read thread - probably will be bound by disk I/O rather than CPU
		// TODO test the above assumption for fun
	    Runnable reader = new ReaderThread(queue, inputFile, concurrencyLevel);
        executor.execute(reader);
	    
        // Execute Worker Threads
	    for (int i = 0; i < concurrencyLevel; i++) {
	    	System.out.println("Starting Worker Thread...");
	    	Runnable worker = new WorkerThread(queue, initialSet);
            executor.execute(worker);
          }
	    
	    // Wait for threads to finish
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        
        System.out.println("All Threads Finished, Beginning Sorting...");
	    
		// Change Set to Array. Note we choose a SET because instructions indicate UNIQUE numbers
        Integer[] initialArrayToSort = new Integer[initialSet.size()];
		initialArrayToSort = initialSet.toArray(initialArrayToSort);
		
		// Parallel Sort used to take advantage of parallel processing (faster)
		Arrays.parallelSort(initialArrayToSort);
		

		// Write results to file and output MD5 hash
		try {
			writeToFile(outputFile, initialArrayToSort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Timing
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println(totalTime);

	}

}
