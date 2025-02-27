package com.zmdev.protoplus.Fragments;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.zmdev.protoplus.Connections.BaseConnection;
import com.zmdev.protoplus.Connections.ConnectionClassInUseException;
import com.zmdev.protoplus.Connections.ConnectionStateKeeper;
import com.zmdev.protoplus.Dialogs.ProtoDialog;
import com.zmdev.protoplus.R;

public class TerminalFragment extends Fragment implements Toolbar.OnMenuItemClickListener {
    //views & UI
    private NavController navController;
    private ImageButton send;
    private TextView consoleTextView;
    private ScrollView consoleScrollView;
    private EditText editText;

    //connection
    private ConnectionStateKeeper stateKeeper;
    private BaseConnection connection;

    //Console
    private enum ConsoleState {INIT, SENDING, RECEIVING;}

    private ConsoleState currentState = ConsoleState.INIT;
    private ConsoleState prevState = ConsoleState.INIT;
    private int indexLineEnd = 0;
    private String[] LINE_END = {"\n", "\r", "\r\n", ""};
    private final String CONSOLE_SP_FILE = "CONSOLE_SP_FILE";
    private final String CONSOLE_SEPARATOR_KEY = "SEPARATOR_INDEX_KEY";
    private SharedPreferences sp;
    private ProtoDialog tsDialog;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stateKeeper = ConnectionStateKeeper.getInstance();
        if (stateKeeper == null) {
            Toast.makeText(getContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
        } else
            connection = stateKeeper.getConnection();
        sp = getContext().getSharedPreferences(CONSOLE_SP_FILE, Context.MODE_PRIVATE);
        indexLineEnd = sp.getInt(CONSOLE_SEPARATOR_KEY, 0);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_terminal, container, false);
        Toolbar toolbar = view.findViewById(R.id.console_toolbar);
        toolbar.setOnMenuItemClickListener(this);
        consoleTextView = view.findViewById(R.id.console_text_view);
        consoleScrollView = view.findViewById(R.id.console_scroll_view);
        send = view.findViewById(R.id.send_command_btn);
        editText = view.findViewById(R.id.terminal_edit);

        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        toolbar.setNavigationOnClickListener(v -> {
            navController.navigateUp();
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

               send.setOnClickListener(v -> {

            //get message and send it
            String text = editText.getText().toString().concat(LINE_END[indexLineEnd]);
            if (connection != null) connection.write(text);

            //UI
            currentState = ConsoleState.SENDING;
            appendConsoleText(text);
            editText.setText(null);

        });

        //----------------------------------- receive message -------------------------------
        if (connection != null) {
            try {
                connection.registerToIncomingTextCallback(text -> {
                    currentState = ConsoleState.RECEIVING;
                    appendConsoleText(text);
                });
            } catch (ConnectionClassInUseException e) {
                e.printStackTrace();
            }
        }
    }

    private void appendConsoleText(String text) {
        if (!currentState.equals(prevState)) { //state change detected
            if (currentState == ConsoleState.SENDING) {
                consoleTextView.append(">> ");
                prevState = ConsoleState.SENDING;
            } else if (currentState == ConsoleState.RECEIVING) {
                consoleTextView.append("<< ");
                prevState = ConsoleState.RECEIVING;
            }
        }

        consoleTextView.append(text);

        consoleScrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.consol_separator_lf:
                indexLineEnd = 0;
                break;
            case R.id.consol_separator_cr:
                indexLineEnd = 1;
                break;
            case R.id.consol_separator_crlf:
                indexLineEnd = 2;
                break;
            case R.id.consol_separator_none:
                indexLineEnd = 3;
                break;
        }
        sp.edit().putInt(CONSOLE_SEPARATOR_KEY, indexLineEnd).apply();
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (connection != null) connection.unregisterFromCallbacks();
    }



 }
