
package louis.testwaveview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import louis.testwaveview.R;

public class MainActivity extends Activity implements OnClickListener {

    private View mWaveDemo;
    private View mXfermode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        mWaveDemo = findViewById(R.id.wave_demo);
        mWaveDemo.setOnClickListener(this);

        mXfermode = findViewById(R.id.xfermode_demo);
        mXfermode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mWaveDemo) {
            Intent intent = new Intent(this, WaveDemoActivity.class);
            startActivity(intent);
        } else if (v == mXfermode) {
            Intent intent = new Intent(this, PorterDuffXfermodeActivity.class);
            startActivity(intent);
        }
    }

}
