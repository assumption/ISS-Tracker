package edu.calpoly.isstracker.IssData.Pojos;

import java.util.HashMap;
import java.util.Map;

public class Astronaut {

    private String craft;
    private String name;
    private Map<String, Object> additionalProperties = new HashMap<>();

    /**
     *
     * @return
     * The craft
     */
    public String getCraft() {
        return craft;
    }

    /**
     *
     * @param craft
     * The craft
     */
    public void setCraft(String craft) {
        this.craft = craft;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}