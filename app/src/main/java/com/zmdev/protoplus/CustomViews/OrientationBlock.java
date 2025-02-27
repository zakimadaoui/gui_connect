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

import com.zmdev.protoplus.R;
import com.zmdev.protoplus.WidgetsPreferences.SensorFrequencyPref;
import com.zmdev.protoplus.db.Entities.AttrsAndCommand;
import com.zmdev.protoplus.db.Entities.Command;
import com.zmdev.protoplus.db.Entities.Parameter;
import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;

import org.jetbrains.annotations.NotNull;

import static com.zmdev.protoplus.CustomViews.ProtoView.Type.VIEW_TYPE_ACC;
import static com.zmdev.protoplus.CustomViews.ProtoView.Type.VIEW_TYPE_GYRO;
import static com.zmdev.protoplus.CustomViews.ProtoView.Type.VIEW_TYPE_ORIENTATION;


public class OrientationBlock extends LinearLayout
        implements ProtoView, CompoundButton.OnCheckedChangeListener, SensorEventListener {

    private int sampling_rate = 100 * 1000; //100ms
    private SensorManager sensorManager;
    private final Command linkedCommand;
    private final ProtoViewAttrs mAttrs;
    private final int[] paramIndex = new int[3];
    private Parameter[] parameters;
    private TextView data_text;
    private SwitchCompat aSwitch;
    private OnCommandExecutedListener callback;
    private boolean isPreview = false;


    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];
    private Sensor accelerometer;
    private Sensor magneticField;
    private final float TO_DEGREES = (float) (180 / Math.PI);
    // TODO: http://plaw.info/articles/sensorfusion/


    public OrientationBlock(@NonNull Context context, AttrsAndCommand attrs) {
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

        inflater.inflate(R.layout.sensor_orientation_layout, this);

        setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        setX(mAttrs.getX());
        setY(mAttrs.getY());

        // Im using the MAX progress variable to hold the sampling rate, to avoid adding
        // more code and newer variables to the database
        sampling_rate = (int) mAttrs.getMaxProgress();


        //setup sensors stuff
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        data_text = findViewById(R.id.sensor_data);
        aSwitch = findViewById(R.id.sensor_switch);
        aSwitch.setOnCheckedChangeListener(this);

        if (linkedCommand != null) {
            parameters = linkedCommand.getParams();
            int count = 0;
            for (int i = 0; i < parameters.length ; i++) {
                if (parameters[i].getIsVariable() == 1) paramIndex[count++] = i;
                if (count == 3) break;
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
        attrs.setViewType(VIEW_TYPE_ORIENTATION);
        attrs.setPreviewTitle("Orientation block");
        attrs.setX(0);
        attrs.setY(0);
        attrs.setProWidget(true);
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
        Command cmnd = new Command(0,"apr",null,
                new Parameter[]{
                        new Parameter("0","azimuth", 1,DEF_COMMAND),
                        new Parameter("0","pitch", 1,DEF_COMMAND),
                        new Parameter("0","roll", 1,DEF_COMMAND)
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
        if (isChecked) {
            sensorManager.registerListener(this, accelerometer, sampling_rate);
            sensorManager.registerListener(this, magneticField, sampling_rate);
        } else {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isPreview) return; //this will never happen , but just to be sure

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading,
                    0, accelerometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);
        }

        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);
        //update the device's orientation angles
        SensorManager.getOrientation(rotationMatrix, orientationAngles);

        orientationAngles[0] = orientationAngles[0] * TO_DEGREES;
        orientationAngles[1] = orientationAngles[1] * TO_DEGREES;
        orientationAngles[2] = orientationAngles[2] * TO_DEGREES;

        data_text.setText(String.format("a: %+06.1f\np: %+06.1f\nr: %+06.1f", orientationAngles[0],orientationAngles[1],orientationAngles[2]));
        parameters[paramIndex[0]].setDefValue(String.format("%.1f",orientationAngles[0]));
        parameters[paramIndex[1]].setDefValue(String.format("%.1f",orientationAngles[1]));
        parameters[paramIndex[2]].setDefValue(String.format("%.1f",orientationAngles[2]));
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
                R.string.doc_orientation_desc,
                R.string.doc_orientation_reqs,
                R.string.doc_orientation_usage
        };
    }

}