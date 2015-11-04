package addams.family.web.scheduler;

import addams.family.web.config.SensorsProperty;
import it.sauronsoftware.cron4j.Scheduler;

public class SensorsSchedulerThread  extends Thread {
	
	private Scheduler s; 
	private String id;
	
	public SensorsSchedulerThread(SensorsProperty sp) {
		s = new Scheduler();
		schedule(sp);
	}
	
	public void reload(SensorsProperty sp) {
		unschedule();
		schedule(sp);
	}
	
	private void schedule(SensorsProperty sp) {
		SensorsRunnable sr = new SensorsRunnable(sp);
		id = s.schedule(sp.getCron(), sr);
	}
	
	private void unschedule() {
		if(null != id) 
			s.deschedule(id);
	}
	
    public void run() {
		s.start();
    }
}
