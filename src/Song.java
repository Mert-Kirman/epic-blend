// Class for song object implementation
public class Song {
    public int songID;
    public String songName;
    public Integer playlistID;  // The playlist ID that contains this song
    public Integer playCount;
    public Integer heartacheScore;  // A value in range 0 - 100
    public Integer roadTripScore;
    public Integer blissfulScore;

    Song() {}
    Song(int[] songInfo, String songName) {
        this.songID = songInfo[0];
        this.songName = songName;
        this.playCount = songInfo[1];
        this.heartacheScore = songInfo[2];
        this.roadTripScore = songInfo[3];
        this.blissfulScore = songInfo[4];
    }

    // Method that compares two song objects according to a specified criteria
    public int compare(Song song2, String sortBy, boolean maxHeap) {
        switch (sortBy) {
            case "heartache" -> {
                if (this.heartacheScore.compareTo(song2.heartacheScore) != 0) {
                    return this.heartacheScore - song2.heartacheScore;
                } else {  // Compare Lexicographically
                    if(maxHeap) {
                        return song2.songName.compareTo(this.songName);
                    }
                    else {
                        return this.songName.compareTo(song2.songName);
                    }
                }
            }
            case "roadTrip" -> {
                if (this.roadTripScore.compareTo(song2.roadTripScore) != 0) {
                    return this.roadTripScore - song2.roadTripScore;
                } else {  // Compare Lexicographically
                    if(maxHeap) {
                        return song2.songName.compareTo(this.songName);
                    }
                    else {
                        return this.songName.compareTo(song2.songName);
                    }
                }
            }
            case "blissful" -> {
                if (this.blissfulScore.compareTo(song2.blissfulScore) != 0) {
                    return this.blissfulScore - song2.blissfulScore;
                } else {  // Compare Lexicographically
                    if(maxHeap) {
                        return song2.songName.compareTo(this.songName);
                    }
                    else {
                        return this.songName.compareTo(song2.songName);
                    }
                }
            }
            case "playCount" -> {
                if (this.playCount.compareTo(song2.playCount) != 0) {
                    return this.playCount - song2.playCount;
                } else {  // Compare Lexicographically
                    if(maxHeap) {
                        return song2.songName.compareTo(this.songName);
                    }
                    else {
                        return this.songName.compareTo(song2.songName);
                    }
                }
            }
            case "name" -> {  // Compare Lexicographically
                if(maxHeap) {
                    return song2.songName.compareTo(this.songName);
                }
                else {
                    return this.songName.compareTo(song2.songName);
                }
            }
        }
        return 0;
    }
}
