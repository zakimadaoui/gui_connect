package com.zmdev.protoplus.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.zmdev.protoplus.App;
import com.zmdev.protoplus.CustomViews.ProtoCanvas;
import com.zmdev.protoplus.CustomViews.ProtoView;
import com.zmdev.protoplus.Dialogs.NewWidgetPreferencesDialog;
import com.zmdev.protoplus.Dialogs.WidgetsCatalogDialog;
import com.zmdev.protoplus.Utils.TutorialFlow;
import com.zmdev.protoplus.ViewBuilder;
import com.zmdev.protoplus.db.Controllers.ProjectsController;
import com.zmdev.protoplus.db.Entities.AttrsAndCommand;
import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;
import com.zmdev.protoplus.R;
import com.zmdev.protoplus.db.Controllers.AttrsController;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.zmdev.protoplus.App.display_unit;
import static com.zmdev.protoplus.App.loss_offset;
import static com.zmdev.protoplus.App.screen_width;

public class GuiEditorFragment extends Fragment implements View.OnTouchListener {

    private static final String TAG = "GuiCreatorFragment";
    private static int OPTION_ON_COLOR ;
    private static int OPTION_OFF_COLOR ;
    private Context             mContext;
    private RelativeLayout      controller_layout;
    private int                 _xDelta;
    private int                 _yDelta;
    private AttrsController    attrsController;
    List<View>                  viewList = new ArrayList<>();
    private NavController       navController;
    private ProtoCanvas protoCanvas;
    private int[] protoCanvasAbsolutePosition = new int[2];
    private boolean isInDeleteMode = false;
    private boolean isInEditMode = false;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private ProgressDialog progressDialog;
    private ImageButton remove_btn;
    private ImageButton edit_btn;
    private ImageButton add_btn;
    private ScrollView scrollView;
    private View save_btn;
    private List<ProtoViewAttrs> removedViewsList = new LinkedList<>();
    private boolean changeOccurred = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        attrsController = new AttrsController(getContext());

        OPTION_ON_COLOR  = ContextCompat.getColor(mContext,R.color.light_red);
        OPTION_OFF_COLOR = ContextCompat.getColor(mContext,R.color.app_color);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, backPressCallback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_canvas, container, false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        controller_layout = view.findViewById(R.id.controller_layout);
        controller_layout.getLayoutParams().height = 3 * App.screen_height;
        controller_layout.requestLayout();

        scrollView = view.findViewById(R.id.gui_scroll_view);
        scrollView.setOnTouchListener(this);

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        loadViewFromDbToUI();

        //proto grid
        protoCanvas = view.findViewById(R.id.proto_grid);
        protoCanvas.setOnTouchListener(this);
        remove_btn = view.findViewById(R.id.controller_remove);
        edit_btn = view.findViewById(R.id.controller_edit);
        add_btn = view.findViewById(R.id.controller_add);
        save_btn = view.findViewById(R.id.controller_save);

        //back button
        view.findViewById(R.id.controller_back).setOnClickListener(v -> {
//            navController.navigateUp();
            showExitDialog();
        });
        //adding widgets
        add_btn.setOnClickListener(v -> {
            //don't forget to turn-off the other option
            setDeleteModeEnabled(false);
            setEditModeEnabled(false);
            hideWidgetSelectionBorders();
            //show catalog of widgets
            WidgetsCatalogDialog previewsDialog = new WidgetsCatalogDialog();
            previewsDialog.show(getParentFragmentManager(), "WidgetsCatalog");
            previewsDialog.setCallback((attrs) -> {
                NewWidgetPreferencesDialog prefs_dialog = new NewWidgetPreferencesDialog(attrs,
                        false);
                prefs_dialog.show(getParentFragmentManager(), "create_widget_dialog");
                prefs_dialog.setOnWidgetAddedCallback(this::addWidget);
            });
        });
        //Adding views to DB
        save_btn.setOnClickListener(v -> {
            attrsController.deleteList(removedViewsList);
            attrsController.insertAll(viewList, this::loadViewFromDbToUI);
            Toast.makeText(mContext, R.string.saving_gui, Toast.LENGTH_SHORT).show();
        });
        //toggle remove mode
        remove_btn.setOnClickListener(v -> {
            isInDeleteMode = !isInDeleteMode;
            setDeleteModeEnabled(isInDeleteMode);
            setEditModeEnabled(false);//don't forget to turn-off the other option
            hideWidgetSelectionBorders();
        });
        //toggle edit mode
        edit_btn.setOnClickListener(v -> {
            isInEditMode = !isInEditMode;
            setEditModeEnabled(isInEditMode);
            setDeleteModeEnabled(false);//don't forget to turn-off the other option
            hideWidgetSelectionBorders();
        });
    }

    private void hideWidgetSelectionBorders() {
        protoCanvas.showResizingBorders(null, 0, false);
    }

    private void setDeleteModeEnabled(boolean toggle) {
        isInDeleteMode = toggle;
        if (toggle) remove_btn.setImageTintList(ColorStateList.valueOf(OPTION_ON_COLOR));
        else remove_btn.setImageTintList(ColorStateList.valueOf(OPTION_OFF_COLOR));
    }

    private void setEditModeEnabled(boolean toggle) {
        isInEditMode = toggle;
        if (toggle) edit_btn.setImageTintList(ColorStateList.valueOf(OPTION_ON_COLOR));
        else edit_btn.setImageTintList(ColorStateList.valueOf(OPTION_OFF_COLOR));
    }

    private void loadViewFromDbToUI() {
        progressDialog.show();
        int currentProjectID = ProjectsController.getSelectedProject().getId();

        executor.execute(() -> {
            List<AttrsAndCommand> attrsAndCommands = attrsController.getAttrsAndCommandsList(currentProjectID);
            //run on UI thread
            new Handler(Looper.getMainLooper()).post(() -> {
                //make sure the layout is cleared
                viewList.clear();
                controller_layout.removeAllViews();

                for (AttrsAndCommand specs : attrsAndCommands) {
                    View v = ViewBuilder.generateViewFromAttrs(specs, mContext);
                    v.setOnTouchListener(this);
                    controller_layout.addView(v);
                    viewList.add(v);
                    ((ProtoView)v).setViewInMode(ProtoView.MODE_NO_TOUCH); //No touch to be able to edit in peace
                }
                progressDialog.cancel();
                showTutorial();
            });
        });
    }

    private void displayDeleteDialog(Runnable onProceed) {
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.del_widget)
                .setMessage(R.string.del_widget_dilog)
                .setPositiveButton(R.string.yes, (dialog, which) -> onProceed.run())
                .setNegativeButton(R.string.no, null)
                .create()
                .show();
    }

    private void addWidget(ProtoViewAttrs attrs) {
        View v = ViewBuilder.generateViewFromAttrs(attrs, mContext);
        v.setOnTouchListener(this);
        ((ProtoView)v).setViewInMode(ProtoView.MODE_NO_TOUCH);
        viewList.add(v);
        controller_layout.addView(v);
        changeOccurred = true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float pointerX, pointerY, lockX, lockY;
        int scroll_offset = scrollView.getScrollY();
        controller_layout.getLocationOnScreen(protoCanvasAbsolutePosition);

        if (isInDeleteMode) {
            if(v == scrollView) return true;
            if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                //select the view to delete
                protoCanvas.showResizingBorders(v, scroll_offset, true);
                //show the confirmation dialog
                displayDeleteDialog(() -> {
                    controller_layout.removeView(v);
                    viewList.remove(v);
                    removedViewsList.add(((ProtoView)v).getAttrs());
                    hideWidgetSelectionBorders();
                    changeOccurred = true;
                });
            }
        }
        else if (isInEditMode) {
            if(v == scrollView) return true;
            if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                editWidget(v);
                protoCanvas.showResizingBorders(v, scroll_offset, true);
            }
        }
        else
            switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                if (v == scrollView) scrollView.onTouchEvent(event);
                _xDelta = (int) (v.getX() - event.getRawX());
                _yDelta = (int) (v.getY() - event.getRawY());

                if (protoCanvas.isResizeBordersShown()){
                    protoCanvas.performResizeThumbCheck(event.getRawX(), event.getRawY() - protoCanvasAbsolutePosition[1], scroll_offset);
                }
                if(!protoCanvas.isThumbPressed())protoCanvas.showResizingBorders(v, scroll_offset, v != scrollView);
                break;
            case MotionEvent.ACTION_UP:
                if (v == scrollView) {
                    scrollView.onTouchEvent(event);
                } else {
                    protoCanvas.showResizingBorders(v,scroll_offset, v != scrollView);
                }
                protoCanvas.hidePrecisionLines();
                v.performClick();

                break;
            case MotionEvent.ACTION_MOVE:

                //When resizing the view
                if (protoCanvas.isThumbPressed()){
                    protoCanvas.trackThumbAndResize(event.getRawX(), event.getRawY() - protoCanvasAbsolutePosition[1]);
                    return true;
                } else if (v == scrollView) {
                    return scrollView.onTouchEvent(event); //prevent moving the canvas}
                }

                //notify GUI changes
                changeOccurred = true;

                //When moving the view
                //real time coordinates
                float X = event.getRawX() + _xDelta;
                float Y = event.getRawY() + _yDelta;

                //constrain X movement to screen width
                if (X <= 0) X = 0;
                else if (X + v.getWidth() >= screen_width) X = screen_width - v.getWidth();

                //pointer and lock point
                pointerX = X + v.getWidth() / 2.0f;
                pointerY = Y + v.getHeight() / 2.0f;
                lockX = Math.round(pointerX / display_unit) * display_unit - v.getWidth() / 2.0f;
                lockY = Math.round(pointerY / display_unit) * display_unit - v.getHeight() / 2.0f;
                //show guide lines
                protoCanvas.drawPrecisionLines(pointerX, pointerY - scroll_offset);
                protoCanvas.showResizingBorders(v, scroll_offset, v != scrollView);
                //position the view

                //TODO: add auto-scroll functionality
                // if (event.getRawY() > App.screen_height * 0.95) {
                //     scroll_integral += 7;
                //     scrollView.scrollBy(0,7);
                // }
                // v.setY(lockY + scroll_integral);

                v.setX(lockX + loss_offset / 2.0f);
                v.setY(lockY);

                break;
                }
        return true;
    }

    private void editWidget(View v) {
        //temporarily save the widget's coordinates
        float tempX = v.getX();
        float tempY = v.getY();
        //use copy constructor to avoid mutations
        ProtoViewAttrs attributes = new ProtoViewAttrs(((ProtoView) v).getAttrs());
        //center widget in preview
        attributes.setX(0);
        attributes.setY(0);
        NewWidgetPreferencesDialog dialog =
                new NewWidgetPreferencesDialog(attributes, true);
        dialog.show(getParentFragmentManager(), "edit_widget");
        //called when a widget is modified
        dialog.setOnWidgetAddedCallback(attrs -> {
            //remove the old widget
            viewList.remove(v);
            controller_layout.removeView(v);
            hideWidgetSelectionBorders();
            //render the edited widget and add it to GUI
            View edited = ViewBuilder.generateViewFromAttrs(attrs, mContext);
            ((ProtoView)edited).setViewInMode(ProtoView.MODE_NO_TOUCH);
            //restore coordinates
            edited.setX(tempX);
            edited.setY(tempY);
            edited.setOnTouchListener(this);
            viewList.add(edited);
            controller_layout.addView(edited);
            //notify GUI changes
            changeOccurred = true;
        });
    }

    private void showTutorial() {
        //tutorial
        TutorialFlow.showFlowFor(
                getActivity(),
                new View[]{add_btn, edit_btn, remove_btn, save_btn},
                new String[]{
                        getString(R.string.tuto_add_widgets),
                        getString(R.string.tuto_edit_widgets),
                        getString(R.string.tuto_remove_widgets),
                        getString(R.string.tuto_save_gui)
                }, "3");
    }

    private void showExitDialog(){
        if (changeOccurred) {
            new AlertDialog.Builder(mContext)
                    .setMessage("Would you like to save the current changes ?")
                    .setNegativeButton("No, discard", (dialog1, which) -> navController.navigateUp())
                    .setPositiveButton("Yes", (dialog1, which) -> {
                        Snackbar.make(controller_layout, "Saving...", Snackbar.LENGTH_SHORT).show();
                        attrsController.deleteList(removedViewsList);
                        attrsController.insertAll(
                                viewList,
                                /*On finish*/ () -> navController.navigateUp());
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        } else {
            navController.navigateUp();
        }
    }


    private final OnBackPressedCallback backPressCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            showExitDialog();
        }
    };


}
