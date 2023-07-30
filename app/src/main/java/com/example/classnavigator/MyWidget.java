package com.example.classnavigator;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyWidget extends AppWidgetProvider {
    private Handler handler = new Handler();
    private Runnable runnable;
    private static final int INTERVAL = 1000; // 更新間隔（ミリ秒）

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // このプロバイダーに属する各App Widgetに対してこのループ手順を実行します
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            // ウィジェットをクリックしたときに開きたいアクティビティや画面
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // App Widgetのレイアウトを取得し、ボタンにオンクリックリスナーを添付
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

            // 1秒ごとにshowData()メソッドを呼び出すための処理
            if (runnable != null) {
                handler.removeCallbacks(runnable);
            }
            runnable = new Runnable() {
                @Override
                public void run() {
                    showData(context, views);
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                    handler.postDelayed(this, INTERVAL);
                }
            };
            handler.postDelayed(runnable, INTERVAL);
        }
    }

    private void showData(Context context, RemoteViews views) {
        //授業時間のリスト作成
        List<Period> schedule = new ArrayList<>();
        schedule.add(new Period("8:40", "10:20"));
        schedule.add(new Period("10:35", "12:15"));
        schedule.add(new Period("13:15", "14:55"));
        schedule.add(new Period("15:10", "16:50"));
        schedule.add(new Period("17:05", "18:45"));
        String[] DaysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        //データベース
        TimetableDbHelper helper = new TimetableDbHelper(context.getApplicationContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        //現在曜日の取得
        Calendar calendar = Calendar.getInstance();
        Integer dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        //dayOfWeekではSUNDAY(1)、MONDAY(2)、TUESDAY(3)、WEDNESDAY(4)、THURSDAY(5)、FRIDAY(6)、SATURDAY(7)
        String currentDayOfWeek = DaysOfWeek[dayOfWeek-1];
        // 現在時刻の取得
        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);
        int currentSecond = currentTime.get(Calendar.SECOND);
        String[] projection = {
                "id",
                "day_of_week",
                "period",
                "subject",
                "classroom"
        };
        //現在曜日(currentDayOfWeek)のデータのみ取得
        Cursor cursor = db.query(
                "Timetable",
                projection,
                "day_of_week = ?", new String[]{currentDayOfWeek},
                null,
                null,
                "period ASC" //何限目かの値が小さい順にとりだし
        );
        List<String> timesList = new ArrayList<>();
        List<String> subjectList = new ArrayList<>();
        List<String> classroomList = new ArrayList<>();
        List<String> periodsList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String subject = cursor.getString(cursor.getColumnIndexOrThrow("subject"));
            String classroom = cursor.getString(cursor.getColumnIndexOrThrow("classroom"));
            Integer period = cursor.getInt(cursor.getColumnIndexOrThrow("period"));
            Period timePeriod = schedule.get(period-1);
            //何限目かをもとに開始時間と終了時間を取得しStringで画面表示文に直す
            String timePeriodString = (period) + "限(" + timePeriod.getStartTime() + "~" + timePeriod.getEndTime() + ")";
            // 授業の開始時刻の取得
            String startTime = timePeriod.getStartTime();
            String[] startTokens = startTime.split(":");
            int startHour = Integer.parseInt(startTokens[0]);
            int startMinute = Integer.parseInt(startTokens[1]);
            // 現在時刻から授業の開始時刻までの時間差を計算
            int hourDifference = startHour - currentHour;
            int minuteDifference = startMinute - currentMinute;
            int secondDifference = 0 - currentSecond;
            // 秒に変換
            int totalSeconds = hourDifference * 3600 + minuteDifference * 60 + secondDifference;
            // 分と秒に変換
            int hour = totalSeconds / 3600;
            int minutes = (totalSeconds % 3600) / 60;
            int seconds = totalSeconds % 60;
            // 時間差を文字列に整形
            String timeDifference;
            if(hour == 0){
                timeDifference = String.format("あと%d分%02d秒", minutes, seconds);
            }else{
                timeDifference = String.format("あと%d時間%02d分%02d秒", hour, minutes, seconds);
            }

            if(totalSeconds >=0 ){
                timesList.add(timeDifference);
                subjectList.add(subject);
                classroomList.add(classroom);
                periodsList.add(timePeriodString);
            }
        }
        //Listを配列に変換
        String[] times = timesList.toArray(new String[0]);
        String[] subject = subjectList.toArray(new String[0]);
        String[] classroom = classroomList.toArray(new String[0]);
        String[] periods = periodsList.toArray(new String[0]);
        //授業が1つも残ってない場合
        if (timesList.isEmpty()) {
            views.setTextViewText(R.id.textTime, "本日の授業はもうありません");
            views.setTextViewText(R.id.textSubject, "お疲れ様です");
            views.setTextViewText(R.id.textClassRoom, "");
            views.setTextViewText(R.id.textPeriod, "");
        }else{
            views.setTextViewText(R.id.textTime, times[0]);
            views.setTextViewText(R.id.textSubject, subject[0]);
            views.setTextViewText(R.id.textClassRoom, classroom[0]);
            views.setTextViewText(R.id.textPeriod, periods[0]);
        }
        cursor.close();
        db.close();
    }

    //時間割の開始時刻と終了時刻を保存するためのクラス
    class Period {
        private String startTime;
        private String endTime;
        public Period(String startTime, String endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }
        public String getStartTime() {
            return startTime;
        }
        public String getEndTime() {
            return endTime;
        }
    }
}