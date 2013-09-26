package examples.threadinfo;

import com.sun.enterprise.admin.cli.CLICommand;
import com.sun.enterprise.admin.cli.remote.RemoteCLICommand;
import com.sun.enterprise.admin.remote.sse.GfSseInboundEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.api.Param;
import org.glassfish.api.admin.AdminCommandEventBroker;
import org.glassfish.api.admin.CommandException;
import org.glassfish.hk2.api.PerLookup;
import org.jvnet.hk2.annotations.Service;

/**
 *
 * @author martinmares
 */
@Service(name = "list-threads")
@PerLookup
public class ThreadCountCliCommand extends CLICommand implements AdminCommandEventBroker.AdminCommandListener<GfSseInboundEvent> {
    
    @Param(defaultValue = "1", optional = true, primary = true)
    private int interval;
    
    private int counter = 0;

    @Override
    protected int executeCommand() throws CommandException {
        programOpts.removeDetach();
        RemoteCLICommand cmd = new RemoteCLICommand("_list-threads", programOpts, env);
        cmd.registerListener(ThreadCountCommand.EVENT_NAME, this);
        cmd.execute("_list-threads", String.valueOf(interval));
        return 0;
    }

    public void onAdminCommandEvent(String string, GfSseInboundEvent t) {
        if (counter == 0) { //Print header
            Thread.State[] states = Thread.State.values();
            StringBuilder header = new StringBuilder(states.length * 16 + 1);
            header.append("|");
            for (Thread.State val : states) {
                header.append(String.format("%14s |", val.name()));
            }
            logger.info(header.toString());
        }
        if (++counter >= 10) {
            counter = 0;
        }
        try {
            String event = t.getData();
            String[] data = event.substring(1, event.length() - 1).split(", ");
            StringBuilder line = new StringBuilder(data.length * 16 + 1);
            line.append("|");
            for (String val : data) {
                line.append(String.format("%14s |", val));
            }
            logger.info(line.toString());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
}
