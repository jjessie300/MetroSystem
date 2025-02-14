import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class McMetroTest {

    @Test
    void testMaxPassengers() {
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 200)
        };

        Track[] tracks = new Track[]{
                new Track(new TrackID(1), bid1, bid2, 100, 50)
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        int maxPassengers = mcMetro.maxPassengers(bid1, bid2);
        assertEquals(50, maxPassengers);
    }

    @Test
    void testBestMetro() {
        BuildingID bid1 = new BuildingID(1);
        BuildingID bid2 = new BuildingID(2);
        Building[] buildings = new Building[]{
                new Building(bid1, 100),
                new Building(bid2, 200)
        };

        TrackID tid = new TrackID(1);
        Track[] tracks = new Track[]{
                new Track(tid, bid1, bid2, 100, 50)
        };

        McMetro mcMetro = new McMetro(tracks, buildings);
        TrackID[] bms = mcMetro.bestMetroSystem();
        TrackID[] expected = new TrackID[]{tid};
        assertArrayEquals(expected, bms);
    }

    @Test
    void testSearchForPassengers() {
        McMetro mcMetro = new McMetro(new Track[0], new Building[0]);
        String[] passengers = {"Alex", "Bob", "Ally"};
        String[] expected = {"Alex", "Ally"};
        mcMetro.addPassengers(passengers);

        ArrayList<String> found = mcMetro.searchForPassengers("al");
        assertArrayEquals(expected, found.toArray(new String[0]));
    }

    @Test
    void testHireTicketCheckers() {
        int[][] schedule = new int[4][2];
        schedule[0][0] = 1;
        schedule[0][1] = 2;
        schedule[1][0] = 2;
        schedule[1][1] = 3;
        schedule[2][0] = 3;
        schedule[2][1] = 4;
        schedule[3][0] = 1;
        schedule[3][1] = 3;

        int toHire = McMetro.hireTicketCheckers(schedule);
        assertEquals(3, toHire);
    }


}