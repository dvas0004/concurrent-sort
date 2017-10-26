package practice_code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

//shamelessly plugged from https://stackoverflow.com/questions/29339933/read-and-write-files-in-java-using-separate-threads
// with only minor changes

public class ReaderThread implements Runnable{

  protected BlockingQueue<Integer> blockingQueue = null;
  private String inputFile = null;
  private int concurrencyLevel = 0;

  public ReaderThread(BlockingQueue<Integer> blockingQueue, String inputFile, Integer concurrencyLevel){
    this.blockingQueue = blockingQueue;     
    this.inputFile = inputFile;
    this.concurrencyLevel = concurrencyLevel;
  }

  @Override
  public void run() {
    BufferedReader br = null;
     try {
            br = new BufferedReader(new FileReader(new File(inputFile)));
            String buffer = null;
            
            //TODO: while reading file, calculate MD5 sum, and print it when ready
            while((buffer=br.readLine())!=null){
            	int integerToProcess = Integer.parseInt(buffer);
                blockingQueue.put(integerToProcess);
            }
            
            for (int i = 0; i < concurrencyLevel; i++) {
            	blockingQueue.put(-1);  //When end of file has been reached (we need to do this once for each thread - "poison pill")
              }
            

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } catch(InterruptedException e){

        }finally{
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


  }



}