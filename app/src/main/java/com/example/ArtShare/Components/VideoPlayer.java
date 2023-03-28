package com.example.ArtShare.Components;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.ArtShare.R;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class VideoPlayer extends ConstraintLayout {
    private final Context context;

    private ImageView videoContentPreview;
    private VideoView videoContent;
    private TextView durationText;
    private LinearLayout playButtonBg;
    private Button playButton;
    private LinearLayout muteButtonBg;
    private Button muteButton;

    private boolean isMuted = false;
    private boolean isPathSet = false;

    private String videoPath;

    public VideoPlayer(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context);
        this.context = context;
    }

    private void initControl(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.video_player, this);

        videoContentPreview = findViewById(R.id.video_content_preview);
        videoContent = findViewById(R.id.video_content);
        durationText = findViewById(R.id.video_content_duration);
        playButtonBg = findViewById(R.id.video_content_play_button_bg);
        playButton = findViewById(R.id.video_content_play_button);
        muteButtonBg = findViewById(R.id.video_content_mute_button_bg);
        muteButton = findViewById(R.id.video_content_mute_button);

        videoContent.setOnCompletionListener(mediaPlayer -> {
            playButton.setBackgroundResource(R.drawable.ic_play_white);
            videoContent.seekTo(0);
            durationText.setText(getDuration(videoContent.getCurrentPosition(), videoContent.getDuration()));
        });

        videoContent.setOnPreparedListener(mediaPlayer -> {
            startUpdatingDurationTimer();

            int height = mediaPlayer.getVideoHeight();
            ViewGroup.LayoutParams layoutParams = videoContent.getLayoutParams();
            layoutParams.height = height;
            videoContent.setLayoutParams(layoutParams);

            muteButton.setOnClickListener(view -> {
                if (isMuted) {
                    mediaPlayer.setVolume(1, 1);
                    muteButton.setBackgroundResource(R.drawable.ic_volume_up);
                    isMuted = false;
                } else {
                    mediaPlayer.setVolume(0, 0);
                    muteButton.setBackgroundResource(R.drawable.ic_volume_off);
                    isMuted = true;
                }
            });
        });

        videoContent.setOnClickListener(view -> {
            playButtonBg.setVisibility(VISIBLE);
            playButton.setVisibility(VISIBLE);
            muteButtonBg.setVisibility(VISIBLE);
            muteButton.setVisibility(VISIBLE);

            hideButtons();
        });

        playButton.setOnClickListener(view -> {
            if (videoContent.isPlaying()) {
                pause();
            } else {
                start();
            }
        });
    }

    private void startUpdatingDurationTimer() {
        int duration = videoContent.getDuration();
        int updateTimes = duration / 100;

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ((Activity)context).runOnUiThread(() -> {
                    if (videoContent.isPlaying()) {
                        durationText.setText(getDuration(videoContent.getCurrentPosition(), videoContent.getDuration()));
                    }
                });
            }
        }, 0, updateTimes);
    }

    public void start() {
        if (videoContent != null) {
            if (videoPath != null && !isPathSet) {
                videoContent.setVideoPath(videoPath);
                isPathSet = true;
            }

            videoContent.setVisibility(VISIBLE);
            durationText.setVisibility(VISIBLE);
            videoContentPreview.setVisibility(GONE);
            videoContent.start();
            playButton.setBackgroundResource(R.drawable.ic_pause_white);
            hideButtons();
        }
    }

    public void pause() {
        if (videoContent != null) {
            videoContent.pause();
            playButton.setBackgroundResource(R.drawable.ic_play_white);
        }
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;

        if (this.videoPath != null) {
            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(this.videoPath.replace("file:///", ""), MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
            if (thumbnail != null) {
                videoContentPreview.setImageBitmap(thumbnail);
            }
        }
    }

    public String getVideoPath() {
        return videoPath;
    }

    private String getDuration(int currentMilliseconds, int totalMilliseconds) {
        String totalMinutes = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(totalMilliseconds));
        if (!(totalMinutes.length() > 1)) {
            totalMinutes = "0" + totalMinutes;
        }
        String totalSeconds = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(totalMilliseconds) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalMilliseconds)));
        if (!(totalSeconds.length() > 1)) {
            totalSeconds = "0" + totalSeconds;
        }

        String minutesLeft = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(currentMilliseconds));
        if (!(minutesLeft.length() > 1)) {
            minutesLeft = "0" + minutesLeft;
        }
        String secondsLeft = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(currentMilliseconds) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentMilliseconds)));
        if (!(secondsLeft.length() > 1)) {
            secondsLeft = "0" + secondsLeft;
        }
        return String.format("%s:%s / %s:%s", minutesLeft, secondsLeft, totalMinutes, totalSeconds);
    }

    private void hideButtons() {
        postDelayed(() -> {
            if (videoContent.isPlaying()) {
                playButtonBg.setVisibility(INVISIBLE);
                playButton.setVisibility(INVISIBLE);
                muteButtonBg.setVisibility(INVISIBLE);
                muteButton.setVisibility(INVISIBLE);
            }
        }, 3000);
    }
}
