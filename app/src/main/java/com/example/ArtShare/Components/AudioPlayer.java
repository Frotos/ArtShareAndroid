package com.example.ArtShare.Components;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.ArtShare.R;
import com.masoudss.lib.WaveformSeekBar;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class AudioPlayer extends ConstraintLayout {
    Context context;

    private Button playButton;
    private WaveformSeekBar waveform;
    private TextView durationText;
    private MediaPlayer mediaPlayer;

    private String audioPath;

    public AudioPlayer(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context);
    }

    private void initControl(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.audio_player, this);

        playButton = findViewById(R.id.audio_content_play_button);
        waveform = findViewById(R.id.audio_content_waveform);
        durationText = findViewById(R.id.audio_content_duration);

        waveform.setOnProgressChanged((waveformSeekBar, v, b) -> {
            if (b) {
                float progress = v / 100;
                int milliseconds = (int)(mediaPlayer.getDuration() * progress);
                mediaPlayer.seekTo(milliseconds);
            }
        });

        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            waveform.setProgress(0);
            playButton.setBackgroundResource(R.drawable.ic_play_circle);
            durationText.setText(getDuration(mediaPlayer.getDuration()));
        });

        mediaPlayer.setOnPreparedListener(mediaPlayer -> {
            int duration = mediaPlayer.getDuration();
            int updateTimes = duration / 400;

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    ((Activity)context).runOnUiThread(() -> {
                        if (mediaPlayer.isPlaying()) {
                            if (!(updateTimes * waveform.getProgress() >= duration)) {
                                float progress = waveform.getProgress();
                                progress += 0.25;
                                waveform.setProgress(progress);
                                durationText.setText(getDuration(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition()));
                            }
                        }
                    });
                }
            }, 0, updateTimes);
        });

        playButton.setOnClickListener(view -> {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    pause();
                } else {
                    start();
                }
            }
        });
    }

    public void start() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            playButton.setBackgroundResource(R.drawable.ic_pause_circle);
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            playButton.setBackgroundResource(R.drawable.ic_play_circle);
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            playButton.setBackgroundResource(R.drawable.ic_play_circle);
        }
    }

    public void setAudioPath(String audioPath) {
        if (audioPath == null) {
            return;
        }

        this.audioPath = audioPath;

        if (mediaPlayer != null) {
            try {
                mediaPlayer.setDataSource(audioPath);
                mediaPlayer.prepare();
                durationText.setText(getDuration(mediaPlayer.getDuration()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        waveform.setSampleFrom(this.audioPath);
    }

    public String getAudioPath() {
        return audioPath;
    }

    private String getDuration(int milliseconds) {
        String minutes = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(milliseconds));
        if (!(minutes.length() > 1)) {
            minutes = "0" + minutes;
        }
        String seconds = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
        if (!(seconds.length() > 1)) {
            seconds = "0" + seconds;
        }
        return String.format("%s:%s", minutes, seconds);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mediaPlayer == null) {
            context = getContext();
            mediaPlayer = new MediaPlayer();

            mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                waveform.setProgress(0);
                playButton.setBackgroundResource(R.drawable.ic_play_circle);
                durationText.setText(getDuration(mediaPlayer.getDuration()));
            });

            mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                int duration = mediaPlayer.getDuration();
                int updateTimes = duration / 400;

                Timer mTimer = new Timer();
                mTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        ((Activity)context).runOnUiThread(() -> {
                            if (mediaPlayer.isPlaying()) {
                                if (!(updateTimes * waveform.getProgress() >= duration)) {
                                    float progress = waveform.getProgress();
                                    progress += 0.25;
                                    waveform.setProgress(progress);
                                    durationText.setText(getDuration(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition()));
                                }
                            }
                        });
                    }
                }, 0, updateTimes);
            });

            setAudioPath(audioPath);
            playButton.setBackgroundResource(R.drawable.ic_play_circle);
            waveform.setProgress(0);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer = null;
        }
    }
}
