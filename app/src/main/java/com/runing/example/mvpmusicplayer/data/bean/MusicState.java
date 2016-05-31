package com.runing.example.mvpmusicplayer.data.bean;

import com.runing.example.mvpmusicplayer.service.MusicService;

/**
 * Created by runing on 2016/5/16.
 * <p/>
 * This file is part of MvpMusicPlayer.
 * MvpMusicPlayer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * MvpMusicPlayer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with MvpMusicPlayer.  If not, see <http://www.gnu.org/licenses/>.
 */
public class MusicState {

    private Music music;

    private int progress;

    private int total;

    private int position;

    private MusicService.PlayState state = MusicService.PlayState.PAUSE;

    private MusicService.PlayMode mode = MusicService.PlayMode.LOOP;

    public MusicService.PlayMode getMode() {
        return mode;
    }

    public void setMode(MusicService.PlayMode mode) {
        this.mode = mode;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public MusicService.PlayState getState() {
        return state;
    }

    public void setState(MusicService.PlayState state) {
        this.state = state;
    }
}
