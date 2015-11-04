package addams.family.web.scheduler;

import java.util.ArrayList;
import java.util.List;

import addams.family.web.config.AudioProperty;
import it.sauronsoftware.cron4j.Scheduler;

public class SoundSchedulerThread extends Thread {
	
	private Scheduler s; 
	private List<String> ids;
	
	public SoundSchedulerThread(List<AudioProperty> aps) {
		s = new Scheduler();
		schedule(aps);
	}
	
	public void reload(List<AudioProperty> aps) {
		unschedule();
		schedule(aps);
	}
	
	private void schedule(List<AudioProperty> aps) {
		ids = new ArrayList<String>();
		for (AudioProperty ap : aps) {
			SoundRunnable sr = new SoundRunnable(ap);
			ids.add(s.schedule(ap.getCron(), sr));
		}
	}
	
	private void unschedule() {
		for (String id : ids) {
			s.deschedule(id);
		}
	}
	
    public void run() {
		s.start();
    }
}
