package com.zmdev.protoplus.db.Entities;

public class Parameter  {

     private static final String TAG = "Parameter";
     String defValue;
     String hint;
     int isVariable;
     int command_id;


    public Parameter(String defValue, String hint, int isVariable, int command_id) {
        this.defValue = defValue;
        this.hint = hint;
        this.isVariable = isVariable;
        this.command_id = command_id;
    }

    public String getDefValue() {//can't rename this to getValue cuz GSON will fail for prev versions
        return defValue;
    }

    public void setDefValue(String defValue) {
        this.defValue = defValue;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public int getIsVariable() {
        return isVariable;
    }

    public void setIsVariable(int isVariable) {
        this.isVariable = isVariable;
    }

    public int getCommand_id() {
        return command_id;
    }

    public void setCommand_id(int command_id) {
        this.command_id = command_id;
    }

    public String getNiceName(int i){
        String index = "" + i;
        if (hint == null || hint.isEmpty()) {
            if (isVariable == 1) {
                return  index.concat(":var");
            } else {
                return index.concat(":const:").concat(defValue);
            }
        } else {
            if (isVariable == 1) {
                return index.concat(":var:").concat(hint);
            } else {
                return index.concat(":const:").concat(hint).concat(":").concat(defValue);
            }
        }
    }

}
