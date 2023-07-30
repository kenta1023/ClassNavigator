package com.example.classnavigator.ui.home;


import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.classnavigator.R;
import com.example.classnavigator.TimetableDbHelper;
import com.example.classnavigator.databinding.FragmentHomeBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private final Handler handler = new Handler();
    private Runnable runnable;

    private String lastFetchedDate = ""; // データを最後に取得した日付を保持する変数
    private String[] subjects;
    private String[] classrooms;
    private String[] periods;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // データ取得(グローバルで定義したものを使用)
        TimetableData timetableData = getData();
        subjects = timetableData.getSubjects();
        classrooms = timetableData.getClassrooms();
        periods = timetableData.getPeriods();
        showData(root);
        setCurrentDate(root);
        setupCountdown();
        return root;
    }

    private void setupCountdown() {
        runnable = new Runnable() {
            @Override
            public void run() {
                setCurrentDate(binding.getRoot());
                // 現在の日付を取得
                Calendar calendar = Calendar.getInstance();
                // 月は0から始まるので+1する
                String currentDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
                // 前回取得した日付と現在の日付を比較
                if (!currentDate.equals(lastFetchedDate)) {
                    // 日付が変わった場合はデータを再取得して表示
                    lastFetchedDate = currentDate;
                    //新しい曜日のデータを取得
                    TimetableData timetableData = getData();
                    subjects = timetableData.getSubjects();
                    classrooms = timetableData.getClassrooms();
                    periods = timetableData.getPeriods();
                    showData(binding.getRoot());
                }
                showData(binding.getRoot());
                // 1秒ごとに再実行
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000); // Initial delay 1 second (to align with the next second)
    }

    private void setCurrentDate(View root) {
        TextView dateTextView = root.findViewById(R.id.dateTextView);
        TextView timeTextView = root.findViewById(R.id.timeTextView);
        Calendar calendar = Calendar.getInstance();
        //日時を取得
        SimpleDateFormat dateFormat = new SimpleDateFormat("M月dd日 EEEE", Locale.getDefault());
        dateTextView.setText(dateFormat.format(calendar.getTime()));
        //時刻を取得
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        timeTextView.setText(timeFormat.format(calendar.getTime()));

    }

    private TimetableData getData() {
        //データベース
        TimetableDbHelper helper = new TimetableDbHelper(getContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        //現在の曜日
        String[] DaysOfWeek = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        //dayOfWeekではSUNDAY(1)、MONDAY(2)、TUESDAY(3)、WEDNESDAY(4)、THURSDAY(5)、FRIDAY(6)、SATURDAY(7)
        String currentDayOfWeek = DaysOfWeek[dayOfWeek - 2];
        String[] projection = {
                "id",
                "day_of_week",
                "period",
                "subject",
                "classroom"
        };

        // 現在曜日(currentDayOfWeek)のデータのみ取得
        Cursor cursor = db.query(
                "Timetable",
                projection,
                "day_of_week = ?", new String[]{currentDayOfWeek},
                null,
                null,
                "period ASC" //何限目かの値が小さい順にとりだし
        );
        List<String> subjectList = new ArrayList<>();
        List<String> classroomList = new ArrayList<>();
        List<String> periodsList = new ArrayList<>();

        while (cursor.moveToNext()) {
            //Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String subject = cursor.getString(cursor.getColumnIndexOrThrow("subject"));
            String classroom = cursor.getString(cursor.getColumnIndexOrThrow("classroom"));
            int period = cursor.getInt(cursor.getColumnIndexOrThrow("period"));

            subjectList.add(subject);
            classroomList.add(classroom);
            periodsList.add(Integer.toString(period));
        }
        //Listを配列に変換
        String[] subject = subjectList.toArray(new String[0]);
        String[] classroom = classroomList.toArray(new String[0]);
        String[] periods = periodsList.toArray(new String[0]);

        cursor.close();
        db.close();
        //取得したデータはTimetableDataクラスにて保管
        return new TimetableData(subject, classroom, periods);
    }
    private int getIntervalTime(String period){
        //授業時間のリスト作成
        List<Period> schedule = new ArrayList<>();
        schedule.add(new Period("8:40", "10:20"));
        schedule.add(new Period("10:35", "12:15"));
        schedule.add(new Period("13:15", "14:55"));
        schedule.add(new Period("15:10", "16:50"));
        schedule.add(new Period("17:05", "18:45"));
        // 現在時刻の取得
        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);
        int currentSecond = currentTime.get(Calendar.SECOND);
        // 授業の開始時刻の取得
        Period timePeriod = schedule.get(Integer.parseInt(period) - 1);
        String startTime = timePeriod.getStartTime();
        String[] startTokens = startTime.split(":");
        int startHour = Integer.parseInt(startTokens[0]);
        int startMinute = Integer.parseInt(startTokens[1]);
        // 現在時刻から授業の開始時刻までの時間差を計算
        int hourDifference = startHour - currentHour;
        int minuteDifference = startMinute - currentMinute;
        int secondDifference = - currentSecond;
        // 秒に変換
        return hourDifference * 3600 + minuteDifference * 60 + secondDifference;
    }

    private void showData(View root) {
        // データ取得(グローバルで定義したものを使用)
        //TimetableData timetableData = getData();
        //String[] subjects = timetableData.getSubjects();
        //String[] classrooms = timetableData.getClassrooms();
        //String[] periods = timetableData.getPeriods();

        // 表示用リスト作成
        List<String> times_display = new ArrayList<>();
        List<String> subject_display = new ArrayList<>();
        List<String> classroom_display = new ArrayList<>();
        List<String> periods_display = new ArrayList<>();
        for (int i = 0; i < subjects.length; i++) {
            int intervalTime = getIntervalTime(periods[i]);
            if (intervalTime >= 0) {
                times_display.add(formatTimeDifference(intervalTime));
                subject_display.add(subjects[i]);
                classroom_display.add(classrooms[i]);
                periods_display.add(periods[i] + "限");
            }
        }

        // 授業が1つも残ってない場合
        if (times_display.isEmpty()) {
            times_display.add("本日の授業はもうありません。");
            subject_display.add("お疲れ様です。");
            classroom_display.add("");
            periods_display.add("");
        }

        ListView listView = root.findViewById(R.id.listView); // root.findViewById() を使用する
        CustomAdapter adapter = new CustomAdapter(requireActivity(), times_display.toArray(new String[0]), subject_display.toArray(new String[0]), classroom_display.toArray(new String[0]), periods_display.toArray(new String[0])); // requireActivity() を使用する
        listView.setAdapter(adapter);
    }

    @SuppressLint("DefaultLocale")
    private String formatTimeDifference(int totalSeconds) {
        // 分と秒に変換
        int hour = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        // 時間差を文字列に整形
        if (hour == 0) {
            return String.format("あと%d分%02d秒", minutes, seconds);
        } else {
            return String.format("あと%d時間%02d分%02d秒", hour, minutes, seconds);
        }
    }

    //時間割の開始時刻と終了時刻を保存するためのクラス
    static class Period {
        private final String startTime;
        private final String endTime;
        public Period(String startTime, String endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }
        public String getStartTime() {
            return startTime;
        }
        public String getEndTime() {return endTime;}
    }

    public static class TimetableData {
        private final String[] subjects;
        private final String[] classrooms;
        private final String[] periods;

        public TimetableData(String[] subjects, String[] classrooms, String[] periods) {
            this.subjects = subjects;
            this.classrooms = classrooms;
            this.periods = periods;
        }
        public String[] getSubjects() {
            return subjects;
        }

        public String[] getClassrooms() {
            return classrooms;
        }

        public String[] getPeriods() {
            return periods;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
        binding = null;
    }
}