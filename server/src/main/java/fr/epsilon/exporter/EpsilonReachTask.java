package fr.epsilon.exporter;

import fr.epsilon.common.Epsilon;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EpsilonReachTask {
    private static BukkitTask task;

    public static void init(EpsilonExporter main)
    {
        task = main.getServer().getScheduler().runTaskTimer(main, () ->
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
        }, 0, 10);
    }
}
