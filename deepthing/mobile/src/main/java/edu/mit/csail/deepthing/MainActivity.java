package edu.mit.csail.deepthing;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.view.ViewGroup.LayoutParams;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";
    Button buttonClick;
    Preview preview;
    Camera camera;
    Activity act;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // must before setContentView
        setContentView(R.layout.activity_main);

        ctx = this;
        act = this;

        preview = new Preview(this, (SurfaceView)findViewById(R.id.surfaceView));
        preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        ((FrameLayout) findViewById(R.id.layout)).addView(preview);
        preview.setKeepScreenOn(true);

        /**
        preview.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
            }
        });
         **/

    }

    @Override
    protected void onResume() {
        super.onResume();
        int numCams = Camera.getNumberOfCameras();
        if(numCams > 0){
            try{
                camera = Camera.open(0);
                camera.startPreview();
                preview.setCamera(camera);
            } catch (RuntimeException ex){
                Log.e(TAG, "Camera not found");
            }
        }
    }

    @Override
    protected void onPause() {
        if(camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
            preview.setCamera(null);
        }
        super.onPause();
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
