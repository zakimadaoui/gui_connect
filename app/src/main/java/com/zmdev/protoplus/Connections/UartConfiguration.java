package com.zmdev.protoplus.Connections;

public class UartConfiguration {

    private final int baudRate;
    private final int flowControlValue;
    private final int parityValue;
    private final int stopBitValue;
    private final int dataBitsValue;

    public UartConfiguration(int baudRate, int flowControlValue, int parityValue, int stopBitValue, int dataBitsValue) {
        this.baudRate = baudRate;
        this.flowControlValue = flowControlValue;
        this.parityValue = parityValue;
        this.stopBitValue = stopBitValue;
        this.dataBitsValue = dataBitsValue;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public int getFlowControlValue() {
        return flowControlValue;
    }

    public int getParityValue() {
        return parityValue;
    }

    public int getStopBitValue() {
        return stopBitValue;
    }

    public int getDataBitsValue() {
        return dataBitsValue;
    }
}
