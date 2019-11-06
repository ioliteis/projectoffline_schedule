package jp.projectoffline.schedule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.TimetableView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_ADD = 1;
    public static final int REQUEST_EDIT = 2;

    private TimetableView timetable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Dark theme
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", true)){
            setTheme(R.style.AppTheme_Dark);
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        timetable = findViewById(R.id.timetable);

        loadSavedData();

        timetable.setOnStickerSelectEventListener(new TimetableView.OnStickerSelectedListener() {
            @Override
            public void OnStickerSelected(int idx, ArrayList<Schedule> schedules) {
                Intent intent = new android.content.Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("mode",REQUEST_EDIT);
                intent.putExtra("idx", idx);
                intent.putExtra("schedules", schedules);
                startActivityForResult(intent,REQUEST_EDIT);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new android.content.Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("mode",REQUEST_ADD);
                startActivityForResult(intent,REQUEST_ADD);
            }
        });
    }
    //Load
    private void loadSavedData(){
        timetable.removeAll();
        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(this);
        String scheduleData = data.getString("timetable_demo","");
        if(scheduleData != null && !scheduleData.equals("")) {
            timetable.load(scheduleData);
        }
    }
    //Save
    private void savedDataPreference(String savedData) {
        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = data.edit();
        editor.putString("timetable_demo", savedData);
        editor.apply();
    }

    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode){
            case REQUEST_ADD:
                if(resultCode == EditActivity.RESULT_OK_ADD){
                    ArrayList<Schedule> item = (ArrayList<Schedule>)intent.getSerializableExtra("schedules");
                    timetable.add(item);
                }
                savedDataPreference(timetable.createSaveData());
                loadSavedData();
                break;
            case REQUEST_EDIT:
                if(resultCode == EditActivity.RESULT_OK_EDIT){
                    int idx = intent.getIntExtra("idx",-1);
                    ArrayList<Schedule> item = (ArrayList<Schedule>)intent.getSerializableExtra("schedules");
                    timetable.edit(idx,item);
                }
                else if(resultCode == EditActivity.RESULT_OK_DELETE){
                    int idx = intent.getIntExtra("idx",-1);
                    timetable.remove(idx);
                }
                savedDataPreference(timetable.createSaveData());
                loadSavedData();
                break;
        }
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
            //intent
            Intent intent = new android.content.Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_about) {
            //intent
            Intent intent = new android.content.Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
