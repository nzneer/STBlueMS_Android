package com.st.BlueMS.demos.COSensor;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.st.BlueMS.R;
import com.st.BlueSTSDK.Feature;
import com.st.BlueSTSDK.Features.FeatureCOSensor;
import com.st.BlueSTSDK.Node;
import com.st.BlueSTSDK.gui.demos.DemoDescriptionAnnotation;
import com.st.BlueSTSDK.gui.demos.DemoFragment;


@DemoDescriptionAnnotation(name = "CO Sensor",iconRes = R.drawable.co_sensor_icon,
        requareAll = {FeatureCOSensor.class})
public class COSensorDemoFragment extends DemoFragment implements
        SetSensitivityDialogFragment.SetSensitivityDialogFragmentCallback {

    private static final String SET_SENSITIVITY_DIALOG_TAG=COSensorDemoFragment.class.getCanonicalName() + ".TAG";

    private TextView mCOValueText;
    private float mCurrentSensitivity;

    private FeatureCOSensor.FeatureCOSensorListener mFeatureListener = new FeatureCOSensor.FeatureCOSensorListener(){

        @Override
        public void onUpdate(Feature f, Feature.Sample sample) {
            float gasConcentration = FeatureCOSensor.getGasPresence(sample);
            updateGui(()-> mCOValueText.setText(getString(R.string.coSensor_numberFormat,gasConcentration)));
        }

        @Override
        public void onSensorSensitivityRead(@NonNull FeatureCOSensor feature, float sensitivity) {
            mCurrentSensitivity = sensitivity;
        }
    };

    private FeatureCOSensor mCOSensor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_co_sensor, container, false);
        mCOValueText = root.findViewById(R.id.coSensor_sensorValue);

        TextView dataUnit = root.findViewById(R.id.coSensor_valueUnit);
        dataUnit.setText(FeatureCOSensor.FEATURE_UNIT);
        return root;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_co_sensor_demo, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.coSensor_menu_setSensitivity) {
            displaySetSensitivityDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void enableNeededNotification(@NonNull Node node) {
        mCOSensor = node.getFeature(FeatureCOSensor.class);

        if(mCOSensor!=null){
            mCOSensor.addFeatureListener(mFeatureListener);
            node.enableNotification(mCOSensor);
            mCOSensor.requestSensitivity();
        }
    }

    @Override
    protected void disableNeedNotification(@NonNull Node node) {
        if(mCOSensor!=null){
            mCOSensor.removeFeatureListener(mFeatureListener);
            node.disableNotification(mCOSensor);
        }
    }

    private void displaySetSensitivityDialog(){
        DialogFragment dialog = SetSensitivityDialogFragment.newInstance(mCurrentSensitivity);
        dialog.show(getChildFragmentManager(),SET_SENSITIVITY_DIALOG_TAG);
    }

    @Override
    public void onNewSensitivity(float sensitivity) {
        if(mCOSensor!=null){
            mCOSensor.setSensorSensitivity(sensitivity);
        }
    }
}
