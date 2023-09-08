package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User newUser = new User(name , mobile);
        users.add(newUser);
        return newUser;
    }

    public Artist createArtist(String name) {
        Artist newArtist = new Artist(name);
        artists.add(newArtist);
        artistAlbumMap.put(newArtist , new ArrayList<Album>());
        return newArtist;
    }

    public Album createAlbum(String title, String artistName) {
        boolean isArtistPresent = false;
        Artist currArtist = new Artist();
        for(Artist a : artists){
            if(a.getName().equals(artistName)){
                isArtistPresent = true;
                currArtist = a;
            }
        }

        if(!isArtistPresent) {
            currArtist = createArtist(artistName);
        }

        Album newAlbum = new Album(title);
        albums.add(newAlbum);
        albumSongMap.put(newAlbum , new ArrayList<Song>());

        List<Album> albumList = artistAlbumMap.get(currArtist);
        albumList.add(newAlbum);
        artistAlbumMap.put(currArtist , albumList);
        albumSongMap.put(newAlbum , new ArrayList<>());
        return newAlbum;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        boolean isAlbumCreated = isAlbumPresent(albumName);
        if(!isAlbumCreated){
            throw new Exception("Album does not exist");
        }

        Song newSong = new Song(title , length);
        songs.add(newSong);
        for(Album album : albumSongMap.keySet()){
            if(album.getTitle().equals(albumName)){
                List<Song> songs = albumSongMap.get(album);
                songs.add(newSong);
                albumSongMap.put(album , songs);
            }
        }
        return newSong;
    }

    private boolean isAlbumPresent(String albumName) {
        for(Album A : albums){
            if(A.getTitle().equals(albumName)){
                return true;
            }
        }
        return false;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {

        User currUser = null;
        for(User user : users){
            if(user.getMobile().equals(mobile)){
                currUser = user;
                break;
            }
        }

        if(currUser == null){
            throw new Exception("User does not exist");
        }

        Playlist newPlayList = new Playlist(title);
        playlists.add(newPlayList);

        List<Song> songs = new ArrayList<>();
        for(Song song : songs){
            if(song.getLength() == length){
                songs.add(song);
            }
        }
        List<User> users = new ArrayList<>();
        users.add(currUser);
        creatorPlaylistMap.put(currUser , newPlayList);
        playlistListenerMap.put(newPlayList , users);
        playlistSongMap.put(newPlayList , songs);

        if(userPlaylistMap.containsKey(currUser)){
            List<Playlist> playlists1 = userPlaylistMap.get(currUser);
            playlists1.add(newPlayList);
            userPlaylistMap.put(currUser , playlists1);
        }else{
            List<Playlist> playlists1 = new ArrayList<>();
            playlists1.add(newPlayList);
            userPlaylistMap.put(currUser , playlists1);
        }
        return newPlayList;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {

        User currUser = null;
        for(User user : users){
            if(user.getMobile().equals(mobile)){
                currUser = user;
                break;
            }
        }

        if(currUser == null){
            throw new Exception("User does not exist");
        }

        Playlist newPlayList = new Playlist(title);
        playlists.add(newPlayList);

        List<Song> songs = new ArrayList<>();
        for(String songTitle : songTitles){
            for(Song song : songs){
                if(song.getTitle().equals(songTitle)){
                    songs.add(song);
                }
            }
        }

        List<User> users = new ArrayList<>();
        users.add(currUser);
        creatorPlaylistMap.put(currUser , newPlayList);
        playlistListenerMap.put(newPlayList , users);
        playlistSongMap.put(newPlayList , songs);

        if(userPlaylistMap.containsKey(currUser)){
            List<Playlist> playlists1 = userPlaylistMap.get(currUser);
            playlists1.add(newPlayList);
            userPlaylistMap.put(currUser , playlists1);
        }else{
            List<Playlist> playlists1 = new ArrayList<>();
            playlists1.add(newPlayList);
            userPlaylistMap.put(currUser , playlists1);
        }
        return newPlayList;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User currUser = null;
        for(User user : users){
            if(user.getMobile().equals(mobile)){
                currUser =  user;
                break;
            }
        }

        if(currUser == null){
            throw new Exception("User does not exist");
        }

        Playlist currPlayList = null;
        for(Playlist playlist : playlists){
            if(playlist.getTitle().equals(playlistTitle)){
                currPlayList = playlist;
                break;
            }
        }

        if(currPlayList == null){
            throw new Exception("Playlist does not exist");
        }

        List<User> currListeners = playlistListenerMap.get(currPlayList);
        if(!currListeners.contains(currUser)){
            currListeners.add(currUser);
        }

        playlistListenerMap.put(currPlayList , currListeners);
        return currPlayList;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User currUser = null;
        for(User user : users){
            if(user.getMobile().equals(mobile)){
                currUser =  user;
                break;
            }
        }

        if(currUser == null){
            throw new Exception("User does not exist");
        }

        Song currSong = null;
        for(Song song : songs){
            if(song.getTitle().equals(songTitle)){
                currSong = song;
                break;
            }
        }

        if(currSong == null){
            throw new Exception("Song does not exist");
        }

        List<User> likedUsers = new ArrayList<>();
        if(songLikeMap.containsKey(currSong)){
            likedUsers = songLikeMap.get(currSong);
        }

        if(!likedUsers.contains(currUser)){
            likedUsers.add(currUser);
        }
        songLikeMap.put(currSong , likedUsers);
        Album currAlbum = null;
        for(Album album : albumSongMap.keySet()){
            List<Song> songs = albumSongMap.get(album);
            if(songs.contains(currSong)){
                currAlbum = album;
                break;
            }
        }

        Artist currArtist = null;
        for(Artist artist : artistAlbumMap.keySet()){
            List<Album> albums = artistAlbumMap.get(artist);
            if(albums.contains(currAlbum)){
                currArtist = artist;
                break;
            }
        }
        currArtist.setLikes(currArtist.getLikes() + 1);
        return currSong;
    }

    public String mostPopularArtist() {

        int max = 0;
        Artist popularArtist = null;
        for(Artist artist : artists){
            if(artist.getLikes() > max){
                max = artist.getLikes();
                popularArtist = artist;
            }
        }

        if(popularArtist == null) return null;
        return popularArtist.getName();
    }

    public String mostPopularSong() {

        int max = 0;
        Song popularSong = null;

        for(Song song : songLikeMap.keySet()){
            int likes = songLikeMap.get(song).size();
            if(likes > max){
                max = likes;
                popularSong = song;
            }
        }

        if(popularSong == null) return null;
        return popularSong.getTitle();
    }
}
