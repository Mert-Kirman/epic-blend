import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Song[] allSongsArray;  // Array to store song objects given in the first input file
        EpicBlend epicBlend;
        PlayList[] playListArray;  // Array to store playlist objects

        FileWriter output = new FileWriter("output_add_large.txt", true);

        // Take song infos from the input file and create corresponding song objects via first input file
        File file = new File("songs.txt");
        Scanner input = new Scanner(file);

        // Store all songs
        allSongsArray = new Song[Integer.parseInt(input.nextLine()) + 1];
        allSongsArray[0] = null;

        while(input.hasNextLine()) {
            String[] songInfo = input.nextLine().strip().split(" ");

            // Parse string info into integer
            int[] songInfoInt = new int[songInfo.length-1];
            int j = 0;
            for(int i=0; i<songInfo.length; i++) {
                if(i==1) {
                    continue;
                }
                songInfoInt[j] = Integer.parseInt(songInfo[i]);
                j++;
            }

            // Create song object
            Song song = new Song(songInfoInt, songInfo[1]);
            allSongsArray[song.songID] = song;
        }
        input.close();

        // Create playlists, EpicBland and process requests from the user via second input file
        file = new File("add_large.txt");
        input = new Scanner(file);

        // Create epicBland object with the given limit infos
        String[] limits = input.nextLine().strip().split(" ");
        int[] limitsInt = new int[limits.length];
        for(int i=0; i<limits.length; i++) {
            limitsInt[i] = Integer.parseInt(limits[i]);
        }

        // Create playlists and store them
        int playlistCount = input.nextInt();
        input.nextLine();
        playListArray = new PlayList[playlistCount + 1];
        playListArray[0] = null;

        Song[] currentSongsArray = new Song[allSongsArray.length];
        int index = 0;

        for(int i=0; i<playlistCount; i++) {
            int playListID = input.nextInt();
            int songCount = input.nextInt();  // Song count inside the current play list
            input.nextLine();

            PlayList playList = new PlayList();
            for(int j=0; j<songCount; j++) {
                int songID = input.nextInt();
                Song song = allSongsArray[songID];  // Get the song object with the specified song ID
                song.playlistID = playListID;  // Specify which playlist this song belongs to

                playList.songsArray.add(song);

                currentSongsArray[index] = song;
                index++;
            }
            input.nextLine();

            playListArray[playListID] = playList;
        }

        epicBlend = new EpicBlend(limitsInt, currentSongsArray, currentSongsArray, currentSongsArray, playListArray.length);
        epicBlend.createEpicBlend(playListArray);

        // Process events / requests
        int numOfEvents = input.nextInt();
        input.nextLine();

        for(int i=0; i<numOfEvents; i++) {
            String[] event = input.nextLine().strip().split(" ");
            String eventType = event[0];

            switch (eventType) {
//                case "REM" -> {
//                    int songID = Integer.parseInt(event[1]);
//                    int playlistID = Integer.parseInt(event[2]);
//                    Song deletedSong = allSongsArray[songID];
//                    playListArray[playlistID].songsArray.remove(deletedSong);
//                    epicBlend.remove(playListArray, allSongsArray, deletedSong, output);
//                }
                case "ADD" -> {
                    int songID = Integer.parseInt(event[1]);
                    int playlistID = Integer.parseInt(event[2]);
                    Song newSong = allSongsArray[songID];
                    newSong.playlistID = playlistID;
                    playListArray[playlistID].songsArray.add(newSong);
                    epicBlend.add(playListArray, allSongsArray, newSong, output);
                }
                case "ASK" -> epicBlend.ask(output);
            }
        }
        input.close();
        output.close();
    }
}