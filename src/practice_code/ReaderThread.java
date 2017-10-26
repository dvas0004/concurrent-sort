package practice_code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingQueue;

// shamelessly plugged from https://stackoverflow.com/questions/29339933/read-and-write-files-in-java-using-separate-threads
// with only minor changes:
// * inclusion of MD5 hash calculation
// * inclusion of "poison pill" technique

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
            MessageDigest md = MessageDigest.getInstance("MD5");
            
            
            String buffer = null;
            
            while((buffer=br.readLine())!=null){
            	int integerToProcess = Integer.parseInt(buffer);
            	
            	// update md5 buffer - append newline because bufferedReader removes it, convert result to bytes
            	md.update((buffer+"\n").getBytes());
                blockingQueue.put(integerToProcess);
            }
            
            for (int i = 0; i < concurrencyLevel; i++) {
            	blockingQueue.put(-1);  //When end of file has been reached (we need to do this once for each thread - "poison pill")
              }
            
            StringBuilder sb = new StringBuilder();
            for (byte b : md.digest()) {
            	sb.append(String.format("%02x", b & 0xff));
            }
            System.out.println(sb.toString());            

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } catch(InterruptedException e){

        } catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


  }



}
