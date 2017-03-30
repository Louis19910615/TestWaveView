package louis.testwaveview;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import louis.testwaveview.ui.SurfaceWaveView;
import louis.testwaveview.ui.managers.WaveTransitionManager;
import louis.testwaveview.utils.UiUtils;

public class WaveDemoActivity extends Activity {

    private Button mCtrlWaveBtn1;
    private Button mCtrlWaveBtn2;
    private Button mCtrlWaveBtn3;
    private SurfaceWaveView mSurfaceWaveView;
    private WaveTransitionManager mWaveTransitionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wave_demo_layout);

        mSurfaceWaveView = (SurfaceWaveView) findViewById(R.id.surface_wave_view);

        mCtrlWaveBtn1 = (Button) findViewById(R.id.ctrl_wave_btn1);
        mCtrlWaveBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWaveTransitionManager.offer1();
            }
        });

        mCtrlWaveBtn2 = (Button) findViewById(R.id.ctrl_wave_btn2);
        mCtrlWaveBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWaveTransitionManager.offer2();
            }
        });

        mCtrlWaveBtn3 = (Button) findViewById(R.id.ctrl_wave_btn3);
        mCtrlWaveBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWaveTransitionManager.offer3();
            }
        });

        mWaveTransitionManager = new WaveTransitionManager(mSurfaceWaveView);
        mWaveTransitionManager.startThread();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("WaveDemoActivity", "onDestroy");
        mWaveTransitionManager.stopThread();
    }
}
