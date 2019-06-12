package com.example.rdmusicplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout parentRelativeLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String keeper = "";

    private ImageView pausePlayBtn, nextBtn, previousBtn;
    private TextView songNameTxt;

    private ImageView imageView;
    private RelativeLayout lowerRelativeLayout;
    private Button voiceEnabledBtn;
    private String mode = "ON";

    private MediaPlayer myMediaPlayer;
    private int position;
    private ArrayList<File> mySongs;
    private String mSongsName;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkVoiceCommandPermission();

        pausePlayBtn = findViewById(R.id.play_pause_btn);
        nextBtn = findViewById(R.id.next_btn);
        previousBtn = findViewById(R.id.previous_btn);
        songNameTxt = findViewById(R.id.songName);
        imageView = findViewById(R.id.logo);
        lowerRelativeLayout = findViewById(R.id.lower);
        voiceEnabledBtn = findViewById(R.id.voice_enabled_btn);


        parentRelativeLayout = findViewById(R.id.parentRelativeLayout);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault());

        validateReceiveValuesAndStartPlaying();
        imageView.setBackgroundResource(R.drawable.logo);


        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {

                ArrayList<String> mathesFound = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if(mathesFound != null){

                    if(mode.equals("ON")){

                        keeper = mathesFound.get(0);

                        switch (keeper) {
                            case "pause":

                                playPauseSong();
                                break;
                            case "play":

                                playPauseSong();
                                break;
                            case "play next song":

                                playNextSong();
                                break;
                            case "play previous song":

                                playPreviousSong();
                                break;
                        }
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });


        parentRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){

                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecognizerIntent);
                        keeper = "";
                        break;

                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;

                }

                return false;
            }
        });

        voiceEnabledBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode.equals("ON")){
                    mode = "OFF";
                    voiceEnabledBtn.setText("Voice Enabled Mode - OFF");
                    lowerRelativeLayout.setVisibility(View.VISIBLE);
                }
                else{
                    mode = "ON";
                    voiceEnabledBtn.setText("Voice Enabled Mode - ON");
                    lowerRelativeLayout.setVisibility(View.GONE);
                }
            }
        });


        pausePlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(myMediaPlayer.getCurrentPosition()>0) {
                    playPauseSong();
                }
            }
        });


        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(myMediaPlayer.getCurrentPosition()>0) {
                    playPreviousSong();
                }
            }
        });


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(myMediaPlayer.getCurrentPosition()>0) {
                    playNextSong();
                }
            }
        });
    }


    private void validateReceiveValuesAndStartPlaying(){

        if(myMediaPlayer != null){

            myMediaPlayer.stop();
            myMediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();


        mySongs = (ArrayList) bundle.getParcelableArrayList("song");
        mSongsName = mySongs.get(position).getName();
        String songName = intent.getStringExtra("name");

        songNameTxt.setText(songName);
        songNameTxt.setSelected(true);

        position = bundle.getInt("position", 0);
        Uri uri = Uri.parse(mySongs.get(position).toString());

        myMediaPlayer = MediaPlayer.create(MainActivity.this,uri);
        myMediaPlayer.start();
    }


    private void checkVoiceCommandPermission(){
        if(!(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)){

            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            finish();
        }
    }


    private void playPauseSong(){

        imageView.setBackgroundResource(R.drawable.four);

        if(myMediaPlayer.isPlaying()){

            pausePlayBtn.setImageResource(R.drawable.play);
            myMediaPlayer.pause();
        }
        else{
            pausePlayBtn.setImageResource(R.drawable.pause);
            myMediaPlayer.start();
            imageView.setBackgroundResource(R.drawable.five);
        }
    }


    private void playNextSong(){

        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();

        position = ((position+1)%mySongs.size());

        Uri uri = Uri.parse(mySongs.get(position).toString());

        myMediaPlayer = MediaPlayer.create(MainActivity.this, uri);

        mSongsName = mySongs.get(position).toString();
        songNameTxt.setText(mSongsName);
        myMediaPlayer.start();

        imageView.setBackgroundResource(R.drawable.musicbrain);

        if(myMediaPlayer.isPlaying()){

            pausePlayBtn.setImageResource(R.drawable.pause);
        }
        else{
            pausePlayBtn.setImageResource(R.drawable.play);

            imageView.setBackgroundResource(R.drawable.five);
        }

    }


    private void playPreviousSong(){

        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();

        position = ((position-1)<0 ? (mySongs.size()-1) : (position-1));

        Uri uri = Uri.parse(mySongs.get(position).toString());

        myMediaPlayer = MediaPlayer.create(MainActivity.this, uri);

        mSongsName = mySongs.get(position).toString();
        songNameTxt.setText(mSongsName);
        myMediaPlayer.start();

        imageView.setBackgroundResource(R.drawable.two);

        if(myMediaPlayer.isPlaying()){

            pausePlayBtn.setImageResource(R.drawable.pause);
        }
        else{
            pausePlayBtn.setImageResource(R.drawable.play);

            imageView.setBackgroundResource(R.drawable.five);
        }

    }

}
