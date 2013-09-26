package examples.threadinfo;

import java.util.Arrays;
import java.util.Set;
import org.glassfish.api.Param;
import org.glassfish.api.admin.AccessRequired;
import org.glassfish.api.admin.AdminCommand;
import org.glassfish.api.admin.AdminCommandContext;
import org.glassfish.api.admin.AdminCommandEventBroker;
import org.glassfish.api.admin.CommandLock;
import org.glassfish.api.admin.ManagedJob;
import org.glassfish.hk2.api.PerLookup;
import org.jvnet.hk2.annotations.Service;

/** Provides periodical overview of thread amount in each state.
 *
 * @author martinmares
 */
@Service(name="_list-threads")
@PerLookup
@CommandLock(CommandLock.LockType.NONE)
@AccessRequired(resource="domain", action="read")
@ManagedJob
public class ThreadCountCommand implements AdminCommand {
    
    public static final String EVENT_NAME = "thread.count";
    
    @Param(defaultValue = "1", optional = true, primary = true)
    private int interval;
    
    public void execute(AdminCommandContext acc) {
        AdminCommandEventBroker eb = acc.getEventBroker();
        while (eb.listening(EVENT_NAME)) {
            Set<Thread> threads = Thread.getAllStackTraces().keySet();
            int[] event = new int[Thread.State.values().length];
            Arrays.fill(event, 0);
            for (Thread thread : threads) {
                event[thread.getState().ordinal()]++;
            }
            eb.fireEvent(EVENT_NAME, Arrays.toString(event));
            if (interval > 0) {
                try {
                    Thread.sleep(interval * 1000l);
                } catch (InterruptedException ex) {}
            }
        }
        System.out.println("_list-threads: DONE");
    }
    
}
