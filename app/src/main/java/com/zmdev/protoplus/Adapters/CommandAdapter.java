package com.zmdev.protoplus.Adapters;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.zmdev.protoplus.db.Entities.Command;
import com.zmdev.protoplus.R;
import com.zmdev.protoplus.db.Entities.Parameter;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Typeface.BOLD;

public class CommandAdapter extends RecyclerView.Adapter<CommandAdapter.Holder> {

    private static final String TAG = "CommandAdapter";
    List<Command> commands = new ArrayList<>();
    private Context mContext;
    private OnCommandClickedListener listener;

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new Holder(LayoutInflater.from(mContext).inflate(R.layout.item_command,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Command currCmd = commands.get(position);
        holder.commandTxt.setText(getHighlightedCommand(currCmd));
    }

    @Override
    public int getItemCount() {
        return commands.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView commandTxt;

        public Holder(@NonNull View itemView) {
            super(itemView);
            commandTxt = itemView.findViewById(R.id.command_item_txt);
            commandTxt.setMovementMethod(new ScrollingMovementMethod());
            itemView.setOnClickListener(this);
            commandTxt.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
           if (listener != null) listener.onClick(commands.get(getAdapterPosition()), getAdapterPosition());
        }
    }


    public interface OnCommandClickedListener {
        void onClick(Command command, int position);
    }

    public void setOnCommandClickedListener(OnCommandClickedListener listener) {
        this.listener = listener;
    }

    public static SpannableString getHighlightedCommand(Command command) {

        int opcode_color = 0xFF6495F6;
        int index_color  = 0xFF90CAF9;
        int type_color   = 0xFFB39DDB;
        int name_color   = 0xFFEF9A9A;
        int value_color  = 0xFFEF9A9A;

        int hIndex;
        String opcode = command.getOpcode();
        Parameter[] params = command.getParams();
        StringBuilder stringBuilder = new StringBuilder();

        // generate parameters syntax, the Spannable must have full string
        // beforehand to be able to span it
        stringBuilder.append(opcode).append("  ");
        int count = 0;
        for (Parameter param : params) {
            stringBuilder.append(param.getNiceName(count++)).append("  ");
        }

        //highlight opcode
        SpannableString spannable = new SpannableString(stringBuilder.toString());
        hIndex = opcode.length();
        spannable.setSpan(new ForegroundColorSpan(opcode_color),0,hIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new StyleSpan(BOLD),0,hIndex, Spanned.SPAN_COMPOSING);
        hIndex+=2;//for spaces

        int pCount = 0;
        for (Parameter p: params) {
            String index = "" + pCount++;
            //one for ':'
            if (p.getIsVariable() == 1) {
                //"index:var"
                spannable.setSpan(new ForegroundColorSpan(index_color),hIndex,hIndex+=index.length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //hIndex++ ; //for ':'
                spannable.setSpan(new ForegroundColorSpan(type_color),hIndex,hIndex+=3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                if(!p.getHint().isEmpty()) {
                    //"index:var:hint"
                    hIndex++ ; //for ':'
                    spannable.setSpan(new ForegroundColorSpan(name_color),hIndex,hIndex+=p.getHint().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

            } else {
                //"index:const"
                spannable.setSpan(new ForegroundColorSpan(index_color),hIndex,hIndex+=index.length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //hIndex++ ; //for ':'
                spannable.setSpan(new ForegroundColorSpan(type_color),hIndex,hIndex+=5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                if(!p.getHint().isEmpty()) {
                    //"index:const:hint"
                    hIndex++ ; //for ':'
                    spannable.setSpan(new ForegroundColorSpan(name_color),hIndex,hIndex+=p.getHint().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                //"index:const:hint=def_value"
                hIndex++ ; //for ':'
                spannable.setSpan(new ForegroundColorSpan(value_color),hIndex,hIndex+=p.getDefValue().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            hIndex+=2; //for spaces
        }


        return spannable;
    }

}
