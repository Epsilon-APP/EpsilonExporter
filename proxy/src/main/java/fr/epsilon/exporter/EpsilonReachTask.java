package fr.epsilon.exporter;

import fr.epsilon.common.Epsilon;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class EpsilonReachTask {
    private static ScheduledTask task;

    public static void init(EpsilonExporter main)
    {
        task = main.getProxy().getScheduler().schedule(main, () ->
        {
            boolean reachable = Epsilon.get().isReachable();

            if (!reachable)
                return;

            try {
                Path path = Paths.get("epsilon_start");
                Files.createFile(path);

                task.cancel();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }
}
