import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class EpicBlend {
    private final int categoryLimit;  // Max num of songs a playlist category can offer
    private final int heartacheLimit;  // Max num of heartache songs Epic Blend can contain
    private final int roadTripLimit;
    private final int blissfulLimit;

    // Max heaps that compare song objects according to their vibe category scores (Each heap contains same songs)
    public BinaryHeap heartacheHeap;
    public BinaryHeap roadTripHeap;
    public BinaryHeap blissfulHeap;

    // Min heaps that hold songs chosen for the EpicBlend in each vibe category
    private final BinaryHeap chosenHeartacheHeap;
    private final BinaryHeap chosenRoadTripHeap;
    private final BinaryHeap chosenBlissfulHeap;

    // Hash sets for keeping track of the songs in chosen song heaps (songs inside Epic Blend)
    private final HashSet<Song> inChosenHeartache;
    private final HashSet<Song> inChosenRoadTrip;
    private final HashSet<Song> inChosenBlissful;

    // When a song is removed it is kept note of in a hashset but not popped immediately
    private final HashSet<Song> removedSongsHeartache;
    private final HashSet<Song> removedSongsRoadTrip;
    private final HashSet<Song> removedSongsBlissful;

    // For printing the changes in the Epic Blend
    private final int[] additionsToEpicBlend;  // 0th index: heartache addition, 1st index: roadTrip addition, 2nd index: blissful addition
    private final int[] removalsFromEpicBlend;

    EpicBlend(int[] limits, Song[] heartacheArray, Song[] roadTripArray, Song[] blissfulArray) {
        this.categoryLimit = limits[0];
        this.heartacheLimit = limits[1];
        this.roadTripLimit = limits[2];
        this.blissfulLimit = limits[3];

        this.heartacheHeap = new BinaryHeap(heartacheArray,true, "heartache");
        this.roadTripHeap = new BinaryHeap(roadTripArray,true, "roadTrip");
        this.blissfulHeap = new BinaryHeap(blissfulArray,true, "blissful");

        this.chosenHeartacheHeap = new BinaryHeap(false, "heartache");
        this.chosenRoadTripHeap = new BinaryHeap(false, "roadTrip");
        this.chosenBlissfulHeap = new BinaryHeap(false, "blissful");

        this.inChosenHeartache = new HashSet<>();
        this.inChosenRoadTrip = new HashSet<>();
        this.inChosenBlissful = new HashSet<>();

        this.removedSongsHeartache = new HashSet<>();
        this.removedSongsRoadTrip = new HashSet<>();
        this.removedSongsBlissful= new HashSet<>();

        this.additionsToEpicBlend = new int[3];
        this.removalsFromEpicBlend = new int[3];
    }

    // Fill chosen min heaps at the start of the program, one time use only
    public void createEpicBlend(PlayList[] playListArray) {
        Queue<Song> bypassedQueue = new LinkedList<>();

        // Build chosen heartache min heap
        while(this.heartacheLimit > this.chosenHeartacheHeap.size() && !this.heartacheHeap.isEmpty()) {
            Song newSong = this.heartacheHeap.peek();
            PlayList playList = playListArray[newSong.playlistID];  // Playlist to which the new song belongs to
            if(this.chosenHeartacheHeap.isEmpty()) {
                if(playList.heartacheOfferedCount < this.categoryLimit) {
                    this.chosenHeartacheHeap.insert(newSong);
                    playList.heartacheOfferedCount++;
                    this.inChosenHeartache.add(newSong);
                    this.heartacheHeap.pop();
                    this.heartacheHeap.elementCount--;
                }
            }
            else {
                // If the playlist to which the new song belongs does not exceed the offered song count limit for this category
                if(playList.heartacheOfferedCount < this.categoryLimit) {
                    this.chosenHeartacheHeap.insert(newSong);
                    playList.heartacheOfferedCount++;
                    this.inChosenHeartache.add(newSong);
                }
                // The playlist this new song belongs to cannot offer more songs for this category, bypass it
                else {
                    bypassedQueue.offer(newSong);
                }
                this.heartacheHeap.pop();
                this.heartacheHeap.elementCount--;
            }
        }
        while(!bypassedQueue.isEmpty()) {
            this.heartacheHeap.insert(bypassedQueue.poll());
        }

        // Build chosen road trip min heap
        while(this.roadTripLimit > this.chosenRoadTripHeap.size() && !this.roadTripHeap.isEmpty()) {
            Song newSong = this.roadTripHeap.peek();
            PlayList playList = playListArray[newSong.playlistID];
            if(this.chosenRoadTripHeap.isEmpty()) {
                if(playList.roadTripOfferedCount < this.categoryLimit) {
                    this.chosenRoadTripHeap.insert(newSong);
                    playList.roadTripOfferedCount++;
                    this.inChosenRoadTrip.add(newSong);
                    this.roadTripHeap.pop();
                    this.roadTripHeap.elementCount--;
                }
            }
            else {
                // If the playlist to which the new song belongs does not exceed the offered song count limit for this category
                if(playList.roadTripOfferedCount < this.categoryLimit) {
                    this.chosenRoadTripHeap.insert(newSong);
                    playList.roadTripOfferedCount++;
                    this.inChosenRoadTrip.add(newSong);
                }
                // The playlist this new song belongs to cannot offer more songs for this category, bypass it
                else {
                    bypassedQueue.offer(newSong);
                }
                this.roadTripHeap.pop();
                this.roadTripHeap.elementCount--;
            }
        }
        while(!bypassedQueue.isEmpty()) {
            this.roadTripHeap.insert(bypassedQueue.poll());
        }

        // Build chosen blissful min heap
        while(this.blissfulLimit > this.chosenBlissfulHeap.size() && !this.blissfulHeap.isEmpty()) {
            Song newSong = this.blissfulHeap.peek();
            PlayList playList = playListArray[newSong.playlistID];
            if(this.chosenBlissfulHeap.isEmpty()) {
                if(playList.blissfulOfferedCount < this.categoryLimit) {
                    this.chosenBlissfulHeap.insert(newSong);
                    playList.blissfulOfferedCount++;
                    this.inChosenBlissful.add(newSong);
                    this.blissfulHeap.pop();
                    this.blissfulHeap.elementCount--;
                }
            }
            else {
                // If the playlist to which the new song belongs does not exceed the offered song count limit for this category
                if(playList.blissfulOfferedCount < this.categoryLimit) {
                    this.chosenBlissfulHeap.insert(newSong);
                    playList.blissfulOfferedCount++;
                    this.inChosenBlissful.add(newSong);
                }
                // The playlist this new song belongs to cannot offer more songs for this category, bypass it
                else {
                    bypassedQueue.offer(newSong);
                }
                this.blissfulHeap.pop();
                this.blissfulHeap.elementCount--;
            }
        }
        while(!bypassedQueue.isEmpty()) {
            this.blissfulHeap.insert(bypassedQueue.poll());
        }
    }

    // Keep note of the changes happened in the chosen songs heap (min heaps) via hashsets and print them
    private void updateHashsetsAndPrint(Song[] allSongsArray , FileWriter output) throws IOException {
        if(this.additionsToEpicBlend[0] != 0) {  // Update existing heartache songs in the Epic Blend
            Song addedHeartacheSong = allSongsArray[this.additionsToEpicBlend[0]];
            this.inChosenHeartache.add(addedHeartacheSong);
        }
        if(this.removalsFromEpicBlend[0] != 0) {
            Song deletedHeartacheSong = allSongsArray[this.removalsFromEpicBlend[0]];
            this.inChosenHeartache.remove(deletedHeartacheSong);
//            this.chosenHeartacheHeap.elementCount--;
        }

        if(this.additionsToEpicBlend[1] != 0) {  // Update existing road trip songs in the Epic Blend
            Song addedRoadTripSong = allSongsArray[this.additionsToEpicBlend[1]];
            this.inChosenRoadTrip.add(addedRoadTripSong);
        }
        if(this.removalsFromEpicBlend[1] != 0) {
            Song deletedRoadTripSong = allSongsArray[this.removalsFromEpicBlend[1]];
            this.inChosenRoadTrip.remove(deletedRoadTripSong);
//            this.chosenRoadTripHeap.elementCount--;
        }

        if(this.additionsToEpicBlend[2] != 0) {  // Update existing blissful songs in the Epic Blend
            Song addedBlissfulSong = allSongsArray[this.additionsToEpicBlend[2]];
            this.inChosenBlissful.add(addedBlissfulSong);
        }
        if(this.removalsFromEpicBlend[2] != 0) {
            Song deletedBlissfulSong = allSongsArray[this.removalsFromEpicBlend[2]];
            this.inChosenBlissful.remove(deletedBlissfulSong);
//            this.chosenBlissfulHeap.elementCount--;
        }

        // Print additions
        for(int i:this.additionsToEpicBlend) {
            output.write(i + " ");
        }
        output.write("\n");

        // Print removals
        for(int i:this.removalsFromEpicBlend) {
            output.write(i + " ");
        }
        output.write("\n");

        // Clear the arrays used in printing the modifications
        for(int i = 0; i < 3; i++) {
            this.additionsToEpicBlend[i] = 0;
            this.removalsFromEpicBlend[i] = 0;
        }
    }

    // Remove a selected song from its playlist, which might also impact Epic Blend
    public void remove(PlayList[] playListArray, Song[] allSongsArray, Song deletedSong, FileWriter output) throws IOException {
        // Remove the song from heartache category
        if(!this.inChosenHeartache.contains(deletedSong)) {  // Removed song is not in Epic Blend
            this.removedSongsHeartache.add(deletedSong);
            this.heartacheHeap.elementCount--;
        }
        else {  // Removed song is included in the heartache category of Epic Blend actively
            // Remove it
            this.inChosenHeartache.remove(deletedSong);
            playListArray[deletedSong.playlistID].heartacheOfferedCount--;
            this.removedSongsHeartache.add(deletedSong);
            this.chosenHeartacheHeap.elementCount--;

            // Choose another song in its place if there are songs waiting in the general heap, until heartache category of Epic Blend is full
            Queue<Song> poppedSongs = new LinkedList<>();
            while(this.chosenHeartacheHeap.elementCount < this.heartacheLimit && this.heartacheHeap.elementCount > 0) {
                Song newSong = this.heartacheHeap.peek();
                while(this.removedSongsHeartache.contains(newSong)) {  // Choose a new candidate song that is not inactive(removed) from the general heap
                    this.removedSongsHeartache.remove(newSong);
                    this.heartacheHeap.pop();
                    newSong = this.heartacheHeap.peek();
                }
                this.heartacheHeap.pop();  // Remove new active song from the general heap, it will be like a new song added by the user
                this.heartacheHeap.elementCount--;

                if(playListArray[newSong.playlistID].heartacheOfferedCount < this.categoryLimit) {  // Category limit has not been exceeded
                    emptySlotsWithinLimit(playListArray, this.chosenHeartacheHeap, newSong, "heartache", this.removedSongsHeartache);
                    this.removalsFromEpicBlend[0] = deletedSong.songID;
                    break;
                }
                else {  // Category limit is full but the candidate song may replace another song from the same playlist
                    Song oldSong = this.chosenHeartacheHeap.peek();
                    while(this.removedSongsHeartache.contains(oldSong)) {
                        this.removedSongsHeartache.remove(oldSong);
                        this.chosenHeartacheHeap.pop();
                        oldSong = this.chosenHeartacheHeap.peek();
                    }
                    boolean inserted = emptySlotsAtLimit(this.chosenHeartacheHeap, this.heartacheHeap, oldSong, newSong, "heartache", removedSongsHeartache);

                    if(inserted) {  // If the song inserted into chosen heap for Epic Blend
                        break;
                    }
                    else {  // Choose another candidate song if possible
                        poppedSongs.offer(this.heartacheHeap.pop());
                        this.heartacheHeap.elementCount--;
                    }
                }
            }

            // Put the popped songs back into the general heap
            while(!poppedSongs.isEmpty()) {
                this.heartacheHeap.insert(poppedSongs.poll());
            }
        }

        // Remove the song from road trip category
        if(!this.inChosenRoadTrip.contains(deletedSong)) {  // Removed song is not in Epic Blend
            this.removedSongsRoadTrip.add(deletedSong);
            this.roadTripHeap.elementCount--;
        }
        else {  // Removed song is included in Epic Blend actively
            // Remove it
            this.inChosenRoadTrip.remove(deletedSong);
            playListArray[deletedSong.playlistID].roadTripOfferedCount--;
            this.removedSongsRoadTrip.add(deletedSong);
            this.chosenRoadTripHeap.elementCount--;

            // Choose another song in its place if there are songs waiting in the general heap, until roadTrip category of Epic Blend is full
            Queue<Song> poppedSongs = new LinkedList<>();
            while(this.chosenRoadTripHeap.elementCount < this.roadTripLimit && this.roadTripHeap.elementCount > 0) {
                Song newSong = this.roadTripHeap.peek();
                while(this.removedSongsRoadTrip.contains(newSong)) {  // Choose a new candidate song that is not inactive(removed) from the general heap
                    this.removedSongsRoadTrip.remove(newSong);
                    this.roadTripHeap.pop();
                    newSong = this.roadTripHeap.peek();
                }
                this.roadTripHeap.pop();  // Remove new active song from the general heap, it will be like a new song added by the user
                this.roadTripHeap.elementCount--;

                if(playListArray[newSong.playlistID].roadTripOfferedCount < this.categoryLimit) {  // Category limit has not been exceeded
                    emptySlotsWithinLimit(playListArray, this.chosenRoadTripHeap, newSong, "roadTrip", removedSongsRoadTrip);
                    this.removalsFromEpicBlend[1] = deletedSong.songID;
                    break;
                }
                else {  // Category limit is full but the candidate song may replace another song from the same playlist
                    Song oldSong = this.chosenRoadTripHeap.peek();
                    while(this.removedSongsRoadTrip.contains(oldSong)) {
                        this.removedSongsRoadTrip.remove(oldSong);
                        this.chosenRoadTripHeap.pop();
                        oldSong = this.chosenRoadTripHeap.peek();
                    }
                    boolean inserted = emptySlotsAtLimit(this.chosenRoadTripHeap, this.roadTripHeap, oldSong, newSong, "roadTrip", removedSongsRoadTrip);

                    if(inserted) {  // If the song inserted into chosen heap for Epic Blend
                        break;
                    }
                    else {  // Choose another candidate song if possible
                        poppedSongs.offer(this.roadTripHeap.pop());
                        this.roadTripHeap.elementCount--;
                    }
                }
            }

            // Put the popped songs back into the general heap
            while(!poppedSongs.isEmpty()) {
                this.roadTripHeap.insert(poppedSongs.poll());
            }
        }

        // Remove the song from blissful category
        if(!this.inChosenBlissful.contains(deletedSong)) {  // Removed song is not in Epic Blend
            this.removedSongsBlissful.add(deletedSong);
            this.blissfulHeap.elementCount--;
        }
        else {  // Removed song is included in Epic Blend actively
            // Remove it
            this.inChosenBlissful.remove(deletedSong);
            playListArray[deletedSong.playlistID].blissfulOfferedCount--;
            this.removedSongsBlissful.add(deletedSong);
            this.chosenBlissfulHeap.elementCount--;

            // Choose another song in its place if there are songs waiting in the general heap, until blissful category of Epic Blend is full
            Queue<Song> poppedSongs = new LinkedList<>();
            while(this.chosenBlissfulHeap.elementCount < this.blissfulLimit && this.blissfulHeap.elementCount > 0) {
                Song newSong = this.blissfulHeap.peek();
                while(this.removedSongsBlissful.contains(newSong)) {  // Choose a new candidate song that is not inactive(removed) from the general heap
                    this.removedSongsBlissful.remove(newSong);
                    this.blissfulHeap.pop();
                    newSong = this.blissfulHeap.peek();
                }
                this.blissfulHeap.pop();  // Remove new active song from the general heap, it will be like a new song added by the user
                this.blissfulHeap.elementCount--;

                if(playListArray[newSong.playlistID].blissfulOfferedCount < this.categoryLimit) {  // Category limit has not been exceeded
                    emptySlotsWithinLimit(playListArray, this.chosenBlissfulHeap, newSong, "blissful", removedSongsBlissful);
                    this.removalsFromEpicBlend[2] = deletedSong.songID;
                    break;
                }
                else {  // Category limit is full but the candidate song may replace another song from the same playlist
                    Song oldSong = this.chosenBlissfulHeap.peek();
                    while(this.removedSongsBlissful.contains(oldSong)) {
                        this.removedSongsBlissful.remove(oldSong);
                        this.chosenBlissfulHeap.pop();
                        oldSong = this.chosenBlissfulHeap.peek();
                    }
                    boolean inserted = emptySlotsAtLimit(this.chosenBlissfulHeap, this.blissfulHeap, oldSong, newSong, "blissful", removedSongsBlissful);

                    if(inserted) {  // If the song inserted into chosen heap for Epic Blend
                        break;
                    }
                    else {  // Choose another candidate song if possible
                        poppedSongs.offer(this.blissfulHeap.pop());
                        this.blissfulHeap.elementCount--;
                    }
                }
            }

            // Put the popped songs back into the general heap
            while(!poppedSongs.isEmpty()) {
                this.blissfulHeap.insert(poppedSongs.poll());
            }
        }

        updateHashsetsAndPrint(allSongsArray, output);
    }

    // There are empty spots in the Epic Blend and category limit has not been exceeded for this song's playlist
    private void emptySlotsWithinLimit(PlayList[] playListArray, BinaryHeap chosenHeap, Song newSong, String heapType, HashSet<Song> removedSongs) {
        if(!removedSongs.contains(newSong)) {  // New song does not exist in the chosen Epic Blend heap in an inactivated form
            chosenHeap.insert(newSong);
        }
        else {  // If new song previously existed in the Epic Blend but was removed (inactivated, still inside the heap) activate it
            removedSongs.remove(newSong);
            chosenHeap.elementCount++;
        }

        switch (heapType) {
            case "heartache" -> {
                playListArray[newSong.playlistID].heartacheOfferedCount++;
                this.additionsToEpicBlend[0] = newSong.songID;
            }
            case "roadTrip" -> {
                playListArray[newSong.playlistID].roadTripOfferedCount++;
                this.additionsToEpicBlend[1] = newSong.songID;
            }
            case "blissful" -> {
                playListArray[newSong.playlistID].blissfulOfferedCount++;
                this.additionsToEpicBlend[2] = newSong.songID;
            }
        }
    }

    //  There are empty spots in the Epic Blend however new song's playlist's offer count is at the category limit, but
    // it may replace another song which is in the same playlist as itself
    private boolean emptySlotsAtLimit(BinaryHeap chosenHeap, BinaryHeap generalHeap, Song oldSong, Song newSong, String heapType, HashSet<Song> removedSongs) {
        return noEmptySlotsDifferentPlaylistAtLimit(chosenHeap, generalHeap, oldSong, newSong, heapType, removedSongs);
    }

    //  Epic Blend is full however new song is in the same playlist as the min scored song in Epic Blend and replaces it
    // due to higher score or lexicographically advantageous
    private void noEmptySlotsSamePlaylist(BinaryHeap chosenHeap, BinaryHeap generalHeap, Song oldSong, Song newSong, String heapType, HashSet<Song> removedSongs) {
        chosenHeap.pop();  // Remove the active song with the minimum score from chosen heap
        chosenHeap.elementCount--;
        generalHeap.insert(oldSong);
        if(!removedSongs.contains(newSong)) {
            chosenHeap.insert(newSong);
        }
        else {
            removedSongs.remove(newSong);
            chosenHeap.elementCount++;
        }

        switch (heapType) {
            case "heartache" -> {
                this.additionsToEpicBlend[0] = newSong.songID;
                this.removalsFromEpicBlend[0] = oldSong.songID;
            }
            case "roadTrip" -> {
                this.additionsToEpicBlend[1] = newSong.songID;
                this.removalsFromEpicBlend[1] = oldSong.songID;
            }
            case "blissful" -> {
                this.additionsToEpicBlend[2] = newSong.songID;
                this.removalsFromEpicBlend[2] = oldSong.songID;
            }
        }
    }

    // Epic Blend is full however new song is in a different playlist from the min scored song in Epic Blend and replaces it due to higher score or lexicographical advantage
    // New song's playlist's offer count is within category limit
    private void noEmptySlotsDifferentPlaylistWithinLimit(PlayList[] playListArray, BinaryHeap chosenHeap, BinaryHeap generalHeap, Song oldSong, Song newSong, String heapType, HashSet<Song> removedSongs) {
        chosenHeap.pop();
        chosenHeap.elementCount--;
        generalHeap.insert(oldSong);  // Put the popped song back into the corresponding category's max heap
        if(!removedSongs.contains(newSong)) {
            chosenHeap.insert(newSong);
        }
        else {
            removedSongs.remove(newSong);
            chosenHeap.elementCount++;
        }

        switch (heapType) {
            case "heartache" -> {
                playListArray[oldSong.playlistID].heartacheOfferedCount--;
                playListArray[newSong.playlistID].heartacheOfferedCount++;
                this.additionsToEpicBlend[0] = newSong.songID;
                this.removalsFromEpicBlend[0] = oldSong.songID;
            }
            case "roadTrip" -> {
                playListArray[oldSong.playlistID].roadTripOfferedCount--;
                playListArray[newSong.playlistID].roadTripOfferedCount++;
                this.additionsToEpicBlend[1] = newSong.songID;
                this.removalsFromEpicBlend[1] = oldSong.songID;
            }
            case "blissful" -> {
                playListArray[oldSong.playlistID].blissfulOfferedCount--;
                playListArray[newSong.playlistID].blissfulOfferedCount++;
                this.additionsToEpicBlend[2] = newSong.songID;
                this.removalsFromEpicBlend[2] = oldSong.songID;
            }
        }
    }

    // Epic Blend is full however new song has a chance to enter Epic Blend based on its score and is in a different playlist from the min scored song in Epic Blend
    // New song's playlist's offer count is at the category limit, but it may replace another song which is in the same playlist as itself
    private boolean noEmptySlotsDifferentPlaylistAtLimit(BinaryHeap chosenHeap, BinaryHeap generalHeap, Song oldSong, Song newSong, String heapType, HashSet<Song> removedSongs) {
        Queue<Song> poppedSongs = new LinkedList<>();  // Temporary queue for keeping popped songs while doing a search
        // Search for the minimum scored song in the chosen heartache / road trip / blissful heap that is from the same playlist as the new song
        while(!oldSong.playlistID.equals(newSong.playlistID) || removedSongs.contains(oldSong)) {
            if(removedSongs.contains(oldSong)) {  // Remove inactive remnants of previous songs
                removedSongs.remove(oldSong);
                chosenHeap.pop();
                oldSong = chosenHeap.peek();
                continue;
            }
            poppedSongs.offer(chosenHeap.pop());
            chosenHeap.elementCount--;
            oldSong = chosenHeap.peek();
        }

        int oldSongScore = 0;
        int newSongScore = 0;

        switch (heapType) {
            case "heartache" -> {
                oldSongScore = oldSong.heartacheScore;
                newSongScore = newSong.heartacheScore;
            }
            case "roadTrip" -> {
                oldSongScore = oldSong.roadTripScore;
                newSongScore = newSong.roadTripScore;
            }
            case "blissful" -> {
                oldSongScore = oldSong.blissfulScore;
                newSongScore = newSong.blissfulScore;
            }
        }

        //  New song should replace the old song that is from the same playlist as itself as new song has higher score
        // or lexicographically advantageous
        if(oldSongScore < newSongScore || (oldSongScore == newSongScore && newSong.songName.compareTo(oldSong.songName) < 0)) {
            generalHeap.insert(chosenHeap.pop());
            chosenHeap.elementCount--;
            if(!removedSongs.contains(newSong)) {
                chosenHeap.insert(newSong);
            }
            else {  // If this new song exists in chosen heap in an inactive form, activate it
                removedSongs.remove(newSong);
                chosenHeap.elementCount++;
            }

            switch (heapType) {
                case "heartache" -> {
                    this.additionsToEpicBlend[0] = newSong.songID;
                    this.removalsFromEpicBlend[0] = oldSong.songID;
                }
                case "roadTrip" -> {
                    this.additionsToEpicBlend[1] = newSong.songID;
                    this.removalsFromEpicBlend[1] = oldSong.songID;
                }
                case "blissful" -> {
                    this.additionsToEpicBlend[2] = newSong.songID;
                    this.removalsFromEpicBlend[2] = oldSong.songID;
                }
            }

            while(!poppedSongs.isEmpty()) {
                chosenHeap.insert(poppedSongs.poll());  // Put back the popped songs
            }

            return true;  // New song successfully inserted
        }
        else {  // New song cannot enter the chosen heap due to category limit
            if(!removedSongs.contains(newSong)) {
                generalHeap.insert(newSong);
            }
            else {
                removedSongs.remove(newSong);
                generalHeap.elementCount++;
            }

            while(!poppedSongs.isEmpty()) {
                chosenHeap.insert(poppedSongs.poll());  // Put back the popped songs
            }

            return false;  // New song could not be inserted into the chosen heartache / roadTrip / blissful heap for Epic Blend
        }
    }

    // Add a new song to one of the playlists, which may change Epic Blend
    public void add(PlayList[] playListArray, Song[] allSongsArray, Song newSong, FileWriter output) throws IOException {
        // Check heartache category for updates
        if(this.chosenHeartacheHeap.elementCount < this.heartacheLimit) {  // There are empty slots in the Epic Blend
            if(playListArray[newSong.playlistID].heartacheOfferedCount < this.categoryLimit) {  // Category limit has not been exceeded
                emptySlotsWithinLimit(playListArray, this.chosenHeartacheHeap, newSong, "heartache", this.removedSongsHeartache);
            }
            else {  // Category limit is full but new song may replace another song from the same playlist
                Song oldSong = this.chosenHeartacheHeap.peek();
                while(this.removedSongsHeartache.contains(oldSong)) {
                    this.removedSongsHeartache.remove(oldSong);
                    this.chosenHeartacheHeap.pop();
                    oldSong = this.chosenHeartacheHeap.peek();
                }
                emptySlotsAtLimit(this.chosenHeartacheHeap, this.heartacheHeap, oldSong, newSong, "heartache", this.removedSongsHeartache);
            }
        }
        else { // Chosen Heartache Heap is full, only replacements can be done
            Song oldSong = this.chosenHeartacheHeap.peek();  // Song with the minimum heartache score that is in Epic Blend
            while(this.removedSongsHeartache.contains(oldSong)) {  // If old song is an inactive song get rid of it and choose another song
                this.removedSongsHeartache.remove(oldSong);
                this.chosenHeartacheHeap.pop();
                oldSong = this.chosenHeartacheHeap.peek();
            }
            // New song has a chance to enter Epic Blend either because of having a higher score or the same score but lexicographically smaller song name
            if(newSong.heartacheScore > oldSong.heartacheScore || (oldSong.heartacheScore.equals(newSong.heartacheScore) && newSong.songName.compareTo(oldSong.songName) < 0)) {
                if(newSong.playlistID.equals(oldSong.playlistID)) {  // Both are from the same playlist
                    noEmptySlotsSamePlaylist(this.chosenHeartacheHeap, this.heartacheHeap, oldSong, newSong, "heartache", this.removedSongsHeartache);
                }
                else {  // The new and old songs are from different playlists, update offered count of each playlist
                    if(playListArray[newSong.playlistID].heartacheOfferedCount < this.categoryLimit) {  // New song's playlist does not exceed offer limit for this category
                        noEmptySlotsDifferentPlaylistWithinLimit(playListArray, this.chosenHeartacheHeap, this.heartacheHeap, oldSong, newSong, "heartache", this.removedSongsHeartache);
                    }
                    else {  // New song's playlist exceeds the offer limit for this category, but it may replace another song which is in the same playlist as itself
                        noEmptySlotsDifferentPlaylistAtLimit(this.chosenHeartacheHeap, this.heartacheHeap, oldSong, newSong, "heartache", this.removedSongsHeartache);
                    }
                }
            }
            else {  // New song's score is not enough to make it into the heartache category of Epic Blend
                if(!this.removedSongsHeartache.contains(newSong)) {
                    this.heartacheHeap.insert(newSong);
                }
                else {
                    this.removedSongsHeartache.remove(newSong);
                    this.heartacheHeap.elementCount++;
                }
            }
        }

        // Check roadTrip category for updates
        if(this.chosenRoadTripHeap.elementCount < this.roadTripLimit) {  // There are empty slots in the Epic Blend
            if(playListArray[newSong.playlistID].roadTripOfferedCount < this.categoryLimit) {  // Category limit has not been exceeded
                emptySlotsWithinLimit(playListArray, this.chosenRoadTripHeap, newSong, "roadTrip", this.removedSongsRoadTrip);
            }
            else {  // Category limit is full but new song may replace another song from the same playlist
                Song oldSong = this.chosenRoadTripHeap.peek();
                while(this.removedSongsRoadTrip.contains(oldSong)) {
                    this.removedSongsRoadTrip.remove(oldSong);
                    this.chosenRoadTripHeap.pop();
                    oldSong = this.chosenRoadTripHeap.peek();
                }
                emptySlotsAtLimit(this.chosenRoadTripHeap, this.roadTripHeap, oldSong, newSong, "roadTrip", this.removedSongsRoadTrip);
            }
        }
        else { // Chosen RoadTrip Heap is full, only replacements can be done
            Song oldSong = this.chosenRoadTripHeap.peek();  // Song with the minimum roadTrip score that is in Epic Blend
            while(this.removedSongsRoadTrip.contains(oldSong)) {  // If old song is an inactive song get rid of it and choose another song
                this.removedSongsRoadTrip.remove(oldSong);
                this.chosenRoadTripHeap.pop();
                oldSong = this.chosenRoadTripHeap.peek();
            }
            // New song has a chance to enter Epic Blend either because of having a higher score or the same score but lexicographically smaller song name
            if(newSong.roadTripScore > oldSong.roadTripScore || (oldSong.roadTripScore.equals(newSong.roadTripScore) && newSong.songName.compareTo(oldSong.songName) < 0)) {
                if(newSong.playlistID.equals(oldSong.playlistID)) {  // Both are from the same playlist
                    noEmptySlotsSamePlaylist(this.chosenRoadTripHeap, this.roadTripHeap, oldSong, newSong, "roadTrip", this.removedSongsRoadTrip);
                }
                else {  // The new and old songs are from different playlists, update offered count of each playlist
                    if(playListArray[newSong.playlistID].roadTripOfferedCount < this.categoryLimit) {  // New song's playlist does not exceed offer limit for this category
                        noEmptySlotsDifferentPlaylistWithinLimit(playListArray, this.chosenRoadTripHeap, this.roadTripHeap, oldSong, newSong, "roadTrip", this.removedSongsRoadTrip);
                    }
                    else {  // New song's playlist exceeds the offer limit for this category, but it may replace another song which is in the same playlist as itself
                        noEmptySlotsDifferentPlaylistAtLimit(this.chosenRoadTripHeap, this.roadTripHeap, oldSong, newSong, "roadTrip", this.removedSongsRoadTrip);
                    }
                }
            }
            else {  // New song's score is not enough to make it into the roadTrip category of Epic Blend
                if(!this.removedSongsRoadTrip.contains(newSong)) {
                    this.roadTripHeap.insert(newSong);
                }
                else {
                    this.removedSongsRoadTrip.remove(newSong);
                    this.roadTripHeap.elementCount++;
                }
            }
        }

        // Check blissful category for updates
        if(this.chosenBlissfulHeap.elementCount < this.blissfulLimit) {  // There are empty slots in the Epic Blend
            if(playListArray[newSong.playlistID].blissfulOfferedCount < this.categoryLimit) {  // Category limit has not been exceeded
                emptySlotsWithinLimit(playListArray, this.chosenBlissfulHeap, newSong, "blissful", this.removedSongsBlissful);
            }
            else {  // Category limit is full but new song may replace another song from the same playlist
                Song oldSong = this.chosenBlissfulHeap.peek();
                while(this.removedSongsBlissful.contains(oldSong)) {
                    this.removedSongsBlissful.remove(oldSong);
                    this.chosenBlissfulHeap.pop();
                    oldSong = this.chosenBlissfulHeap.peek();
                }
                emptySlotsAtLimit(this.chosenBlissfulHeap, this.blissfulHeap, oldSong, newSong, "blissful", this.removedSongsBlissful);
            }
        }
        else { // Chosen Blissful Heap is full, only replacements can be done
            Song oldSong = this.chosenBlissfulHeap.peek();  // Song with the minimum blissful score that is in Epic Blend
            while(this.removedSongsBlissful.contains(oldSong)) {  // If old song is an inactive song get rid of it and choose another song
                this.removedSongsBlissful.remove(oldSong);
                this.chosenBlissfulHeap.pop();
                oldSong = this.chosenBlissfulHeap.peek();
            }
            // New song has a chance to enter Epic Blend either because of having a higher score or the same score but lexicographically smaller song name
            if(newSong.blissfulScore > oldSong.blissfulScore || (oldSong.blissfulScore.equals(newSong.blissfulScore) && newSong.songName.compareTo(oldSong.songName) < 0)) {
                if(newSong.playlistID.equals(oldSong.playlistID)) {  // Both are from the same playlist
                    noEmptySlotsSamePlaylist(this.chosenBlissfulHeap, this.blissfulHeap, oldSong, newSong, "blissful", this.removedSongsBlissful);
                }
                else {  // The new and old songs are from different playlists, update offered count of each playlist
                    if(playListArray[newSong.playlistID].blissfulOfferedCount < this.categoryLimit) {  // New song's playlist does not exceed offer limit for this category
                        noEmptySlotsDifferentPlaylistWithinLimit(playListArray, this.chosenBlissfulHeap, this.blissfulHeap, oldSong, newSong, "blissful", this.removedSongsBlissful);
                    }
                    else {  // New song's playlist exceeds the offer limit for this category, but it may replace another song which is in the same playlist as itself
                        noEmptySlotsDifferentPlaylistAtLimit(this.chosenBlissfulHeap, this.blissfulHeap, oldSong, newSong, "blissful", this.removedSongsBlissful);
                    }
                }
            }
            else {  // New song's score is not enough to make it into the blissful category of Epic Blend
                if(!this.removedSongsBlissful.contains(newSong)) {
                    this.blissfulHeap.insert(newSong);
                }
                else {
                    this.removedSongsBlissful.remove(newSong);
                    this.blissfulHeap.elementCount++;
                }
            }
        }

        updateHashsetsAndPrint(allSongsArray, output);
    }

    // Print epicBlend in descending order of play counts
    public void ask(FileWriter output) throws IOException {
        HashSet<Song> songsCombined = new HashSet<>();
        Song[] songsCombinedFinal;  // Combination of songs from 3 categories without null elements

        // Traverse all songs from the 3 chosen min heaps and combine them via inserting them into an array using songID as index
        for(Song song:this.chosenHeartacheHeap.array) {
            if(song == null) {
                continue;
            }
            songsCombined.add(song);
        }

        for(Song song:this.chosenRoadTripHeap.array) {
            if(song == null) {
                continue;
            }
            songsCombined.add(song);
        }

        for(Song song:this.chosenBlissfulHeap.array) {
            if(song == null) {
                continue;
            }
            songsCombined.add(song);
        }

        songsCombinedFinal = new Song[songsCombined.size()];
        int index = 0;

        for(Song song:songsCombined) {
            songsCombinedFinal[index] = song;
            index++;
        }

        // Sort songs by their play count and print the play list
        quickSort(songsCombinedFinal, 0, songsCombinedFinal.length-1);
        for(int i=0; i<songsCombinedFinal.length; i++) {
            output.write(songsCombinedFinal[i].songID + "");
            if(i != songsCombinedFinal.length - 1) {
                output.write(" ");
            }
            else {
                output.write("\n");
            }
        }
    }

    // Sort song objects by their play count
    private static void quickSort(Song[] songsCombinedFinal, int low, int high) {
        if(low < high) {
            int partitionIndex = partition(songsCombinedFinal, low, high);
            quickSort(songsCombinedFinal, low, partitionIndex - 1);
            quickSort(songsCombinedFinal, partitionIndex + 1, high);
        }
    }

    private static int partition(Song[] songsCombinedFinal, int low, int high) {
        int pivot = songsCombinedFinal[high].playCount;
        int i = low - 1;
        for(int j=low; j<high; j++) {
            if(songsCombinedFinal[j].playCount > pivot) {
                i++;
                swap(songsCombinedFinal, i, j);
            }
        }
        swap(songsCombinedFinal, i + 1, high);
        return i + 1;
    }

    private static void swap(Song[] songsCombinedFinal, int i, int j) {
        Song tmpSong = songsCombinedFinal[i];
        songsCombinedFinal[i] = songsCombinedFinal[j];
        songsCombinedFinal[j] = tmpSong;
    }
}
