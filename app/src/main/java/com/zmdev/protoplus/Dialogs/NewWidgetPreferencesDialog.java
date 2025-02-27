package com.zmdev.protoplus.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.zmdev.protoplus.App;
import com.zmdev.protoplus.CustomViews.ProtoView;
import com.zmdev.protoplus.R;
import com.zmdev.protoplus.Utils.ThemeUtils;
import com.zmdev.protoplus.ViewBuilder;
import com.zmdev.protoplus.db.Controllers.CommandsController;
import com.zmdev.protoplus.db.Controllers.ProjectsController;
import com.zmdev.protoplus.db.Entities.Command;
import com.zmdev.protoplus.db.Entities.Parameter;
import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;
import com.zmdev.protoplus.db.Pattern.LiveData;
import com.zmdev.protoplus.db.Pattern.Observer;
import com.maltaisn.icondialog.IconDialog;
import com.maltaisn.icondialog.data.Icon;
import com.maltaisn.icondialog.pack.IconPack;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

import static com.zmdev.protoplus.Adapters.CommandAdapter.getHighlightedCommand;
import static com.zmdev.protoplus.App.screen_width;
import static com.zmdev.protoplus.CustomViews.ProtoView.DEF_COMMAND;

public class NewWidgetPreferencesDialog extends DialogFragment implements IconDialog.Callback{

    private static final String TAG = "WidgetBuilderDialog";
    private Context mContext;
    private View preview;
    private ProtoViewAttrs attrs;
    private Command command = null;
    private int current_project;
    private List<Command> commandsList;
    private TextView command_text;
    private OnWidgetAddedCallback callback;
    private final boolean edit;
    private ImageButton add_btn;
    private CommandsController commandsController;
    private Observer<List<Command>> mObserver;


    public NewWidgetPreferencesDialog(ProtoViewAttrs attrs, boolean edit) {
        this.attrs = attrs;
        this.edit = edit;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        commandsController = new CommandsController(mContext);
        current_project = ProjectsController.getSelectedProject().getId();
        setStyle(DialogFragment.STYLE_NORMAL, ThemeUtils.activityDialogThemeID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //init layout
        View layout = inflater.inflate(R.layout.dialog_widget_preferences, container, false);
        //init views
        command_text                = layout.findViewById(R.id.select_widget_command_btn);
        LinearLayout prefs_layout   = layout.findViewById(R.id.preferences_layout);

        //init preview
        preview = ViewBuilder.generateViewFromAttrs(attrs, mContext);
        getProtoPreview().setViewInMode(ProtoView.MODE_PREVIEW);

        add_btn = layout.findViewById(R.id.create_widget_btn);

        //build preferences page
        for (View v: getProtoPreview().getWidgetPreferences(getChildFragmentManager()))
            prefs_layout.addView(v);


        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //show the selected widget
        LinearLayout preview_container = view.findViewById(R.id.widget_builder_preview_container);
        preview_container.addView(preview);

        //add button
        add_btn.setOnClickListener(v -> {
            boolean noError = checkNeededFieldsAndExtractAttrs();
            if (noError) {
                callback.onAdd(attrs);
                dismiss();
            }
        });

        //back button
        view.findViewById(R.id.wbuilder_back_btn).setOnClickListener(v -> {
           dismiss();
        });

        //docs button
        view.findViewById(R.id.open_docs_btn).setOnClickListener(v -> {
            int[] ids = getProtoPreview().getViewDetailsArray();
            new WidgetDocsDialog(ids[0], ids[1], ids[2], getProtoPreview().getDefCommand())
                    .show(getParentFragmentManager(), "");
        });

        //command button
        command_text.setOnClickListener(v -> {
            new CommandsSheet(commandsList, command -> {
                if (isCommandAccepted(command, getProtoPreview().getVarParamsNbr()))
                    updateCommand(command);

            }).show(getChildFragmentManager(),"commands_sheet");
        });

        //toggle editMode ON
        if (edit){
            add_btn.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_edit));
            ((TextView) view.findViewById(R.id.widget_prefs_page_label)).setText(R.string.edit_widget);
        }

        //show the default command if its a new widget or an existing one with default command
        if (!edit || getProtoPreview().getAttrs().getLinkedCommandID() == DEF_COMMAND)
            updateCommand(getProtoPreview().getDefCommand());


        //attrs & commands observable
        mObserver = new Observer<List<Command>>() {
            @Override
            public void onUpdate(List<Command> commands, LiveData.LiveDataState state) {
                NewWidgetPreferencesDialog.this.commandsList = commands;
                if (getProtoPreview().getDefCommand() != null) {
                    commands.add(getProtoPreview().getDefCommand());
                }
                if (edit) {
                    int id = getProtoPreview().getAttrs().getLinkedCommandID();
                    for (Command command: commandsList) {
                        if (command.getId() == id) {
                            updateCommand(command);
                            break;
                        }
                    }
                }
            }
        };

        commandsController.attachObserver(mObserver);

    }

    private void updateCommand(Command command) {
        this.command = command;
        command_text.setText(getHighlightedCommand(command));
    }

    private boolean checkNeededFieldsAndExtractAttrs() {

        if (command == null) {
            Toast.makeText(mContext, "A Command must be chosen !", Toast.LENGTH_SHORT).show();
            return false;
        }

        attrs = getProtoPreview().getAttrs();
        attrs.setX(screen_width/2.0f - preview.getWidth()/2.0f); //hardcode default posX
        attrs.setY(50); //hardcode default posY
        attrs.setProjectID(current_project);
        attrs.setLinkedCommandID(command.getId());
        return true;
    }

    private ProtoView getProtoPreview() {
        return (ProtoView) preview;
    }

    @Nullable
    @Override
    public IconPack getIconDialogIconPack() {
        return App.iconPack;
    }

    @Override
    public void onIconDialogCancelled() {
    }

    @Override
    public void onIconDialogIconsSelected(@NotNull IconDialog iconDialog, @NotNull List<Icon> list) {
        getProtoPreview().setDrawable(list.get(0).getId());
    }

    //========================== callback ===========================
    public interface OnWidgetAddedCallback {
        void onAdd(ProtoViewAttrs attrs);
    }
    public void setOnWidgetAddedCallback(OnWidgetAddedCallback callback) {
        this.callback = callback;
    }
    //======================= Utils ============================
    public boolean isCommandAccepted(Command command, int paramsCount) {
        int varCount = 0;
        for (Parameter p : command.getParams()) {
            if (p.getIsVariable() == 1) varCount++;
        }

        if (varCount == paramsCount) {
            return true;
        } else {
            String message = String.format(
                    Locale.getDefault(),
                    "This widget requires %d variable parameters, while the command that you " +
                            "have chosen has %d. Please refer to the widget's documentation for further details", paramsCount, varCount);
            ProtoDialog dialog = new ProtoDialog(mContext, "Incompatible Command",message);
            dialog.addNegativeButton("Close");
            dialog.show();
            return false;
        }

    }


    @Override
    public void onDetach() {
        super.onDetach();
        commandsController.detachObserver(mObserver);
    }
}
