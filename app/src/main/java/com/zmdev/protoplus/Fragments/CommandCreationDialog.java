package com.zmdev.protoplus.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.zmdev.protoplus.App;
import com.zmdev.protoplus.Dialogs.ProtoDialog;
import com.zmdev.protoplus.Utils.ThemeUtils;
import com.zmdev.protoplus.db.Controllers.CommandsController;
import com.zmdev.protoplus.db.Controllers.ProjectsController;
import com.zmdev.protoplus.db.Entities.Command;
import com.zmdev.protoplus.db.Entities.Parameter;
import com.zmdev.protoplus.CustomViews.ParamView;
import com.zmdev.protoplus.R;

import java.util.ArrayList;
import java.util.List;

public class CommandCreationDialog extends DialogFragment {

    private static final String TAG = "NewCommandFragment";
    private Context mContext;
    List<ParamView> paramViews = new ArrayList<>();
    private final int MAX_PARAMS = 9;
    private int var_params_nbr = 0;
    private int total_params_nbr = 0; //flag included
    private boolean inEditMode = false;
    private EditText commandNameEdit;
    private EditText detailsEdit;
    private CheckBox flagCheckBox;
    private NumberPicker numPicker;
    private Command command;
    private CommandsController commandsController;

    public void setInEditMode(Command command) {
        this.command = command;
        this.inEditMode = true;
    }

    public CommandCreationDialog() {
        //empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, ThemeUtils.activityDialogThemeID);
        commandsController = new CommandsController(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        return inflater.inflate(R.layout.page_new_command, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        commandNameEdit = view.findViewById(R.id.opcode_edit);
        detailsEdit = view.findViewById(R.id.details_edit);
        flagCheckBox = view.findViewById(R.id.flag_checkbox);

        //setup params layout
        LinearLayout paramsLayout = view.findViewById(R.id.parameters_layout);
        for (int i = 0; i <= MAX_PARAMS ; i++) { //init MAX+1 paramViews
            ParamView paramView = new ParamView(mContext, i-1);
            paramView.setIsVariable(1);
            paramViews.add(paramView); //initially all params are vars
            paramsLayout.addView(paramView);
        }
        paramViews.get(0).setParamNbr(0); //correct param 0 index
        paramViews.get(0).setIsVariable(0); //set param 0 as a flag


        //flag
        flagCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            paramViews.get(0).setEnabled(isChecked);
            for (int i = 1; i <= var_params_nbr; i++) {
                if (isChecked) paramViews.get(i).setParamNbr(i); //shift all by +1
                else paramViews.get(i).setParamNbr(i-1); //shift all by -1
            }
        });


        //setup number picker
        numPicker = view.findViewById(R.id.params_nbr_picker);
        numPicker.setMinValue(0);
        numPicker.setMaxValue(MAX_PARAMS);
        numPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            var_params_nbr = newVal;
            for (int i = 1; i <= MAX_PARAMS; i++) {
                paramViews.get(i).setEnabled(i <= var_params_nbr);
            }
        });

        if (inEditMode) {
//            view.findViewById(R.id.edit_warning).setVisibility(View.VISIBLE);//SHOW edit warning
            loadDataForEditMode();
            view.findViewById(R.id.params_nbr_picker_txt).setVisibility(View.GONE);
            numPicker.setVisibility(View.GONE);
            flagCheckBox.setVisibility(View.GONE);
            view.findViewById(R.id.flag_txt).setVisibility(View.GONE);

            View deleteButton = view.findViewById(R.id.delete_command);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(v -> {
                new AlertDialog.Builder(mContext)
                        .setTitle(R.string.delete_command_title)
                        .setMessage(R.string.delete_command_text)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            commandsController.delete(command);
                            dismiss();
                        })
                        .setNegativeButton(R.string.no, null)
                        .create()
                        .show();
            });
        }

        //save button
        view.findViewById(R.id.save).setOnClickListener(v -> {

            String opcode = commandNameEdit.getText().toString();
            if (opcode.isEmpty()) {
                commandNameEdit.setError("At least one character is required");
                return;
            } else if (opcode.contains("<")) {
                commandNameEdit.setError("The character '<' cannot be used !");
                return;
            }else if (commandNameEdit.length() > App.WORD_SIZE) {
                commandNameEdit.setError("can't be more than 10 characters!");
                return;
            } else {
                commandNameEdit.setError(null);
            }

            String details = detailsEdit.getText().toString();

            int paramsOffset = 1; //start extracting from param 1
            if (flagCheckBox.isChecked()) {
                paramsOffset = 0; //start extracting from param 0
                total_params_nbr = var_params_nbr + 1; //count the flag
            } else {
                total_params_nbr = var_params_nbr ;    //don't count the flag
            }


            Parameter[] parameters = new Parameter[total_params_nbr];

            for (int i = 0; i < total_params_nbr; i++) {
                ParamView paramView = paramViews.get(i+paramsOffset);
                if (paramView.isEnabled()) {
                    parameters[i] = paramView.extractParameter();
                    if (parameters[i] == null) return; //return if param could not be extracted
                }
            }

            if (inEditMode){
                command.setOpcode(opcode);
                command.setDetails(details);
                command.setParamsNbr(total_params_nbr);
                command.setParams(parameters);
                commandsController.update(command);

            }else{
                commandsController.insert(new Command(ProjectsController.getSelectedProject().getId(),opcode,details,parameters));
            }
            dismiss();
        });
        //cancel button
        view.findViewById(R.id.cancel).setOnClickListener(v ->{
            dismiss();
        });
        //help menu button
        view.findViewById(R.id.help_new_command).setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://github.com/zakimadaoui/GuiConnectHelper/blob/master/docs/Getting%20started%20with%20GuiConnect%2B.md")));
        });
    }

    void loadDataForEditMode(){

        commandNameEdit.setText(command.getOpcode());
        if (command.getDetails() != null) {
            detailsEdit.setText(command.getDetails());
        }


        total_params_nbr = var_params_nbr = command.getParamsNbr();

        if (total_params_nbr > 0 && command.getParams()[0].getIsVariable() == 0) { //flag is used
            var_params_nbr--; //keep only vars count
            paramViews.get(0).setVariableName(command.getParams()[0].getHint());
            paramViews.get(0).setDefaultValue(command.getParams()[0].getDefValue());
            flagCheckBox.setChecked(true); //this in turns enables the view and shifts the others

            for (int i = 1; i <= var_params_nbr; i++) {
                paramViews.get(i).setEnabled(true);
                paramViews.get(i).setVariableName(command.getParams()[i].getHint());
                paramViews.get(i).setIsVariable(command.getParams()[i].getIsVariable());
                paramViews.get(i).setDefaultValue(command.getParams()[i].getDefValue());
            }

        } else {
            for (int i = 1; i <= var_params_nbr; i++) {
                paramViews.get(i).setEnabled(true);
                paramViews.get(i).setVariableName(command.getParams()[i-1].getHint());
                paramViews.get(i).setIsVariable(command.getParams()[i-1].getIsVariable());
                paramViews.get(i).setDefaultValue(command.getParams()[i-1].getDefValue());
            }
        }
        numPicker.setValue(var_params_nbr);
    }

}
