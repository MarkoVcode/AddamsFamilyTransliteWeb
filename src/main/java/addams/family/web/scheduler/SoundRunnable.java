package addams.family.web.scheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import addams.family.web.config.AudioProperty;

public class SoundRunnable implements Runnable {

	private String command;
	private String cron;
	
	public SoundRunnable(AudioProperty ap) {
		command = buildCommand(ap);
		cron = ap.getCron();
		System.out.println("CONSTRUCTED");
	}
	
	private String buildCommand(AudioProperty ap){
		System.out.println("BUILD");
		return "python SoundPlay.py -v "
				+ ap.getVolume()
				+ " -i "
				+ "../soundtrack/"
				+ ap.getTrack()
				+ " -s "
				+ ap.getStart()
				+ " -t "
				+ ap.getStop()
				+ " -k "
				+ ap.getKnocks();
	}
	
	public String getCron() {
		return cron;
	}
	
	@Override
	public void run() {
		System.out.println("STARTED");
		String s = null;
        try {
            Process p = Runtime.getRuntime().exec(command);
             
            BufferedReader stdInput = new BufferedReader(new
                 InputStreamReader(p.getInputStream()));
 
            BufferedReader stdError = new BufferedReader(new
                 InputStreamReader(p.getErrorStream()));

            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
        }
	}

}
