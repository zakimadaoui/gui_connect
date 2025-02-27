package com.zmdev.protoplus.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.zmdev.protoplus.Dialogs.ProtoDialog;
import com.zmdev.protoplus.R;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;

import java.util.concurrent.Executors;


public class TutorialFlow {

    public static final String TUTOS_SP_FILE = "com.proto.tutorials_preferences";
    public static final String TUTOS_SP_NBR_ = "TUTOS_SP_NBR_"; //true means show the tuto, false means do not
    private static final int TUTOS_SP_TOTAL_NBR = 6; //nbr of calls to showFlowFor() functions
    public static boolean keepShowingTutos = false;
    private static SharedPreferences tutorialsSp;


    // must be called in App.onCreate() first before you call anything else
    public static void init(Context context) {
        tutorialsSp = context.getSharedPreferences(TUTOS_SP_FILE, Context.MODE_PRIVATE);
        int trueCount = 0;
        for (int i = 0; i < TUTOS_SP_TOTAL_NBR; i++) {
            if (tutorialsSp.getBoolean(TUTOS_SP_NBR_.concat(String.valueOf(i)), true)) trueCount++;
        }
        keepShowingTutos = (trueCount != 0);
    }

    public static void showFlowFor(Activity activity, View[] view, String[] detail, String tutoID) {

        if (!keepShowingTutos) return;
        if (!tutorialsSp.getBoolean(TUTOS_SP_NBR_.concat(tutoID),true)) return;

        TapTarget[] tapTargets = new TapTarget[view.length];
        Executors.newSingleThreadExecutor().execute(() -> {
            for (int i = 0; i < view.length; i++) {
                tapTargets[i] = TapTarget.forView(view[i], detail[i])
                        .outerCircleColor(R.color.app_color)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.7f)            // Specify the alpha amount for the
                        // outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .descriptionTextSize(18)            // Specify the size (in sp) of the description text
                        .textColor(R.color.white)            // Specify a color for both the title and description text
                        .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)
                        .textTypeface(Typeface.SANS_SERIF)
                        .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
                        .targetRadius(60)                  // Specify the target radius (in dp)
                ;
            }
            new Handler(Looper.getMainLooper()).post(() -> {
                new TapTargetSequence(activity).targets(tapTargets).start();
                //don't forget to save the SP for tuto after showing it
                tutorialsSp.edit().putBoolean(TUTOS_SP_NBR_.concat(tutoID),false).apply();
            });
        });
    }

    public static void showFlowFor(Activity activity, View view, String detail, String tutoID) {

        //exit if tutorials are disabled or this one is already shown
        if (!keepShowingTutos) return;
        if (!tutorialsSp.getBoolean(TUTOS_SP_NBR_.concat(tutoID),true)) return;
        //show the bubble
        TapTargetView.showFor(activity,
                TapTarget.forView(view, detail)
                        .outerCircleColor(R.color.app_color)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.7f)            // Specify the alpha amount for the
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .descriptionTextSize(18)            // Specify the size (in sp) of the description text
                        .textColor(R.color.white)            // Specify a color for both the title and description text
                        .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .textTypeface(Typeface.SANS_SERIF)
                        .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
                        .targetRadius(60)                  // Specify the target radius (in dp)
                );
        //don't forget to save the SP for tuto after showing it
        tutorialsSp.edit().putBoolean(TUTOS_SP_NBR_.concat(tutoID),false).apply();

    }

    public static void showPersistentTutorialsDialog(Context context) {
        if (!tutorialsSp.getBoolean(TUTOS_SP_NBR_.concat("5"),true)) return;
        ProtoDialog dialog = new ProtoDialog(context);
        dialog.setTitle("Quick Guide");
        dialog.setMessage("If this is your fist time using GuiConnect+, we recommend following the \"Getting started tutorial\".");
        dialog.addPositiveButton("Open tutorial", (dialog1, which) -> {
            context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://github.com/zakimadaoui/GuiConnectHelper/blob/master/docs/Getting%20started%20with%20GuiConnect%2B.md")));
        });
        dialog.addNegativeButton("Remind me later", null);
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Don't show again", (dialog12, which) -> {
            tutorialsSp.edit().putBoolean(TUTOS_SP_NBR_.concat("5"),false).apply();
        });
        dialog.show();
    }

    public static void setTutorialsEnabled(boolean enable){
        // true for enabling all
        // false for disabling all
        SharedPreferences.Editor editor = tutorialsSp.edit();
        for (int i = 0; i < TUTOS_SP_TOTAL_NBR; i++) {
            editor.putBoolean(TUTOS_SP_NBR_.concat(String.valueOf(i)), enable);
        }
        editor.apply();
        keepShowingTutos = enable;
    }
}
