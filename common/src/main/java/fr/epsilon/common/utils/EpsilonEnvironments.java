package fr.epsilon.common.utils;

public class EpsilonEnvironments {
    private static String HOST_CONTROLLER = System.getenv("HOST_CONTROLLER");
    private static String HOST_TEMPLATE = System.getenv("HOST_TEMPLATE");

    private static final String scheme = "http";
    private static final int port = 8000;

    public static String getEpsilonURL(String endpoint) {
        return scheme + "://" + HOST_CONTROLLER + ":" + port + endpoint;
    }

    public static String getEpsilonTemplateURL(String endpoint) {
        return scheme + "://" + HOST_TEMPLATE + ":" + port + endpoint;
    }
}
