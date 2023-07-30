package com.example.classnavigator.ui.notifications;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.classnavigator.R;

import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<String> {

    private List<Integer> idList;
    private NotificationsFragment fragment;

    public CustomArrayAdapter(@NonNull Context context, int resource, @NonNull List<String> data, List<Integer> idList, NotificationsFragment fragment) {
        super(context, resource, data);
        this.idList = idList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_delete, parent, false);
        }

        // レイアウトからTextViewとButtonのビューを取得します
        TextView textViewItem = convertView.findViewById(R.id.textViewItem);
        Button deleteButton = convertView.findViewById(R.id.deleteButton);

        // TextViewにテキストを設定します
        textViewItem.setText(getItem(position));

        // 削除ボタンのクリックリスナーを設定します
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = idList.get(position); // idListから対応するIDを取得します
                fragment.deleteData(selectedId); // フラグメント内のdeleteData()メソッドを呼び出します
                fragment.showTimetableData(); // 削除後にListViewをリフレッシュします
            }
        });

        return convertView;
    }
}

