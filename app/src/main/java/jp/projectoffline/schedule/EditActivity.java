package jp.projectoffline.schedule;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {
    public static final int RESULT_OK_ADD = 1;
    public static final int RESULT_OK_EDIT = 2;
    public static final int RESULT_OK_DELETE = 3;


    private Spinner spinner;
    private Button deleteButton;
    private EditText editClassName;
    private EditText editClassRoom;
    private EditText editProfName;
    private TextView startTimeView;
    private TextView endTimeView;
    private Schedule schedule;

    private int mode;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Dark theme
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", true)){
            setTheme(R.style.AppTheme_Dark);
        }
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinner = findViewById(R.id.spinner);
        Button addButton = findViewById(R.id.button_add);
        deleteButton = findViewById(R.id.button_delete);
        editClassName = findViewById(R.id.editText);
        editClassRoom = findViewById(R.id.editText2);
        editProfName = findViewById(R.id.editText3);
        startTimeView = findViewById(R.id.textView);
        endTimeView = findViewById(R.id.textView2);

        schedule = new Schedule();

        //Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter.add(getString(R.string.monday));
        adapter.add(getString(R.string.tuesday));
        adapter.add(getString(R.string.wednesday));
        adapter.add(getString(R.string.thursday));
        adapter.add(getString(R.string.friday));

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                //Selected
                schedule.setDay(position);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                //Not selected
            }
        });
        //Start Time
        startTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog dialog = new TimePickerDialog(EditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                        if (minutes < 10) {
                            startTimeView.setText(hours + ":0" + minutes);
                        } else {
                            startTimeView.setText(hours + ":" + minutes);
                        }
                        schedule.getStartTime().setHour(hours);
                        schedule.getStartTime().setMinute(minutes);
                    }
                }, schedule.getStartTime().getHour(), schedule.getStartTime().getMinute(), true);
                dialog.show();
            }
        });
        //End Time
        endTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog dialog = new TimePickerDialog(EditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                        if (minutes < 10) {
                            endTimeView.setText(hours + ":0" + minutes);
                        } else {
                            endTimeView.setText(hours + ":" + minutes);
                        }
                        schedule.getEndTime().setHour(hours);
                        schedule.getEndTime().setMinute(minutes);
                    }
                }, schedule.getEndTime().getHour(), schedule.getEndTime().getMinute(), true);
                dialog.show();
            }
        });

        schedule.setStartTime(new Time(9,0));
        schedule.setEndTime(new Time(10,30));
        //Add schedule
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                schedule.setClassTitle(editClassName.getText().toString());
                schedule.setClassPlace(editClassRoom.getText().toString());
                schedule.setProfessorName(editProfName.getText().toString());
                ArrayList<Schedule> schedules = new ArrayList<Schedule>();
                if(mode == MainActivity.REQUEST_ADD){
                    Intent intent = new Intent();
                    schedules.add(schedule);
                    intent.putExtra("schedules",schedules);
                    setResult(RESULT_OK_ADD,intent);
                    finish();
                }
                else if(mode == MainActivity.REQUEST_EDIT){
                    Intent intent = new Intent();
                    schedules.add(schedule);
                    intent.putExtra("idx",index);
                    intent.putExtra("schedules",schedules);
                    setResult(RESULT_OK_EDIT,intent);
                    finish();
                }
            }
        });
        //Delete schedule
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("idx",index);
                setResult(RESULT_OK_DELETE, intent);
                finish();
            }
        });

        Intent intent = getIntent();
        mode = intent.getIntExtra("mode",MainActivity.REQUEST_ADD);
        if(mode == MainActivity.REQUEST_EDIT){
            loadScheduleData();
            deleteButton.setVisibility(View.VISIBLE);

            int startTimeHour = schedule.getStartTime().getHour();
            int endTimeHour = schedule.getEndTime().getHour();
            int startTimeMin = schedule.getStartTime().getMinute();
            int endTimeMin = schedule.getEndTime().getMinute();

            //16:5 -> 16:05
            if (startTimeMin < 10) {
                startTimeView.setText(startTimeHour + ":0" + startTimeMin);
            } else {
                startTimeView.setText(startTimeHour + ":" + startTimeMin);
            }
            if (endTimeMin < 10) {
                endTimeView.setText(endTimeHour + ":0" + endTimeMin);
            } else {
                endTimeView.setText(endTimeHour + ":" + endTimeMin);
            }
        }
    }
    //Load schedule data
    private void loadScheduleData(){
        Intent intent = getIntent();
        index = intent.getIntExtra("idx",-1);
        ArrayList<Schedule> schedules = (ArrayList<Schedule>)intent.getSerializableExtra("schedules");
        schedule = schedules.get(0);
        //editText
        editClassName.setText(schedule.getClassTitle());
        editClassRoom.setText(schedule.getClassPlace());
        editProfName.setText(schedule.getProfessorName());
        //Spinner
        spinner.setSelection(schedule.getDay());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
