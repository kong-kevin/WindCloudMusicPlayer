package com.example.WindCloudMusicPlayer.manager;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import android.os.Environment;

public class SongsManager {
	// sd卡路径
	final String MEDIA_PATH = Environment.getExternalStorageDirectory().getPath();
			
	
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

	public ArrayList<HashMap<String, String>> getPlayList() {
		System.out.println(MEDIA_PATH);
		File home = new File(MEDIA_PATH);
		if (home.listFiles(new FileExtensionFilter()).length > 0) {
			for (File file : home.listFiles(new FileExtensionFilter())) {
				HashMap<String, String> song = new HashMap<String, String>();
				song.put(
						"songTitle",
						file.getName().substring(0,
								(file.getName().length() - 4)));
				song.put("songPath", file.getPath());
				// 添加歌曲到歌曲集合
				songsList.add(song);
			}
		}
		// 返回歌曲集合
		
			return songsList;
		
	}
}

class FileExtensionFilter implements FilenameFilter {
	public boolean accept(File dir, String name) {
		return (name.endsWith(".mp3") || name.endsWith(".MP3"));
	}

}
