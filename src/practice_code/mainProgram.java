package practice_code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class main {

	public static void main(String[] args) {
		
		
		//TODO - add (and check) arguments
		
		// TODO - calulcate and print file hash 
		
		int maximum_numbers = 9999999;
					
		// according to instructions, initial list has 10% missing, so the initialCapacity should be about 90%
		int initialCapacity = (int) Math.ceil(0.9*maximum_numbers);
		// I have a quad core so one thread per CPU for now
		// TODO auto detect number of cores
		int concurrencyLevel = 4;
		
		// Need to create a Set (instructions said UNIQUE numbers) which is thread safe and can be popuated in a multithreaded way.
		// We do this by creating a newKeySet backed by a concurrentHashMap

		// ConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel)
		ConcurrentHashMap<Integer, Integer> initialMap = new ConcurrentHashMap<Integer, Integer>(initialCapacity, 0.9f, concurrencyLevel);
		Set<Integer> initialSet = initialMap.newKeySet();
		
		
		// TODO: populate set in multithreaded manner, like so:
		// read file thread -> blocking pipe -> worker threads (x4) -> Set
		
		
		// Create array from set to be sorted		
		Integer[] initialArrayToSort = new Integer[initialSet.size()];
		initialArrayToSort = initialSet.toArray(initialArrayToSort);
		
		// Sort array in parallel manner using parallelSort
		Arrays.parallelSort(initialArrayToSort);

		// WARNING - probably too verbose 
		System.out.println(initialArrayToSort); 

	}

}
