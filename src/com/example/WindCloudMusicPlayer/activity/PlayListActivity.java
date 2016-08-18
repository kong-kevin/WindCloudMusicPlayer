package com.example.WindCloudMusicPlayer.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.WindCloudMusicPlayer.R;
import com.example.WindCloudMusicPlayer.manager.SongsManager;

public class PlayListActivity extends ListActivity {
	// 歌曲集合
	public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist);

		ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();
		SongsManager sm = new SongsManager();
		// 获取sd卡中所有歌曲
		this.songsList = sm.getPlayList();
		// 遍历歌曲集合
		if (songsList != null) {
			for (int i = 0; i < songsList.size(); i++) {
				HashMap<String, String> song = songsList.get(i);
				songsListData.add(song);
			}
			ListView lv = getListView();
			ListAdapter adapter = new SimpleAdapter(this, songsListData,
					R.layout.playlist_item, new String[] { "songTitle" },
					new int[] { R.id.songTitle });
			lv.setAdapter(adapter);

			lv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					int songIndex = position;
					Intent in = new Intent(getApplicationContext(),
							AndroidBuildingMusicPlayerActivity.class);
					in.putExtra("songIndex", songIndex);
					setResult(100, in);
					finish();
				}
			});
		}
	}
}
