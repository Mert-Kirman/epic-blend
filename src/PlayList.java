import java.util.ArrayList;

// Playlist object that holds song objects
public class PlayList {
    public ArrayList<Song> songsArray;
    public int heartacheOfferedCount;  // Amount of Heartache songs offered to the EpicBlend from this playlist
    public int roadTripOfferedCount;  // Amount of Road Trip songs offered to the EpicBlend from this playlist
    public int blissfulOfferedCount;  // Amount of Blissful songs offered to the EpicBlend from this playlist

    PlayList() {
        this(0, 0, 0);
    }
    PlayList(int heartacheOfferedCount, int roadTripOfferedCount, int blissfulOfferedCount) {
        this.heartacheOfferedCount = heartacheOfferedCount;
        this.roadTripOfferedCount = roadTripOfferedCount;
        this.blissfulOfferedCount = blissfulOfferedCount;
        this.songsArray = new ArrayList<>();
    }
}
