package addams.family.web.scheduler;

import it.sauronsoftware.cron4j.Scheduler;

public class Quickstart {
//http://www.sauronsoftware.it/projects/cron4j/manual.php
	//http://alvinalexander.com/java/edu/pj/pj010016
	public static void main(String[] args) {
		// Creates a Scheduler instance.
		Scheduler s = new Scheduler();
		// Schedule a once-a-minute task.
		s.schedule("* * * * *", new Runnable() {
			public void run() {
				System.out.println("Another minute ticked away...");
			}
		});
		s.schedule("* * * * *", new Runnable() {
			public void run() {
				System.out.println("Another minute ticked away bla...");
			}
		});
		// Starts the scheduler.
		s.start();
		// Will run for ten minutes.
		try {
			Thread.sleep(1000L * 60L * 10L);
		} catch (InterruptedException e) {
			;
		}
		// Stops the scheduler.
		s.stop();
	}

}