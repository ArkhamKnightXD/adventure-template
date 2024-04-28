package knight.arkham.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AssetsHelper {

    public static Sound loadSound(String filename){
        return Gdx.audio.newSound(Gdx.files.internal("sounds/"+ filename));
    }

    public static Music loadMusic(String filename){
        return Gdx.audio.newMusic(Gdx.files.internal("music/"+ filename));
    }
}
