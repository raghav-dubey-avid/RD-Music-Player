package com.example.rdmusicplayer;

import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity {

    private String[] itemsAll;
    private ListView mSongsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        mSongsList = findViewById(R.id.songsList);

        appExternalStoragePermission();
    }


    public void appExternalStoragePermission(){

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {

                        displayAudioSongsName();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {

                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        token.continuePermissionRequest();
                    }

                }).check();
    }


    public ArrayList<File> readOnlyAudioSongs(File file){

        ArrayList<File> arrayList = new ArrayList<>();

        File[] allFiles = file.listFiles();

        for(File individualFiles : allFiles){

            if(individualFiles.isDirectory() && !individualFiles.isHidden()){

                arrayList.addAll(readOnlyAudioSongs(individualFiles));
            }
            else{

                if(individualFiles.getName().endsWith(".mp3") || individualFiles.getName().endsWith(".wav") || individualFiles.getName().endsWith(".wma")){

                    arrayList.add(individualFiles);
                }
            }
        }

        return arrayList;
    }


    private void displayAudioSongsName(){

        final ArrayList<File> audioSongs = readOnlyAudioSongs(Environment.getExternalStorageDirectory());

        itemsAll = new String[audioSongs.size()];

        for(int songCounter=0; songCounter<audioSongs.size(); songCounter++){

            itemsAll[songCounter] = audioSongs.get(songCounter).getName();
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MusicActivity.this, android.R.layout.simple_list_item_1, itemsAll);
        mSongsList.setAdapter(arrayAdapter);

        mSongsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String songName = mSongsList.getItemAtPosition(i).toString();

                Intent intent = new Intent(MusicActivity.this, MainActivity.class);
                intent.putExtra("song", audioSongs);
                intent.putExtra("name", songName);
                intent.putExtra("position", i);
                startActivity(intent);
            }
        });
    }

}
