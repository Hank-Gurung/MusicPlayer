package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {
    protected void onDestroy() { //to stop the playing of music after back button
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    TextView textView,textView2,textView3;
    ImageView pause, previous, next;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    int position;
    SeekBar seekBar;
    Thread updateSeek;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);

        pause = findViewById(R.id.pause);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentSong");
        textView.setText(textContent);
        textView.setSelected(true);
        position = intent.getIntExtra("position",0);

        playMusic(position);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //to display progression in min:sec format
                int sec = progress/1000;
                if(sec < 60){
                    if(sec <10){
                        textView2.setText(("0:0"+sec));
                    }else{
                        textView2.setText(("0:"+sec));
                    }
                } else{
                    int min = sec/60;
                    int remainingSec = sec%60;
                    if(remainingSec <10){
                        textView2.setText((min+":0"+remainingSec));
                    }else{
                        textView2.setText((min+":"+remainingSec));
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        updateSeek = new Thread(){
            public void run(){
                int currentPosition = 0;
                try{
                    while (currentPosition<mediaPlayer.getDuration()){
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(800);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();

//        play, pause, next, previous buttons
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    pause.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }else{
                    pause.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position!=0){
                    position = position -1;
                }else{
                    position = songs.size()-1;
                }

                playMusic(position);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position!=songs.size()-1){
                    position = position + 1;
                }else{
                    position = 0;
                }
                playMusic(position);
            }
        });
    }
    public void playMusic(int position){
        //        playing music
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();
        pause.setImageResource(R.drawable.pause);
        seekBar.setProgress(0);
        seekBar.setMax(mediaPlayer.getDuration());

//        showing time duration
        int dur = Integer.parseInt(String.valueOf(mediaPlayer.getDuration()));
        int sec = dur / 1000;
        textView3.setText(time(sec));

//        showing song name
        textContent = songs.get(position).getName().toString();
        textView.setText(textContent);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {//to know if the song ended
            @Override
            public void onCompletion(MediaPlayer mp) {
                // The song has finished playing
                mediaPlayer.stop();
                mediaPlayer.release();
                pause.setImageResource(R.drawable.play);
//
                nextSong(position);

            }
        });

    }

    public void nextSong(int position){//play next song automatically
        if(position!=songs.size()-1){
                    position = position + 1;
                }else{
                    position = 0;
                }
        playMusic(position);
    }

    public String time(int allSec){//display song duration in min:sec format
        String timer = "";
        if(allSec > 60){
             int allMin = allSec / 60;
            int remainingSec = allSec % 60;
            if(remainingSec < 10){
                 timer =  (allMin+":"+"0"+remainingSec);
            }else{
                timer = (allMin+":"+remainingSec);
            }
        }
        return timer;


    }


}