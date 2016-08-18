package com.example.WindCloudMusicPlayer.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.WindCloudMusicPlayer.R;
import com.example.WindCloudMusicPlayer.manager.SongsManager;
import com.example.WindCloudMusicPlayer.utils.Utils;

public class AndroidBuildingMusicPlayerActivity extends Activity implements
		OnCompletionListener, SeekBar.OnSeekBarChangeListener {

	private Utils utils;
	private ImageButton btnPlay;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private ImageButton btnPlaylist;
	private ImageButton btnShuffle;
	private SeekBar songProgressBar;
	private TextView songTitleLabel;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	private MediaPlayer mp;
	private SongsManager sm;
	private int currentSongIndex = 0;
	private boolean isShuffle = false;
	private Handler mHandler = new Handler();

	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.player);
		initView();
		setData();
		setListener();
	}

	private void setListener() {
		songProgressBar.setOnSeekBarChangeListener(this);
		mp.setOnCompletionListener(this);
		btnPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mp.isPlaying()) {
					if (mp != null) {
						mp.pause();
						btnPlay.setImageResource(R.drawable.play);
					}
				} else {
					if (mp != null) {
						mp.start();
						btnPlay.setImageResource(R.drawable.pause);
					}
				}
			}
		});
		btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (songsList != null) {
				if (currentSongIndex < (songsList.size() - 1)) {
					playSong(currentSongIndex + 1);
					currentSongIndex = currentSongIndex + 1;
				} else {
					playSong(0);
					currentSongIndex = 0;
				}
			}
				}
		});
		btnPrevious.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (songsList != null) {
					if (currentSongIndex > 0) {
						playSong(currentSongIndex - 1);
						currentSongIndex = currentSongIndex - 1;
					} else {
						playSong(songsList.size() - 1);
						currentSongIndex = songsList.size() - 1;
					}
				}
			}
		});

		btnShuffle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (isShuffle) {
					btnShuffle.setImageResource(R.drawable.shuffle);
					Toast.makeText(getApplicationContext(), "随机播放",
							Toast.LENGTH_SHORT).show();
					isShuffle = false;
				} else {
					btnShuffle.setImageResource(R.drawable.repeat);
					Toast.makeText(getApplicationContext(), "顺序播放",
							Toast.LENGTH_SHORT).show();
					isShuffle = true;
				}

			}

		});
		btnPlaylist.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(),
						PlayListActivity.class);
				startActivityForResult(i, 100);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 100) {
			currentSongIndex = data.getExtras().getInt("songIndex");
			playSong(currentSongIndex);
		}
	}

	private void setData() {
		mp = new MediaPlayer();
		sm = new SongsManager();
		utils = new Utils();
		songsList = sm.getPlayList();
		// playSong(0);
	}

	public void playSong(int songIndex) {
		if (songsList != null) {
			try {
				mp.reset();
				mp.setDataSource(songsList.get(songIndex).get("songPath"));
				mp.prepare();
				mp.start();
				String songTitle = songsList.get(songIndex).get("songTitle");
				songTitleLabel.setText(songTitle);
				btnPlay.setImageResource(R.drawable.pause);
				songProgressBar.setProgress(0);
				songProgressBar.setMax(100);
				updateProgressBar();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void updateProgressBar() {
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}

	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			long totalDuration = mp.getDuration();
			long currentDuration = mp.getCurrentPosition();
			songTotalDurationLabel.setText(""
					+ utils.milliSecondsToTimer(totalDuration));
			songCurrentDurationLabel.setText(""
					+ utils.milliSecondsToTimer(currentDuration));
			int progress = (int) (utils.getProgressPercentage(currentDuration,
					totalDuration));
			// Log.d("Progress", "" + progress);
			songProgressBar.setProgress(progress);
			mHandler.postDelayed(this, 100);
		}
	};

	private void initView() {
		btnPlay = (ImageButton) findViewById(R.id.btn_play);
		btnNext = (ImageButton) findViewById(R.id.btn_next);
		btnPrevious = (ImageButton) findViewById(R.id.btn_pre);
		btnPlaylist = (ImageButton) findViewById(R.id.btn_playlist);
		btnShuffle = (ImageButton) findViewById(R.id.btn_shuffle);
		songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
		songTitleLabel = (TextView) findViewById(R.id.songTitle);
		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);

	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		mHandler.removeCallbacks(mUpdateTimeTask);

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = mp.getDuration();
		int currentPosition = utils.progressToTimer(seekBar.getProgress(),
				totalDuration);
		mp.seekTo(currentPosition);
		updateProgressBar();
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		if (songsList != null) {
			if (isShuffle) {
				Random rand = new Random();
				currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
				playSong(currentSongIndex);
			} else {
				if (currentSongIndex < (songsList.size() - 1)) {
					playSong(currentSongIndex + 1);
					currentSongIndex = currentSongIndex + 1;
				} else {
					playSong(0);
					currentSongIndex = 0;
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mp.release();
	}
}
