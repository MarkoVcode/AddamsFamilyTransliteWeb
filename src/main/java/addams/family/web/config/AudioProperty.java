package addams.family.web.config;

public class AudioProperty {
	private int volume;
	private String track;
	private int start;
	private int stop;
	private String[] knocks;
	private String cron;
	
	public int getVolume() {
		return volume;
	}
	public void setVolume(int volume) {
		this.volume = volume;
	}
	public String getTrack() {
		return track;
	}
	public void setTrack(String track) {
		this.track = track;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getStop() {
		return stop;
	}
	public void setStop(int stop) {
		this.stop = stop;
	}
	public String[] getKnocks() {
		return knocks;
	}
	public void setKnocks(String[] knocks) {
		this.knocks = knocks;
	}
	public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
	}
}
