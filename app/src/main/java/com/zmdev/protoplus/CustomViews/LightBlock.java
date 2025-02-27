package com.zmdev.protoplus.CustomViews;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentManager;

import com.zmdev.protoplus.App;
import com.zmdev.protoplus.R;
import com.zmdev.protoplus.WidgetsPreferences.SensorFrequencyPref;
import com.zmdev.protoplus.db.Entities.AttrsAndCommand;
import com.zmdev.protoplus.db.Entities.Command;
import com.zmdev.protoplus.db.Entities.Parameter;
import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;

import org.jetbrains.annotations.NotNull;

import static com.zmdev.protoplus.CustomViews.ProtoView.Type.VIEW_TYPE_LIGHT;

public class LightBlock extends LinearLayout
        implements ProtoView, CompoundButton.OnCheckedChangeListener, SensorEventListener {

    private int sampling_rate = 100*1000; //100ms
    private SensorManager sensorManager;
    private Sensor sensor_gyro;
    private final Command linkedCommand;
    private final ProtoViewAttrs mAttrs;
    private int paramIndex;
    private Parameter[] parameters;
    private TextView data_text;
    private SwitchCompat aSwitch;
    private OnCommandExecutedListener callback;
    private boolean isPreview = false;

    public LightBlock(@NonNull Context context, AttrsAndCommand attrs) {
        super(context);
        mAttrs = attrs.getAttrs();
        if (mAttrs.getLinkedCommandID() == DEF_COMMAND) linkedCommand = getDefCommand();
        else linkedCommand = attrs.getCommand();
        init(context);
    }


    private void init(Context context){
        //inflate layout
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.sensor_light_layout, this);

        setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        setX(mAttrs.getX());
        setY(mAttrs.getY());

        // Im using the MAX progress variable to hold the sampling rate, to avoid adding
        // more code and newer variables to the database
        sampling_rate = (int) mAttrs.getMaxProgress();


        //setup sensors stuff
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        sensor_gyro = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        data_text = findViewById(R.id.sensor_data);
        aSwitch = findViewById(R.id.sensor_switch);
        aSwitch.setOnCheckedChangeListener(this);

        if (linkedCommand != null) {
            parameters = linkedCommand.getParams();
            for (int i = 0; i < parameters.length ; i++) {
                if (parameters[i].getIsVariable() == 1) {
                    paramIndex = i;
                    break;
                }
            }
        }
    }

    @Override
    public ProtoViewAttrs getAttrs() {
        mAttrs.setX(getX());
        mAttrs.setY(getY());
        return mAttrs;
    }

    public static ProtoViewAttrs getPreviewAttrs() {
        ProtoViewAttrs attrs = new ProtoViewAttrs();
        attrs.setViewType(VIEW_TYPE_LIGHT);
        attrs.setPreviewTitle("Light sensor");
        attrs.setX(0);
        attrs.setY(0);
        return attrs;
    }

    @Override
    public void setPrimaryColor(int color) {

    }

    @Override
    public void setSecondaryColor(int color) {

    }

    @Override
    public void setPrimaryText(String text) {

    }

    @Override
    public void setDrawable(int id) {

    }

    @Override
    public void setMinProgress(double min) {
    }

    @Override
    public void setMaxProgress(double max) {
        // Im using the MAX progress variable to hold the sampling rate, to avoid adding
        // more code and newer variables to the database
        sampling_rate = (int) max;
        mAttrs.setMaxProgress(max);
    }

    @Override
    public void setShowPercentage(boolean show) {

    }

    @Override
    public int getVarParamsNbr() {
        return 3;
    }

    @Override
    public void setViewInMode(int mode) {
        isPreview = true;
    }

    @NotNull
    @Override
    public Command getDefCommand() {
        Command cmnd = new Command(0,"lx",null,
                new Parameter[]{
                        new Parameter("0","intensity", 1,DEF_COMMAND),
                });
        cmnd.setId(DEF_COMMAND);
        return cmnd;
    }

    @Override
    public void setSize(int width, int height) {

    }

    @Override
    public View[] getWidgetPreferences(FragmentManager fm) {
        return new View[]{
                new SensorFrequencyPref(getContext(),this)
        };
    }

    @Override
    public void setOnCommandExecutedListener(OnCommandExecutedListener listener) {
        callback = listener;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isPreview) return;
        if (isChecked) sensorManager.registerListener(this, sensor_gyro,sampling_rate);
        else sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isPreview) return; //this will never happen , but just to be sure
        float[] val = event.values;
        data_text.setText(String.format("Intensity\n%d\nLux", (int)val[0]));
        parameters[paramIndex].setDefValue(String.format("%d",(int)val[0]));
        linkedCommand.setParams(parameters);
        callback.onCommandExecuted(linkedCommand);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        sensorManager.unregisterListener(this);
    }

    @Override
    public int[] getViewDetailsArray(){
        return new int[]{
                R.string.doc_light_desc,
                R.string.doc_light_reqs,
                R.string.doc_light_usage
        };
    }
}
