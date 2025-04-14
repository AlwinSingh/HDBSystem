package src.model;
public class SystemConfiguration {
    private int configId;
    private String parameter;
    private String value;

    public void loadConfiguration() {
        // TODO: Stub - load from file or default settings
    }

    public void updateConfiguration(String param, String value) {
        this.parameter = param;
        this.value = value;
    }

    public String getConfiguration(String param) {
        // TODO: Stub - fetch config based on param
        return value;
    }
}
