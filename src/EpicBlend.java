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

    //  Array of min heaps. Each min heap represents a playlist, and they contain songs chosen for Epic Blend. Aim is to lower
    // add() operation time by accessing a song with a specific playlistID chosen for the Epic Blend (inside chosen minheap)
    private final BinaryHeap[] playlistMinheapsArrayHeartache;
    private final BinaryHeap[] playlistMinheapsArrayRoadTrip;
    private final BinaryHeap[] playlistMinheapsArrayBlissful;

    // Hash sets for keeping track of the songs in chosen song heaps (songs inside Epic Blend)
    private final HashSet<Song> inChosenHeartache;
    private final HashSet<Song> inChosenRoadTrip;
    private final HashSet<Song> inChosenBlissful;

    // When a song is removed it is kept note of in a hashset but not popped immediately
    private final HashSet<Song> removedSongsChosenHeartache;  // Song is removed from the chosen Heartache heap
    private final HashSet<Song> removedSongsChosenRoadTrip;
    private final HashSet<Song> removedSongsChosenBlissful;

    private final HashSet<Song> removedSongsGeneralHeartache;  // Song is removed from the general Heartache heap
    private final HashSet<Song> removedSongsGeneralRoadTrip;
    private final HashSet<Song> removedSongsGeneralBlissful;

    // For printing the changes in the Epic Blend
    private final int[] additionsToEpicBlend;  // 0th index: heartache addition, 1st index: roadTrip addition, 2nd index: blissful addition
    private final int[] removalsFromEpicBlend;

    EpicBlend(int[] limits, Song[] heartacheArray, Song[] roadTripArray, Song[] blissfulArray, int playlistCount) {
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

        this.playlistMinheapsArrayHeartache = new BinaryHeap[playlistCount];  // 1st index corresponds to 1st playlist and so on
        this.playlistMinheapsArrayHeartache[0] = null;  // 0th index will be null
        for(int i=1; i<playlistCount; i++) {
            this.playlistMinheapsArrayHeartache[i] = new BinaryHeap(false, "heartache");
        }

        this.playlistMinheapsArrayRoadTrip = new BinaryHeap[playlistCount];
        this.playlistMinheapsArrayRoadTrip[0] = null;
        for(int i=1; i<playlistCount; i++) {
            this.playlistMinheapsArrayRoadTrip[i] = new BinaryHeap(false, "roadTrip");
        }

        this.playlistMinheapsArrayBlissful = new BinaryHeap[playlistCount];
        this.playlistMinheapsArrayBlissful[0] = null;
        for(int i=1; i<playlistCount; i++) {
            this.playlistMinheapsArrayBlissful[i] = new BinaryHeap(false, "blissful");
        }

        this.inChosenHeartache = new HashSet<>();
        this.inChosenRoadTrip = new HashSet<>();
        this.inChosenBlissful = new HashSet<>();

        this.removedSongsChosenHeartache = new HashSet<>();
        this.removedSongsChosenRoadTrip = new HashSet<>();
        this.removedSongsChosenBlissful= new HashSet<>();

        this.removedSongsGeneralHeartache = new HashSet<>();
        this.removedSongsGeneralRoadTrip = new HashSet<>();
        this.removedSongsGeneralBlissful = new HashSet<>();

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
                    this.playlistMinheapsArrayHeartache[newSong.playlistID].insert(newSong);
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
                    this.playlistMinheapsArrayHeartache[newSong.playlistID].insert(newSong);
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
                    this.playlistMinheapsArrayRoadTrip[newSong.playlistID].insert(newSong);
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
                    this.playlistMinheapsArrayRoadTrip[newSong.playlistID].insert(newSong);
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
                    this.playlistMinheapsArrayBlissful[newSong.playlistID].insert(newSong);
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
                    this.playlistMinheapsArrayBlissful[newSong.playlistID].insert(newSong);
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
        for(int i=0; i<this.additionsToEpicBlend.length; i++) {
            output.write(this.additionsToEpicBlend[i] + "");
            if(i != this.additionsToEpicBlend.length-1) {
                output.write(" ");
            }
            else {
                output.write("\n");
            }
        }

        // Print removals
        for(int i=0; i<this.removalsFromEpicBlend.length; i++) {
            output.write(this.removalsFromEpicBlend[i] + "");
            if(i != this.removalsFromEpicBlend.length-1) {
                output.write(" ");
            }
            else {
                output.write("\n");
            }
        }

        // Clear the arrays used in printing the modifications
        for(int i = 0; i < 3; i++) {
            this.additionsToEpicBlend[i] = 0;
            this.removalsFromEpicBlend[i] = 0;
        }
    }

    // Remove a selected song from its playlist, which might also impact Epic Blend
    public void remove(PlayList[] playListArray, Song[] allSongsArray, Song deletedSong, FileWriter output) throws IOException {
        // Remove the song from heartache category
        if(!this.inChosenHeartache.contains(deletedSong)) {  // Song to be removed is not in Epic Blend
            this.removedSongsGeneralHeartache.add(deletedSong);  // Inactivate the song in the general max heap
            this.heartacheHeap.elementCount--;
        }
        else {  // Removed song is included in the heartache category of Epic Blend actively
            // Remove it
            this.inChosenHeartache.remove(deletedSong);
            playListArray[deletedSong.playlistID].heartacheOfferedCount--;
            this.removedSongsChosenHeartache.add(deletedSong);  // Inactivate the song in the chosen min heap
            this.chosenHeartacheHeap.elementCount--;
            this.removalsFromEpicBlend[0] = deletedSong.songID;

            // Choose another song in its place if there are songs waiting in the general heap, until heartache category of Epic Blend is full
            Queue<Song> poppedSongs = new LinkedList<>();
            while(this.chosenHeartacheHeap.elementCount < this.heartacheLimit && this.heartacheHeap.elementCount > 0) {
                Song newSong = this.heartacheHeap.peek();
                while(this.removedSongsGeneralHeartache.contains(newSong)) {  // Choose a new candidate song that is not inactive(removed) from the general heap
                    this.removedSongsGeneralHeartache.remove(newSong);
                    this.heartacheHeap.pop();
                    newSong = this.heartacheHeap.peek();
                }
                this.heartacheHeap.pop();  // Remove new active song from the general heap, it will be like a new song added by the user
                this.heartacheHeap.elementCount--;

                if(playListArray[newSong.playlistID].heartacheOfferedCount < this.categoryLimit) {  // Category limit has not been exceeded
                    emptySlotsWithinLimit(playListArray, this.chosenHeartacheHeap, this.playlistMinheapsArrayHeartache[newSong.playlistID], newSong, "heartache", this.removedSongsChosenHeartache);
//                    this.removalsFromEpicBlend[0] = deletedSong.songID;
                    break;
                }
                else {
                    poppedSongs.offer(newSong);
                }
//                else {  // Category limit is full but the candidate song may replace another song from the same playlist
//                    Song oldSong = this.chosenHeartacheHeap.peek();
//                    while(this.removedSongsChosenHeartache.contains(oldSong)) {
//                        this.removedSongsChosenHeartache.remove(oldSong);
//                        this.chosenHeartacheHeap.pop();
//                        oldSong = this.chosenHeartacheHeap.peek();
//                    }
//                    boolean inserted = emptySlotsAtLimit(this.chosenHeartacheHeap, this.heartacheHeap, oldSong, newSong, "heartache", this.removedSongsChosenHeartache, this.removedSongsGeneralHeartache, true);
//
//                    if(inserted) {  // If the song inserted into chosen heap for Epic Blend
//                        break;
//                    }
//                    else {  // Choose another candidate song if possible
//                        poppedSongs.offer(this.heartacheHeap.pop());
//                        this.heartacheHeap.elementCount--;
//                    }
//                }
            }

            // Put the popped songs back into the general heap
            while(!poppedSongs.isEmpty()) {
                this.heartacheHeap.insert(poppedSongs.poll());
            }
        }

        // Remove the song from road trip category
        if(!this.inChosenRoadTrip.contains(deletedSong)) {  // Song to be removed is not in Epic Blend
            this.removedSongsGeneralRoadTrip.add(deletedSong);  // Inactivate the song in the general max heap
            this.roadTripHeap.elementCount--;
        }
        else {  // Removed song is included in the roadTrip category of Epic Blend actively
            // Remove it
            this.inChosenRoadTrip.remove(deletedSong);
            playListArray[deletedSong.playlistID].roadTripOfferedCount--;
            this.removedSongsChosenRoadTrip.add(deletedSong);  // Inactivate the song in the chosen min heap
            this.chosenRoadTripHeap.elementCount--;
            this.removalsFromEpicBlend[1] = deletedSong.songID;

            // Choose another song in its place if there are songs waiting in the general heap, until roadTrip category of Epic Blend is full
            Queue<Song> poppedSongs = new LinkedList<>();
            while(this.chosenRoadTripHeap.elementCount < this.roadTripLimit && this.roadTripHeap.elementCount > 0) {
                Song newSong = this.roadTripHeap.peek();
                while(this.removedSongsGeneralRoadTrip.contains(newSong)) {  // Choose a new candidate song that is not inactive(removed) from the general heap
                    this.removedSongsGeneralRoadTrip.remove(newSong);
                    this.roadTripHeap.pop();
                    newSong = this.roadTripHeap.peek();
                }
                this.roadTripHeap.pop();  // Remove new active song from the general heap, it will be like a new song added by the user
                this.roadTripHeap.elementCount--;

                if(playListArray[newSong.playlistID].roadTripOfferedCount < this.categoryLimit) {  // Category limit has not been exceeded
                    emptySlotsWithinLimit(playListArray, this.chosenRoadTripHeap, this.playlistMinheapsArrayRoadTrip[newSong.playlistID], newSong, "roadTrip", this.removedSongsChosenRoadTrip);
//                    this.removalsFromEpicBlend[0] = deletedSong.songID;
                    break;
                }
                else {
                    poppedSongs.offer(newSong);
                }
//                else {  // Category limit is full but the candidate song may replace another song from the same playlist
//                    Song oldSong = this.chosenRoadTripHeap.peek();
//                    while(this.removedSongsChosenRoadTrip.contains(oldSong)) {
//                        this.removedSongsChosenRoadTrip.remove(oldSong);
//                        this.chosenRoadTripHeap.pop();
//                        oldSong = this.chosenRoadTripHeap.peek();
//                    }
//                    boolean inserted = emptySlotsAtLimit(this.chosenRoadTripHeap, this.roadTripHeap, oldSong, newSong, "roadTrip", this.removedSongsChosenRoadTrip, this.removedSongsGeneralRoadTrip, true);
//
//                    if(inserted) {  // If the song inserted into chosen heap for Epic Blend
//                        break;
//                    }
//                    else {  // Choose another candidate song if possible
//                        poppedSongs.offer(this.roadTripHeap.pop());
//                        this.roadTripHeap.elementCount--;
//                    }
//                }
            }

            // Put the popped songs back into the general heap
            while(!poppedSongs.isEmpty()) {
                this.roadTripHeap.insert(poppedSongs.poll());
            }
        }

        // Remove the song from blissful category
        if(!this.inChosenBlissful.contains(deletedSong)) {  // Song to be removed is not in Epic Blend
            this.removedSongsGeneralBlissful.add(deletedSong);  // Inactivate the song in the general max heap
            this.blissfulHeap.elementCount--;
        }
        else {  // Removed song is included in the blissful category of Epic Blend actively
            // Remove it
            this.inChosenBlissful.remove(deletedSong);
            playListArray[deletedSong.playlistID].blissfulOfferedCount--;
            this.removedSongsChosenBlissful.add(deletedSong);  // Inactivate the song in the chosen min heap
            this.chosenBlissfulHeap.elementCount--;
            this.removalsFromEpicBlend[2] = deletedSong.songID;

            // Choose another song in its place if there are songs waiting in the general heap, until blissful category of Epic Blend is full
            Queue<Song> poppedSongs = new LinkedList<>();
            while(this.chosenBlissfulHeap.elementCount < this.blissfulLimit && this.blissfulHeap.elementCount > 0) {
                Song newSong = this.blissfulHeap.peek();
                while(this.removedSongsGeneralBlissful.contains(newSong)) {  // Choose a new candidate song that is not inactive(removed) from the general heap
                    this.removedSongsGeneralBlissful.remove(newSong);
                    this.blissfulHeap.pop();
                    newSong = this.blissfulHeap.peek();
                }
                this.blissfulHeap.pop();  // Remove new active song from the general heap, it will be like a new song added by the user
                this.blissfulHeap.elementCount--;

                if(playListArray[newSong.playlistID].blissfulOfferedCount < this.categoryLimit) {  // Category limit has not been exceeded
                    emptySlotsWithinLimit(playListArray, this.chosenBlissfulHeap, this.playlistMinheapsArrayBlissful[newSong.playlistID], newSong, "blissful", this.removedSongsChosenBlissful);
//                    this.removalsFromEpicBlend[0] = deletedSong.songID;
                    break;
                }
                else {
                    poppedSongs.offer(newSong);
                }
//                else {  // Category limit is full but the candidate song may replace another song from the same playlist
//                    Song oldSong = this.chosenBlissfulHeap.peek();
//                    while(this.removedSongsChosenBlissful.contains(oldSong)) {
//                        this.removedSongsChosenBlissful.remove(oldSong);
//                        this.chosenBlissfulHeap.pop();
//                        oldSong = this.chosenBlissfulHeap.peek();
//                    }
//                    boolean inserted = emptySlotsAtLimit(this.chosenBlissfulHeap, this.blissfulHeap, oldSong, newSong, "blissful", this.removedSongsChosenBlissful, this.removedSongsGeneralBlissful, true);
//
//                    if(inserted) {  // If the song inserted into chosen heap for Epic Blend
//                        break;
//                    }
//                    else {  // Choose another candidate song if possible
//                        poppedSongs.offer(this.blissfulHeap.pop());
//                        this.blissfulHeap.elementCount--;
//                    }
//                }
            }

            // Put the popped songs back into the general heap
            while(!poppedSongs.isEmpty()) {
                this.blissfulHeap.insert(poppedSongs.poll());
            }
        }

        updateHashsetsAndPrint(allSongsArray, output);
    }

    // There are empty spots in the Epic Blend and category limit has not been exceeded for this song's playlist
    private void emptySlotsWithinLimit(PlayList[] playListArray, BinaryHeap chosenHeap, BinaryHeap playlistChosenMinHeap, Song newSong, String heapType, HashSet<Song> removedSongsChosen) {
        if(!removedSongsChosen.contains(newSong)) {  // New song does not exist in the chosen Epic Blend heap in an inactivated form
            chosenHeap.insert(newSong);
            playlistChosenMinHeap.insert(newSong);
        }
        else {  // If new song previously existed in the Epic Blend but was removed (inactivated, still inside the heap) activate it
            removedSongsChosen.remove(newSong);
            chosenHeap.elementCount++;
            playlistChosenMinHeap.insert(newSong);
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
    private boolean emptySlotsAtLimit(BinaryHeap chosenHeap, BinaryHeap generalHeap, BinaryHeap playlistChosenMinHeap, Song oldSong, Song newSong, String heapType, HashSet<Song> removedSongsChosen, HashSet<Song> removedSongsGeneral, boolean comingFromRemoveMethod) {
        return noEmptySlotsDifferentPlaylistAtLimit(chosenHeap, generalHeap, playlistChosenMinHeap, oldSong, newSong, heapType, removedSongsChosen, removedSongsGeneral, comingFromRemoveMethod);
    }

    //  Epic Blend is full however new song is in the same playlist as the min scored song in Epic Blend and replaces it
    // due to higher score or lexicographically advantageous
    private void noEmptySlotsSamePlaylist(BinaryHeap chosenHeap, BinaryHeap generalHeap, Song oldSong, Song newSong, String heapType, HashSet<Song> removedSongsChosen) {
        chosenHeap.pop();  // Remove the active song with the minimum score from chosen heap
        chosenHeap.elementCount--;
        generalHeap.insert(oldSong);
        if(!removedSongsChosen.contains(newSong)) {
            chosenHeap.insert(newSong);
        }
        else {
            removedSongsChosen.remove(newSong);
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
    private void noEmptySlotsDifferentPlaylistWithinLimit(PlayList[] playListArray, BinaryHeap chosenHeap, BinaryHeap generalHeap, Song oldSong, Song newSong, String heapType, HashSet<Song> removedSongsChosen) {
        chosenHeap.pop();
        chosenHeap.elementCount--;
        generalHeap.insert(oldSong);  // Put the popped song back into the corresponding category's max heap
        if(!removedSongsChosen.contains(newSong)) {
            chosenHeap.insert(newSong);
        }
        else {
            removedSongsChosen.remove(newSong);
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
    private boolean noEmptySlotsDifferentPlaylistAtLimit(BinaryHeap chosenHeap, BinaryHeap generalHeap, BinaryHeap playlistChosenMinHeap, Song oldSong, Song newSong, String heapType, HashSet<Song> removedSongsChosen, HashSet<Song> removedSongsGeneral, boolean comingFromRemoveMethod) {
//        Queue<Song> poppedSongs = new LinkedList<>();  // Temporary queue for keeping popped songs while doing a search
//        // Search for the minimum scored song in the chosen heartache / road trip / blissful heap that is from the same playlist as the new song
//        while(!oldSong.playlistID.equals(newSong.playlistID) || removedSongsChosen.contains(oldSong)) {
//            if(removedSongsChosen.contains(oldSong)) {  // Remove inactive remnants of previous songs
//                removedSongsChosen.remove(oldSong);
//                chosenHeap.pop();
//                oldSong = chosenHeap.peek();
//                continue;
//            }
//            poppedSongs.offer(chosenHeap.pop());
//            chosenHeap.elementCount--;
//            oldSong = chosenHeap.peek();
//        }

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
            generalHeap.insert(playlistChosenMinHeap.pop());
            playlistChosenMinHeap.insert(newSong);

            removedSongsChosen.add(oldSong);  // Remove old song from the chosen Epic Blend min heap by inactivating it
            chosenHeap.elementCount--;
            if(!removedSongsChosen.contains(newSong)) {
                chosenHeap.insert(newSong);
            }
            else {  // If this new song exists in chosen heap in an inactive form, activate it
                removedSongsChosen.remove(newSong);
                chosenHeap.elementCount++;
            }

            switch (heapType) {
                case "heartache" -> {
                    this.additionsToEpicBlend[0] = newSong.songID;
                    if(!comingFromRemoveMethod) {  // For remove method this is already done before
                        this.removalsFromEpicBlend[0] = oldSong.songID;
                    }
                }
                case "roadTrip" -> {
                    this.additionsToEpicBlend[1] = newSong.songID;
                    if(!comingFromRemoveMethod) {
                        this.removalsFromEpicBlend[1] = oldSong.songID;
                    }
                }
                case "blissful" -> {
                    this.additionsToEpicBlend[2] = newSong.songID;
                    if(!comingFromRemoveMethod) {
                        this.removalsFromEpicBlend[2] = oldSong.songID;
                    }
                }
            }

//            while(!poppedSongs.isEmpty()) {
//                chosenHeap.insert(poppedSongs.poll());  // Put back the popped songs
//            }

            return true;  // New song successfully inserted
        }
        else {  // New song cannot enter the chosen heap due to category limit
            if(!removedSongsGeneral.contains(newSong)) {
                generalHeap.insert(newSong);
            }
            else {  // It was removed, so activate it
                removedSongsGeneral.remove(newSong);
                generalHeap.elementCount++;
            }

//            while(!poppedSongs.isEmpty()) {
//                chosenHeap.insert(poppedSongs.poll());  // Put back the popped songs
//            }

            return false;  // New song could not be inserted into the chosen heartache / roadTrip / blissful heap for Epic Blend
        }
    }

    // Add a new song to one of the playlists, which may change Epic Blend
    public void add(PlayList[] playListArray, Song[] allSongsArray, Song newSong, FileWriter output) throws IOException {
        // Check heartache category for updates
        if(this.chosenHeartacheHeap.elementCount < this.heartacheLimit) {  // There are empty slots in the Epic Blend
            if(playListArray[newSong.playlistID].heartacheOfferedCount < this.categoryLimit) {  // Category limit has not been exceeded
                emptySlotsWithinLimit(playListArray, this.chosenHeartacheHeap, this.playlistMinheapsArrayHeartache[newSong.playlistID], newSong, "heartache", this.removedSongsChosenHeartache);
            }
            else {  // Category limit is full but new song may replace another song from the same playlist
                Song oldSong = this.playlistMinheapsArrayHeartache[newSong.playlistID].peek();
                while(this.removedSongsChosenHeartache.contains(oldSong) || !oldSong.playlistID.equals(newSong.playlistID)) {
                    this.playlistMinheapsArrayHeartache[newSong.playlistID].pop();
                    oldSong = this.playlistMinheapsArrayHeartache[newSong.playlistID].peek();
                }
//                Song oldSong = this.chosenHeartacheHeap.peek();
//                while(this.removedSongsChosenHeartache.contains(oldSong)) {
//                    this.removedSongsChosenHeartache.remove(oldSong);
//                    this.chosenHeartacheHeap.pop();
//                    oldSong = this.chosenHeartacheHeap.peek();
//                }
                emptySlotsAtLimit(this.chosenHeartacheHeap, this.heartacheHeap, this.playlistMinheapsArrayHeartache[newSong.playlistID], oldSong, newSong, "heartache", this.removedSongsChosenHeartache, this.removedSongsGeneralHeartache, false);
            }
        }
        else { // Chosen Heartache Heap is full, only replacements can be done
            Song oldSong = this.chosenHeartacheHeap.peek();  // Song with the minimum heartache score that is in Epic Blend
            while(this.removedSongsChosenHeartache.contains(oldSong)) {  // If old song is an inactive song get rid of it and choose another song
                this.removedSongsChosenHeartache.remove(oldSong);
                this.chosenHeartacheHeap.pop();
                oldSong = this.chosenHeartacheHeap.peek();
            }
            // New song has a chance to enter Epic Blend either because of having a higher score or the same score but lexicographically smaller song name
            if(newSong.heartacheScore > oldSong.heartacheScore || (oldSong.heartacheScore.equals(newSong.heartacheScore) && newSong.songName.compareTo(oldSong.songName) < 0)) {
                if(newSong.playlistID.equals(oldSong.playlistID)) {  // Both are from the same playlist

                    // Get rid of remnant songs
                    Song tmp = this.playlistMinheapsArrayHeartache[oldSong.playlistID].peek();
                    while(tmp != oldSong) {
                        this.playlistMinheapsArrayHeartache[oldSong.playlistID].pop();
                        tmp = this.playlistMinheapsArrayHeartache[oldSong.playlistID].peek();
                    }
                    this.playlistMinheapsArrayHeartache[oldSong.playlistID].pop();

                    this.playlistMinheapsArrayHeartache[newSong.playlistID].insert(newSong);

                    noEmptySlotsSamePlaylist(this.chosenHeartacheHeap, this.heartacheHeap, oldSong, newSong, "heartache", this.removedSongsChosenHeartache);
                }
                else {  // The new and old songs are from different playlists, update offered count of each playlist
                    if(playListArray[newSong.playlistID].heartacheOfferedCount < this.categoryLimit) {  // New song's playlist does not exceed offer limit for this category

                        // Get rid of remnant songs
                        Song tmp = this.playlistMinheapsArrayHeartache[oldSong.playlistID].peek();
                        while(tmp != oldSong) {
                            this.playlistMinheapsArrayHeartache[oldSong.playlistID].pop();
                            tmp = this.playlistMinheapsArrayHeartache[oldSong.playlistID].peek();
                        }
                        this.playlistMinheapsArrayHeartache[oldSong.playlistID].pop();

                        this.playlistMinheapsArrayHeartache[newSong.playlistID].insert(newSong);

                        noEmptySlotsDifferentPlaylistWithinLimit(playListArray, this.chosenHeartacheHeap, this.heartacheHeap, oldSong, newSong, "heartache", this.removedSongsChosenHeartache);
                    }
                    else {  // New song's playlist exceeds the offer limit for this category, but it may replace another song which is in the same playlist as itself

                        oldSong = this.playlistMinheapsArrayHeartache[newSong.playlistID].peek();
                        while(this.removedSongsChosenHeartache.contains(oldSong) || !oldSong.playlistID.equals(newSong.playlistID)) {
                            this.playlistMinheapsArrayHeartache[newSong.playlistID].pop();
                            oldSong = this.playlistMinheapsArrayHeartache[newSong.playlistID].peek();
                        }

                        noEmptySlotsDifferentPlaylistAtLimit(this.chosenHeartacheHeap, this.heartacheHeap, this.playlistMinheapsArrayHeartache[newSong.playlistID], oldSong, newSong, "heartache", this.removedSongsChosenHeartache, this.removedSongsGeneralHeartache, false);
                    }
                }
            }
            else {  // New song's score is not enough to make it into the heartache category of Epic Blend
                if(!this.removedSongsGeneralHeartache.contains(newSong)) {
                    this.heartacheHeap.insert(newSong);
                }
                else {
                    this.removedSongsGeneralHeartache.remove(newSong);
                    this.heartacheHeap.elementCount++;
                }
            }
        }

        // Check roadTrip category for updates
        if(this.chosenRoadTripHeap.elementCount < this.roadTripLimit) {  // There are empty slots in the Epic Blend
            if(playListArray[newSong.playlistID].roadTripOfferedCount < this.categoryLimit) {  // Category limit has not been exceeded
                emptySlotsWithinLimit(playListArray, this.chosenRoadTripHeap, this.playlistMinheapsArrayRoadTrip[newSong.playlistID], newSong, "roadTrip", this.removedSongsChosenRoadTrip);
            }
            else {  // Category limit is full but new song may replace another song from the same playlist
                Song oldSong = this.playlistMinheapsArrayRoadTrip[newSong.playlistID].peek();
                while(this.removedSongsChosenRoadTrip.contains(oldSong) || !oldSong.playlistID.equals(newSong.playlistID)) {
                    this.playlistMinheapsArrayRoadTrip[newSong.playlistID].pop();
                    oldSong = this.playlistMinheapsArrayRoadTrip[newSong.playlistID].peek();
                }
//                Song oldSong = this.chosenRoadTripHeap.peek();
//                while(this.removedSongsChosenRoadTrip.contains(oldSong)) {
//                    this.removedSongsChosenRoadTrip.remove(oldSong);
//                    this.chosenRoadTripHeap.pop();
//                    oldSong = this.chosenRoadTripHeap.peek();
//                }
                emptySlotsAtLimit(this.chosenRoadTripHeap, this.roadTripHeap, this.playlistMinheapsArrayRoadTrip[newSong.playlistID], oldSong, newSong, "roadTrip", this.removedSongsChosenRoadTrip, this.removedSongsGeneralRoadTrip, false);
            }
        }
        else { // Chosen RoadTrip Heap is full, only replacements can be done
            Song oldSong = this.chosenRoadTripHeap.peek();  // Song with the minimum roadTrip score that is in Epic Blend
            while(this.removedSongsChosenRoadTrip.contains(oldSong)) {  // If old song is an inactive song get rid of it and choose another song
                this.removedSongsChosenRoadTrip.remove(oldSong);
                this.chosenRoadTripHeap.pop();
                oldSong = this.chosenRoadTripHeap.peek();
            }
            // New song has a chance to enter Epic Blend either because of having a higher score or the same score but lexicographically smaller song name
            if(newSong.roadTripScore > oldSong.roadTripScore || (oldSong.roadTripScore.equals(newSong.roadTripScore) && newSong.songName.compareTo(oldSong.songName) < 0)) {
                if(newSong.playlistID.equals(oldSong.playlistID)) {  // Both are from the same playlist

                    // Get rid of remnant songs
                    Song tmp = this.playlistMinheapsArrayRoadTrip[oldSong.playlistID].peek();
                    while(tmp != oldSong) {
                        this.playlistMinheapsArrayRoadTrip[oldSong.playlistID].pop();
                        tmp = this.playlistMinheapsArrayRoadTrip[oldSong.playlistID].peek();
                    }
                    this.playlistMinheapsArrayRoadTrip[oldSong.playlistID].pop();

                    this.playlistMinheapsArrayRoadTrip[newSong.playlistID].insert(newSong);

                    noEmptySlotsSamePlaylist(this.chosenRoadTripHeap, this.roadTripHeap, oldSong, newSong, "roadTrip", this.removedSongsChosenRoadTrip);
                }
                else {  // The new and old songs are from different playlists, update offered count of each playlist
                    if(playListArray[newSong.playlistID].roadTripOfferedCount < this.categoryLimit) {  // New song's playlist does not exceed offer limit for this category

                        // Get rid of remnant songs
                        Song tmp = this.playlistMinheapsArrayRoadTrip[oldSong.playlistID].peek();
                        while(tmp != oldSong) {
                            this.playlistMinheapsArrayRoadTrip[oldSong.playlistID].pop();
                            tmp = this.playlistMinheapsArrayRoadTrip[oldSong.playlistID].peek();
                        }
                        this.playlistMinheapsArrayRoadTrip[oldSong.playlistID].pop();

                        this.playlistMinheapsArrayRoadTrip[newSong.playlistID].insert(newSong);

                        noEmptySlotsDifferentPlaylistWithinLimit(playListArray, this.chosenRoadTripHeap, this.roadTripHeap, oldSong, newSong, "roadTrip", this.removedSongsChosenRoadTrip);
                    }
                    else {  // New song's playlist exceeds the offer limit for this category, but it may replace another song which is in the same playlist as itself

                        oldSong = this.playlistMinheapsArrayRoadTrip[newSong.playlistID].peek();
                        while(this.removedSongsChosenRoadTrip.contains(oldSong) || !oldSong.playlistID.equals(newSong.playlistID)) {
                            this.playlistMinheapsArrayRoadTrip[newSong.playlistID].pop();
                            oldSong = this.playlistMinheapsArrayRoadTrip[newSong.playlistID].peek();
                        }

                        noEmptySlotsDifferentPlaylistAtLimit(this.chosenRoadTripHeap, this.roadTripHeap, this.playlistMinheapsArrayRoadTrip[newSong.playlistID], oldSong, newSong, "roadTrip", this.removedSongsChosenRoadTrip, this.removedSongsGeneralRoadTrip, false);
                    }
                }
            }
            else {  // New song's score is not enough to make it into the roadTrip category of Epic Blend
                if(!this.removedSongsGeneralRoadTrip.contains(newSong)) {
                    this.roadTripHeap.insert(newSong);
                }
                else {
                    this.removedSongsGeneralRoadTrip.remove(newSong);
                    this.roadTripHeap.elementCount++;
                }
            }
        }

        // Check blissful category for updates
        if(this.chosenBlissfulHeap.elementCount < this.blissfulLimit) {  // There are empty slots in the Epic Blend
            if(playListArray[newSong.playlistID].blissfulOfferedCount < this.categoryLimit) {  // Category limit has not been exceeded
                emptySlotsWithinLimit(playListArray, this.chosenBlissfulHeap, this.playlistMinheapsArrayBlissful[newSong.playlistID], newSong, "blissful", this.removedSongsChosenBlissful);
            }
            else {  // Category limit is full but new song may replace another song from the same playlist
//                if(newSong.songID==36) {
//                    System.out.println("hey2");
//                }
                Song oldSong = this.playlistMinheapsArrayBlissful[newSong.playlistID].peek();
                while(this.removedSongsChosenBlissful.contains(oldSong) || !oldSong.playlistID.equals(newSong.playlistID)) {
                    this.playlistMinheapsArrayBlissful[newSong.playlistID].pop();
                    oldSong = this.playlistMinheapsArrayBlissful[newSong.playlistID].peek();
                }
//                if(newSong.songID==36) {
//                    System.out.println(oldSong.songID);
//                }
//                Song oldSong = this.chosenBlissfulHeap.peek();
//                while(this.removedSongsChosenBlissful.contains(oldSong)) {
//                    this.removedSongsChosenBlissful.remove(oldSong);
//                    this.chosenBlissfulHeap.pop();
//                    oldSong = this.chosenBlissfulHeap.peek();
//                }
                emptySlotsAtLimit(this.chosenBlissfulHeap, this.blissfulHeap, this.playlistMinheapsArrayBlissful[newSong.playlistID], oldSong, newSong, "blissful", this.removedSongsChosenBlissful, this.removedSongsGeneralBlissful, false);
            }
        }
        else { // Chosen Blissful Heap is full, only replacements can be done
            Song oldSong = this.chosenBlissfulHeap.peek();  // Song with the minimum blissful score that is in Epic Blend
            while(this.removedSongsChosenBlissful.contains(oldSong)) {  // If old song is an inactive song get rid of it and choose another song
                this.removedSongsChosenBlissful.remove(oldSong);
                this.chosenBlissfulHeap.pop();
                oldSong = this.chosenBlissfulHeap.peek();
            }
            // New song has a chance to enter Epic Blend either because of having a higher score or the same score but lexicographically smaller song name
            if(newSong.blissfulScore > oldSong.blissfulScore || (oldSong.blissfulScore.equals(newSong.blissfulScore) && newSong.songName.compareTo(oldSong.songName) < 0)) {
                if(newSong.playlistID.equals(oldSong.playlistID)) {  // Both are from the same playlist

                    // Get rid of remnant songs
                    Song tmp = this.playlistMinheapsArrayBlissful[oldSong.playlistID].peek();
                    while(tmp != oldSong) {
                        this.playlistMinheapsArrayBlissful[oldSong.playlistID].pop();
                        tmp = this.playlistMinheapsArrayBlissful[oldSong.playlistID].peek();
                    }
                    this.playlistMinheapsArrayBlissful[oldSong.playlistID].pop();

                    this.playlistMinheapsArrayBlissful[newSong.playlistID].insert(newSong);

                    noEmptySlotsSamePlaylist(this.chosenBlissfulHeap, this.blissfulHeap, oldSong, newSong, "blissful", this.removedSongsChosenBlissful);
                }
                else {  // The new and old songs are from different playlists, update offered count of each playlist
                    if(playListArray[newSong.playlistID].blissfulOfferedCount < this.categoryLimit) {  // New song's playlist does not exceed offer limit for this category

                        // Get rid of remnant songs
                        Song tmp = this.playlistMinheapsArrayBlissful[oldSong.playlistID].peek();
                        while(tmp != oldSong) {
                            this.playlistMinheapsArrayBlissful[oldSong.playlistID].pop();
                            tmp = this.playlistMinheapsArrayBlissful[oldSong.playlistID].peek();
                        }
                        this.playlistMinheapsArrayBlissful[oldSong.playlistID].pop();

                        this.playlistMinheapsArrayBlissful[newSong.playlistID].insert(newSong);

                        noEmptySlotsDifferentPlaylistWithinLimit(playListArray, this.chosenBlissfulHeap, this.blissfulHeap, oldSong, newSong, "blissful", this.removedSongsChosenBlissful);
                    }
                    else {  // New song's playlist exceeds the offer limit for this category, but it may replace another song which is in the same playlist as itself

                        oldSong = this.playlistMinheapsArrayBlissful[newSong.playlistID].peek();
                        while(this.removedSongsChosenBlissful.contains(oldSong) || !oldSong.playlistID.equals(newSong.playlistID)) {
                            this.playlistMinheapsArrayBlissful[newSong.playlistID].pop();
                            oldSong = this.playlistMinheapsArrayBlissful[newSong.playlistID].peek();
                        }

                        noEmptySlotsDifferentPlaylistAtLimit(this.chosenBlissfulHeap, this.blissfulHeap, this.playlistMinheapsArrayBlissful[newSong.playlistID], oldSong, newSong, "blissful", this.removedSongsChosenBlissful, this.removedSongsGeneralBlissful, false);
                    }
                }
            }
            else {  // New song's score is not enough to make it into the blissful category of Epic Blend
                if(!this.removedSongsGeneralBlissful.contains(newSong)) {
                    this.blissfulHeap.insert(newSong);
                }
                else {
                    this.removedSongsGeneralBlissful.remove(newSong);
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
        for(Song song:this.inChosenHeartache) {
            songsCombined.add(song);
        }

        for(Song song:this.inChosenRoadTrip) {
            songsCombined.add(song);
        }

        for(Song song:this.inChosenBlissful) {
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
        int pivotInt = songsCombinedFinal[high].playCount;
        String pivotName = songsCombinedFinal[high].songName;
        int i = low - 1;
        for(int j=low; j<high; j++) {
            if(songsCombinedFinal[j].playCount > pivotInt || (songsCombinedFinal[j].playCount == pivotInt && songsCombinedFinal[j].songName.compareTo(pivotName) < 0)) {
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
