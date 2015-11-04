package addams.family.web.scheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.LoggerFactory;

import addams.family.web.config.AudioProperty;

public class SoundRunnable implements Runnable {

	private String command;
	private static org.slf4j.Logger LOG;

	public SoundRunnable(AudioProperty ap) {
		LOG = LoggerFactory.getLogger(SoundRunnable.class);	
		command = buildCommand(ap);
	}
	
	private String buildCommand(AudioProperty ap){
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

	@Override
	public void run() {
		String s = null;
        try {
            Process p = Runtime.getRuntime().exec(command);
             
            BufferedReader stdInput = new BufferedReader(new
                 InputStreamReader(p.getInputStream()));
 
            BufferedReader stdError = new BufferedReader(new
                 InputStreamReader(p.getErrorStream()));
            while ((s = stdInput.readLine()) != null) {
            	LOG.info(command + ": " +s);
            }
            while ((s = stdError.readLine()) != null) {
            	LOG.error(command + ": " +s);
            }
        }
        catch (IOException e) {
        	LOG.error("Problem: " + e.getMessage());
        }
	}
}
