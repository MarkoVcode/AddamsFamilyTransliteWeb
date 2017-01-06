package addams.family.web.thingoncloud;

import addams.family.web.config.ThingOnCloudProperty;

import java.util.List;

/**
 * Created by developer on 1/6/17.
 */
public class ThingOnCloudThread extends Thread  {

    private ThingOnCloudRunnable toci;

    public ThingOnCloudThread(List<ThingOnCloudProperty> tocp) {
        toci = new ThingOnCloudRunnable(tocp);
    }

    public void run() {
        toci.run();
    }
}
