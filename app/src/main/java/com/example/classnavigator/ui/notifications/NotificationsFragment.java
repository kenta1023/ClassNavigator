package com.example.classnavigator.ui.notifications;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.classnavigator.R;
import com.example.classnavigator.databinding.FragmentNotificationsBinding;
import com.example.classnavigator.TimetableDbHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = root.findViewById(R.id.listView);
        showTimetableData();

        Button addButton = root.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 追加ボタンがクリックされた時の処理
                addTimetableData();
                showTimetableData();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showTimetableData() {
        String[] daysOfWeek = {"月", "火", "水", "木", "金", "土", "日"};
        String[] convertedDaysOfWeek = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        TimetableDbHelper helper = new TimetableDbHelper(getContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] projection = {
                "id",
                "day_of_week",
                "period",
                "subject",
                "classroom"
        };
        Cursor cursor = db.query(
                "Timetable",
                projection,
                null,
                null,
                null,
                null,
                null
        );
        List<String> data = new ArrayList<>();
        List<Integer> id_list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String subject = cursor.getString(cursor.getColumnIndexOrThrow("subject"));
            String classroom = cursor.getString(cursor.getColumnIndexOrThrow("classroom"));
            String dayOfWeek = cursor.getString(cursor.getColumnIndexOrThrow("day_of_week"));
            dayOfWeek = daysOfWeek[Arrays.asList(convertedDaysOfWeek).indexOf(dayOfWeek)];//日本語から英語に変換
            String period = cursor.getString(cursor.getColumnIndexOrThrow("period"));

            String item = dayOfWeek+ "曜" + period + "限: " + subject + classroom;

            data.add(item);
            id_list.add(id);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // リストアイテムがクリックされた時の処理
                deleteData(id_list, position);
                showTimetableData();
            }
        });

        cursor.close();
        db.close();
    }


    private void addTimetableData() {
        String[] daysOfWeek = {"月", "火", "水", "木", "金", "土", "日"};
        String[] convertedDaysOfWeek = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        EditText editSubject = binding.editSubject;
        EditText editClassRoom = binding.editClassRoom;
        Spinner spinnerDayOfWeek = binding.spinnerDayOfWeek;
        Spinner spinnerPeriod = binding.spinnerPeriod;

        String subject = editSubject.getText().toString().trim();
        String classRoom = editClassRoom.getText().toString().trim();
        String dayOfWeek = spinnerDayOfWeek.getSelectedItem().toString();
        dayOfWeek = convertedDaysOfWeek[Arrays.asList(daysOfWeek).indexOf(dayOfWeek)];//日本語から英語に変換
        int period = Integer.parseInt(spinnerPeriod.getSelectedItem().toString());

        TimetableDbHelper helper = new TimetableDbHelper(getContext());
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TimetableDbHelper.COLUMN_SUBJECT, subject);
        values.put(TimetableDbHelper.COLUMN_CLASSROOM, classRoom);
        values.put(TimetableDbHelper.COLUMN_DAY_OF_WEEK, dayOfWeek);
        values.put(TimetableDbHelper.COLUMN_PERIOD, period);

        long newRowId = db.insert(TimetableDbHelper.TABLE_NAME, null, values);

        if (newRowId != -1) {
            Toast.makeText(getContext(), "データ登録完了", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "データの登録に失敗しました。", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    private void deleteData(List<Integer> id_list, int position) {
        //id_listは表示されるidが順に入れられたリスト,positionは何番目に表示されたか→id_list[position]は選択されたデータのidの値
        TimetableDbHelper helper = new TimetableDbHelper(getContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("Timetable", "id=?", new String[]{String.valueOf(id_list.get(position))});
        db.close();
    }
}