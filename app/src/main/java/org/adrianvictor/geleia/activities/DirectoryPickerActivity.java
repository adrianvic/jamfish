package org.adrianvictor.geleia.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class DirectoryPickerActivity extends AppCompatActivity {

    public static final String EXTRA_RESULT_PATH = "result_path";

    private File rootDir;
    private File currentDir;

    private ArrayAdapter<File> adapter;
    private TextView pathView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootDir = Environment.getExternalStorageDirectory();
        currentDir = rootDir;

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        pathView = new TextView(this);
        pathView.setPadding(24, 24, 24, 24);

        ListView listView = new ListView(this);

        Button selectButton = new Button(this);
        selectButton.setText("Select this folder");

        layout.addView(pathView);
        layout.addView(listView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        layout.addView(selectButton);

        setContentView(layout);

        adapter = new ArrayAdapter<File>(this,
                android.R.layout.simple_list_item_1, new ArrayList<>()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                File f = getItem(position);

                File parentFile = currentDir.getParentFile();

                if (parentFile != null && parentFile.equals(f)) {
                    tv.setText("..");
                } else {
                    tv.setText(f.getName());
                }

                return tv;
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((p, v, pos, id) -> {
            File dir = adapter.getItem(pos);
            if (dir != null) openDir(dir);
        });

        selectButton.setOnClickListener(v -> {
            Intent result = new Intent();
            result.putExtra(EXTRA_RESULT_PATH, currentDir.getAbsolutePath());
            setResult(RESULT_OK, result);
            finish();
        });

        openDir(rootDir);
    }

    private void openDir(File dir) {
        if (!dir.exists() || !dir.canRead()) return;

        File[] dirs = dir.listFiles(f ->
                f.isDirectory() && f.canRead()
        );

        if (dirs == null) return;

        adapter.clear();

        File parent = dir.getParentFile();
        if (parent != null && parent.getAbsolutePath().startsWith(rootDir.getAbsolutePath())) {
            adapter.add(parent);
        }

        Arrays.sort(dirs);

        for (File f : dirs) {
            adapter.add(f);
        }

        currentDir = dir;
        pathView.setText(dir.getAbsolutePath());
    }

    @Override
    public void onBackPressed() {
        File parent = currentDir.getParentFile();
        if (parent != null &&
                parent.getAbsolutePath().startsWith(rootDir.getAbsolutePath())) {
            openDir(parent);
        } else {
            super.onBackPressed();
        }
    }
}
