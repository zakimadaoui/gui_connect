package com.zmdev.protoplus;

import android.app.Application;
import android.util.DisplayMetrics;

import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.maltaisn.iconpack.mdi.IconPackMdi;
import com.zmdev.protoplus.Utils.ThemeUtils;
import com.zmdev.protoplus.Utils.TutorialFlow;
import com.maltaisn.icondialog.pack.IconPack;
import com.maltaisn.icondialog.pack.IconPackLoader;

public class App extends Application {

    private static final String TAG = "App";
    public static int        MAX_WIDGET_SIZE;
    public static int        screen_width;
    public static int        screen_height;
    public static int        canvas_height  =-1;
    public static double density_dpi;
    public static double density_px;
    public static double density_sp;
    public static double xdpi;
    public static double ydpi;
    public static IconPack iconPack;

    public static float display_unit;
    public static float unit_div = 40; //the goal for this is to get width/40= odd 4
    public static int loss_offset ;
    public static int WORD_SIZE = 10;

    @Override
    public void onCreate() {
        super.onCreate();

        calculateDisplayMeasures();
        TutorialFlow.init(getApplicationContext());
        IconPackLoader loader = new IconPackLoader(getApplicationContext());
        iconPack = IconPackMdi.createMaterialDesignIconPack(loader);//pre-made 'community' icon pack
        iconPack.loadDrawables(loader.getDrawableLoader());
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_mode", false)) {
            ThemeUtils.themeID = R.style.Theme_ProtoDark;
            ThemeUtils.dialogThemeID = R.style.AlertDialogDarkTheme;
            ThemeUtils.activityDialogThemeID = R.style.ActivityDialogThemeDark;
            ThemeUtils.bottomSheetThemeID = R.style.BottomSheetThemeDark;
            ThemeUtils.backgroundColor = ContextCompat.getColor(this, R.color.card_color_dark);
            ThemeUtils.backgroundColorVariant = ContextCompat.getColor(this, R.color.widget_back_shadow_color_dark);;
            ThemeUtils.shadowColor = ContextCompat.getColor(this, R.color.widget_shadow_color_dark);
            ThemeUtils.canvasDotsColor = ContextCompat.getColor(this, R.color.canvas_dot_color_dark);
            ThemeUtils.canvasPointerColor = ContextCompat.getColor(this, R.color.canvas_pointer_color_dark);
            ThemeUtils.canvasSelectorColor = ContextCompat.getColor(this, R.color.canvas_selector_color_dark);
        } else {
            ThemeUtils.themeID = R.style.Theme_Proto;
            ThemeUtils.dialogThemeID = R.style.AlertDialogLightTheme;
            ThemeUtils.activityDialogThemeID = R.style.ActivityDialogThemeLight;
            ThemeUtils.bottomSheetThemeID = R.style.BottomSheetThemeLight;
            ThemeUtils.backgroundColor = ContextCompat.getColor(this, R.color.card_color_light);
            ThemeUtils.backgroundColorVariant = ContextCompat.getColor(this, R.color.widget_back_shadow_color_light);
            ThemeUtils.shadowColor = ContextCompat.getColor(this, R.color.widget_shadow_color_light);
            ThemeUtils.canvasDotsColor = ContextCompat.getColor(this, R.color.canvas_dot_color_light);
            ThemeUtils.canvasPointerColor = ContextCompat.getColor(this, R.color.canvas_pointer_color_light);
            ThemeUtils.canvasSelectorColor = ContextCompat.getColor(this, R.color.canvas_selector_color_light);
        }
    }

    void calculateDisplayMeasures() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screen_width = displayMetrics.widthPixels;
        screen_height = displayMetrics.heightPixels ;
        MAX_WIDGET_SIZE = (int) (screen_width * 0.95);
        display_unit = (int)(screen_width/unit_div);
        // we will always use either all pixels or a bit less due to integer arithmetic removing the
        // digits after the decimal point. So we can calculate pixels loss and adjust for it
        // by offsetting the grid with half the loss ! (may loose another pixel here but it is insignificant)
        //= (int) (screen_width % unit_div);
        int pixel_loss = (int) (screen_width % unit_div);
        loss_offset = pixel_loss / 2;
        density_dpi = displayMetrics.densityDpi;
        density_sp = displayMetrics.scaledDensity;
        density_px = displayMetrics.density;
        xdpi = displayMetrics.xdpi;
        ydpi = displayMetrics.ydpi;
    }

}
