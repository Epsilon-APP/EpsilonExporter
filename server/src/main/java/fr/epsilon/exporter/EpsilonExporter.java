package fr.epsilon.exporter;

import fr.epsilon.common.Epsilon;
import fr.epsilon.common.template.Template;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class EpsilonExporter extends JavaPlugin {
    @Override
    public void onEnable() {
        Epsilon epsilon = Epsilon.get();

        Template template = epsilon.template();

        getServer().getLogger().info("Download map ...");

        EpsilonMapIgniter mapIgniter = new EpsilonMapIgniter(this, template);
        mapIgniter.download();

        getServer().getLogger().info("Download map has been finished.");

        try {
            initSlots(template.getSlots());
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        getServer().getScheduler().runTask(this, () -> {
            getServer().getLogger().info("Epsilon ignited !");

            mapIgniter.load();
            getServer().getLogger().info("Load maps ...");

            EpsilonReachTask.init(this);
        });
    }

    private void initSlots(int slots) throws ReflectiveOperationException {
        Method serverGetHandle = getServer().getClass().getDeclaredMethod("getHandle");
        Object playerList = serverGetHandle.invoke(getServer());

        getMaxPlayersField(playerList).setInt(playerList, slots);
    }

    private Field getMaxPlayersField(Object playerList) throws ReflectiveOperationException {
        Class<?> playerListClass = playerList.getClass().getSuperclass();

        try {
            Field field = playerListClass.getDeclaredField("maxPlayers");
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            for (Field field : playerListClass.getDeclaredFields()) {
                if (field.getType() != int.class) {
                    continue;
                }

                field.setAccessible(true);

                if (field.getInt(playerList) == getServer().getMaxPlayers()) {
                    return field;
                }
            }

            throw new NoSuchFieldException("Unable to find maxPlayers field in " + playerListClass.getName());
        }
    }
}
