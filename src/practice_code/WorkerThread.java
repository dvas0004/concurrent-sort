package practice_code;

import java.util.Set;
import java.util.concurrent.BlockingQueue;

// Worker Thread that simply reads numbers from a queue and then inserts them into our Set (which since is STATIC is shared among all threads) 

public class WorkerThread implements Runnable{

  protected BlockingQueue<Integer> blockingQueue = null;
  private Set<Integer> initialSet = null;

  public WorkerThread(BlockingQueue<Integer> blockingQueue, Set<Integer> initialSet){
    this.blockingQueue = blockingQueue;
    this.initialSet = initialSet;
  }

  @Override
  public void run() {

    try {

        while(true){
        	Integer numberToProcess = blockingQueue.take();
        	// Check whether end of file has been reached
            if(numberToProcess.equals(-1)){ 
                break;
            }
        	// System.out.println(String.format("execute thread: %s %s", Thread.currentThread().getName(), numberToProcess));
        	initialSet.add(numberToProcess);
            
        }               


    } catch(InterruptedException e){

    }finally{

    } 

  }

}
