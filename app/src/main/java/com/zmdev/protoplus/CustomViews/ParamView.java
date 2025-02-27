package com.zmdev.protoplus.CustomViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.zmdev.protoplus.App;
import com.zmdev.protoplus.db.Entities.Parameter;
import com.zmdev.protoplus.R;

public class ParamView extends LinearLayout {

    private Context mContext;
    RadioButton constVarBtn;
    RadioButton varBtn;
    EditText defValEdit;
    EditText paramLabelEdit;
    TextView title;
    int paramNbr = 0;
    boolean enabled = false;
    boolean isVar = false;

    public ParamView(Context context, int nbr) {
        super(context);
        paramNbr = nbr;
        init(context);
    }


    void init(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setOrientation(VERTICAL);
        setVisibility(GONE);
        setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        inflater.inflate(R.layout.view_parameter, this);

        constVarBtn = findViewById(R.id.cnst_radio);
        varBtn = findViewById(R.id.var_radio);
        defValEdit = findViewById(R.id.def_val_edit);
        paramLabelEdit = findViewById(R.id.param_hint_edit);
        title = findViewById(R.id.param_title_text);

        title.setText("Parameter ".concat(String.valueOf(paramNbr)));

        varBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isVar = isChecked;
            defValEdit.setVisibility(isChecked ? GONE : VISIBLE);
        });
    }

    /*returns null if const field is empty*/
    public Parameter extractParameter() {
        String defVal = defValEdit.getText().toString();
        String hint = paramLabelEdit.getText().toString();
        int isVar = varBtn.isChecked() ? 1 : 0;

        if (isVar == 0 && defVal.trim().isEmpty()) {
            defValEdit.setError("Required field !");
            return null;
        }else if (defVal.contains("<")) {
            defValEdit.setError("The character '<' cannot be used !");
            return null;
        } else if (defVal.length() > App.WORD_SIZE) {
            defValEdit.setError("can't be more than 10 characters!");
            return null;
        } else {
            defValEdit.setError(null);
        }

        return new Parameter(defVal,hint, isVar,-1);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        if (enabled) setVisibility(VISIBLE);
        else setVisibility(GONE);
    }


    public void setVariableName(String name) {
        if (name != null && !name.isEmpty()) paramLabelEdit.setText(name);
    }

    public void setIsVariable(int isVariable) {
        varBtn.setChecked(isVariable == 1);
        constVarBtn.setChecked(isVariable == 0);
    }

    public void setDefaultValue(String defValue) {
        if (defValue != null && !defValue.isEmpty()) defValEdit.setText(defValue);

    }

    public void setParamNbr(int i) {
        title.setText("Parameter ".concat(String.valueOf(i)));
    }
}
