package practice_code;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
	
	public static void writeToFile (String filename, Integer[]x) throws IOException{
		  BufferedWriter outputWriter = null;
		  outputWriter = new BufferedWriter(new FileWriter(filename));
		  for (int i = 0; i < x.length; i++) {
			  // note: correct formatting of numbers
			  outputWriter.write(String.format ("%07d", x[i]));
			  outputWriter.newLine();
		  }
		  // TODO: calculate and print MD5 sum
		  
		  outputWriter.flush();  
		  outputWriter.close();  
		}
	
	public static void main(String[] args) {
		
		
		long startTime = System.currentTimeMillis();
		
		//TODO - change to arguments
		String inputFile = "/tmp/numbers.txt";
					
		int maximum_numbers = 9999999;
					
		// according to instructions, initial list has 10% missing, so the initialCapacity should be about 90%
		int initialCapacity = (int) Math.ceil(0.9*maximum_numbers);
		
		// I have a quad core so one thread per CPU for now
		// TODO auto detect number of cores
		int concurrencyLevel = 4;
		
		ExecutorService executor = Executors.newFixedThreadPool(5);
		
		// Need to create a Set (instructions said UNIQUE numbers) which is thread safe and can be popuated in a multithreaded way.
		// We do this by creating a newKeySet backed by a concurrentHashMap

		// ConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel)
		initialMap = new ConcurrentHashMap<Integer, Integer>(initialCapacity, 0.9f, concurrencyLevel);
		initialSet = initialMap.newKeySet();
				
		// Read file and process in multithreaded manner
		queue = new ArrayBlockingQueue<Integer>(maximum_numbers);

		// Only one read thread - probably will be bound by disk I/O rather than CPU
		// TODO test the above assumption for fun
	    Runnable reader = new ReaderThread(queue, inputFile, concurrencyLevel);
        executor.execute(reader);
	    
        // Start Worker Threads
	    for (int i = 0; i < concurrencyLevel; i++) {
	    	System.out.println("Starting Worker Thread...");
	    	Runnable worker = new WorkerThread(queue, initialSet);
            executor.execute(worker);
          }
	    
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        

	    
		Integer[] initialArrayToSort = new Integer[initialSet.size()];
		initialArrayToSort = initialSet.toArray(initialArrayToSort);
		
		Arrays.parallelSort(initialArrayToSort);
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
//		System.out.println(Arrays.toString(initialArrayToSort));
		try {
			writeToFile("/tmp/sorted.txt", initialArrayToSort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(totalTime);

	}

}