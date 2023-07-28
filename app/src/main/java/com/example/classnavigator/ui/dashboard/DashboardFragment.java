package com.example.classnavigator.ui.dashboard;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.res.Resources;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.classnavigator.TimetableDbHelper;
import com.example.classnavigator.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setTable(root);

        return root;
    }

    private void setTable(View rootView) {
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
        if (rootView != null) {
            Resources resources = getResources();
            String packageName = getContext().getPackageName();

            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String dayOfWeek = cursor.getString(cursor.getColumnIndexOrThrow("day_of_week"));
                int period = cursor.getInt(cursor.getColumnIndexOrThrow("period"));
                String subject = cursor.getString(cursor.getColumnIndexOrThrow("subject"));
                String classroom = cursor.getString(cursor.getColumnIndexOrThrow("classroom"));

                // ログにデータを表示
                //String logMessage = "ID: " + id + ", Day of Week: " + dayOfWeek + ", Period: " + period + ", Subject: " + subject + ", classroom: " + classroom;
                //Log.d("Timetable add", logMessage);

                // UI上にデータを表示
                String textViewId = dayOfWeek + period; // TextViewのidを生成
                int resId = resources.getIdentifier(textViewId, "id", packageName); // リソースIDを取得
                TextView textView = rootView.findViewById(resId); // TextViewを取得
                textView.setText(subject + "\n" + classroom);
            }
        } else {
            Log.e("Timetable", "rootView is null");
        }

        cursor.close();
        db.close(); // データベースを閉じる
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}