package com.example.classnavigator;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.classnavigator.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TimetableDbHelper helper = new TimetableDbHelper(this);//データベースの作成（TimetableDbHelper.java呼び出し）
        SQLiteDatabase db = helper.getWritableDatabase();
        db.close(); // データベースを閉じる


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Update the widget when the MainActivity is created
        updateWidget();
    }

    private void updateWidget() {
        // Get the AppWidgetManager and the ComponentName for the widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName componentName = new ComponentName(this, MyWidget.class);

        // Update the widget using the onUpdate() method of the AppWidgetProvider
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
        MyWidget widgetProvider = new MyWidget();
        widgetProvider.onUpdate(this, appWidgetManager, appWidgetIds);
    }
}