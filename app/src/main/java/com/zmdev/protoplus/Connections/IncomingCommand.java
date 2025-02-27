package com.zmdev.protoplus.Connections;

public class IncomingCommand {
    private String identifier;
    private String data;

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public interface Function{
        int FUNCTION_DISPLAY = 'd';
    }
}
