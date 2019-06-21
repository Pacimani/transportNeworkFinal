package stops;

import org.junit.Before;
import org.junit.Test;
import passengers.Passenger;
import routes.BusRoute;
import routes.Route;
import vehicles.Bus;
import vehicles.PublicTransport;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class RoutingTableTest {

    private Stop stop;
    private Stop stop1;
    private Stop stop2;
    private Stop stop3;
    private PublicTransport vehicle;
    private Passenger passenger;
    private Route route;
    private RoutingTable table;



    @Before
    public void setUp() throws Exception {
        stop = new Stop("UQLake",5,3);
        stop1 = new Stop("CampJil", 0, 2);
        stop2 = new Stop("Papi", 0 ,2);
        stop3 = new Stop("Milo", 2, 0);
        passenger = new Passenger("Imani",stop);
        route = new BusRoute("Goma",3);
        vehicle = new Bus(34,20,route,"ABC124");
        table = new RoutingTable(stop);
    }

    @Test
    public void addNeighbour() {
        //add one stop and check if it is one of its initial stops
        table.addNeighbour(stop1);
        assertTrue(table.traverseNetwork().contains(stop1));
        //check if the added stop is part of the destination
        assertTrue(table.getCosts().keySet().contains(stop1));
        //add two more neighbours and chekc if they exist
        table.addNeighbour(stop2);
        table.addNeighbour(stop3);
        assertEquals(4,table.getCosts().keySet().size());
        //add an existing neighbour and check if it's is aboudated
        table.addNeighbour(stop2);
        assertEquals(4,table.getCosts().keySet().size());

    }

    @Test
    public void addOrUpdateEntry() {
        //checking for a null destination.
        assertTrue(table.addOrUpdateEntry(null,6,stop));
        //check for not existing stop
        assertTrue(table.addOrUpdateEntry(stop1,23,stop));
        //check for existing stop with less cost
        assertTrue(table.addOrUpdateEntry(stop1,4,stop));
        //check for existing cost with a higher cost
        assertFalse(table.addOrUpdateEntry(stop1,45,stop));
        //add an non existing stop on the tabl
        assertTrue(table.addOrUpdateEntry(stop2,45,stop));
    }

    @Test
    public void costTo() {
        //check the cost for this table's stop
        assertEquals(0, table.costTo(stop));
        //chekc for null value
        assertEquals(Integer.MAX_VALUE, table.costTo(null));
        //check for nun-existing stop on this table
        assertEquals(Integer.MAX_VALUE, table.costTo(stop1));
        //now add a stop on this table and check its cost.
        table.addNeighbour(stop1);
        assertEquals(table.getStop().distanceTo(stop1), table.costTo(stop1));

    }

    @Test
    public void getCosts() {
        //Testing for the empty table
        assertFalse(table.getCosts().isEmpty());
        //testing if the table contains its initial stop
        assertTrue(table.getCosts().keySet().contains(stop));
        //testing if the length of the table contains only its initial stop
        assertEquals(1,table.getCosts().keySet().size());
        table.addNeighbour(stop1);
        table.addNeighbour(stop2);
        table.addNeighbour(stop3);
        //add stops and check if the added is there
        assertTrue(table.getCosts().containsKey(stop1));
        table.addNeighbour(stop2);
        //add existing stop to the table and check if it has been added or updat
        assertEquals(4,table.getCosts().keySet().size());

    }

    @Test
    public void getStop() {
        //try adding a null value
        assertFalse(table.getStop().equals(null));
        //check if the initial stop has been added successfully
        assertTrue(table.getStop().equals(stop));
        //check for non existing table stop
        assertFalse(table.getStop().equals(stop1));
    }

    @Test
    public void nextStop() {
        //test for the null value
        assertTrue(table.nextStop(null) == null);
        //test for non existing stop on this table
        assertTrue(table.nextStop(stop2) == null);
        //test for this table stop
        assertTrue(table.nextStop(stop).equals(stop));
        //add a neighbouring stop and check its inter.. which is this table's st
        table.addNeighbour(stop1);
        assertTrue(table.nextStop(stop1).equals(stop));
        //update an non-existing stop and check its intermediate
        table.addOrUpdateEntry(stop2,45,stop3);
        assertTrue(table.nextStop(stop2).equals(stop3));

        Stop stop4 = new Stop("Me", 8, 90);
        table = new RoutingTable(stop);
        table.addNeighbour(stop4);
        assertTrue(table.nextStop(stop4).equals(table.getStop()));

    }

    @Test
    public void synchronise() {
        //create stops to test the synchronise
        Stop stop4 = new Stop("Me", 8, 90);
        stop4.getRoutingTable().addOrUpdateEntry(stop1,45,stop);
        //add an non existing stop on the table
        stop4.getRoutingTable().addOrUpdateEntry(stop3,90,stop);
        //add
        stop4.getRoutingTable().addOrUpdateEntry(stop2,45,stop3);
        //create the second stops
        Stop stop5 = new Stop("You", 45, 8);
        Stop stop6 = new Stop("Lick", 4,8);
        Stop stop7 = new Stop("Mek", 9, 7);
        stop5.getRoutingTable().addOrUpdateEntry(stop6,2,stop7);
        stop5.getRoutingTable().addOrUpdateEntry(stop7,6,stop5);
        //add stop 5 as a synchronisation happens after adding the neighbourto
        stop4.addNeighbouringStop(stop5);
        //Now sychronisation has happen let's see if stop in 4 were synchronise in 5
        assertTrue(stop5.getRoutingTable().getCosts().containsKey(stop2));
        //Use another stops
        stop3.getRoutingTable().addOrUpdateEntry(stop4,34,stop5);
        stop3.getRoutingTable().addOrUpdateEntry(stop5,7,stop3);

        //now let's synchronise stop 3
        stop3.getRoutingTable().synchronise();
        //now check if stop from stop4 are in stop 3
        assertFalse(stop3.getRoutingTable().traverseNetwork().contains(stop1));
        // now we
        //check for non neighbouring stop if the transfer occurs
        assertFalse(stop5.getRoutingTable().transferEntries(stop4));
        //testing for a neighbouring stop
        stop5.addNeighbouringStop(stop4);
        assertTrue(stop5.getRoutingTable().transferEntries(stop4));
        //trying to transfer entry to a stop which is not a neighbour
        Stop stop8 = new Stop("Love", 9, 8);
        //test for a stop which is not there
        assertFalse(stop5.getRoutingTable().transferEntries(stop8));
        //we add a stop and it will synchronise automatically. check if it sync happened
        stop5.addNeighbouringStop(stop8);
        assertTrue(stop8.getRoutingTable().getCosts().containsKey(stop7));
    }

    @Test
    public void transferEntries() {
        //testing a null stop and check if the entries are transfered
        assertFalse(table.transferEntries(null));
        //check for non existing stop
        assertFalse(table.transferEntries(stop1));
        //add an non existing stop to the table anc check if the transfer occurs
        table.addNeighbour(stop1);
        table.addNeighbour(stop2);
        table.addNeighbour(stop3);
        table.addNeighbour(stop2);
        stop1.addNeighbouringStop(stop2);
        assertFalse(table.transferEntries(stop1));
        //using different stops and check if the transfer happens
        Stop stop4 = new Stop("Me", 8, 90);
        stop4.getRoutingTable().addOrUpdateEntry(stop1,45,stop);
        //add an non existing stop on the table
        stop4.getRoutingTable().addOrUpdateEntry(stop3,90,stop);
        //add a non

        //Using table to see if the entries have been transfered
        assertFalse(table.transferEntries(stop1));
        //add the stop above as a neighbour to the table's stop and test it
        stop.addNeighbouringStop(stop1);
        assertTrue(table.transferEntries(stop1));

        //create new stop and test on stops only
        Stop stop5 = new Stop("You", 45, 8);
        Stop stop6 = new Stop("Lick", 4,8);
        Stop stop7 = new Stop("Mek", 9, 7);
        stop5.getRoutingTable().addOrUpdateEntry(stop6,2,stop7);
        stop5.getRoutingTable().addOrUpdateEntry(stop7,6,stop5);
        //check for non neighbouring stop if the transfer occurs
        stop4.addNeighbouringStop(stop5);
        assertFalse(stop5.getRoutingTable().transferEntries(stop4));
        //testing for a neighbouring stop
        stop5.addNeighbouringStop(stop4);
        assertTrue(stop5.getRoutingTable().transferEntries(stop4));
        //trying to transfer entry to a stop which is not a neighbour
        Stop stop8 = new Stop("Love", 9, 8);
        assertFalse(stop5.getRoutingTable().transferEntries(stop8));


    }

    @Test
    public void traverseNetwork() {
        //testing if this table's contain its initial stop
        assertFalse(stop.getRoutingTable().traverseNetwork().isEmpty());
        //testing if for non-existing stop on this table
        assertFalse(table.traverseNetwork().contains(stop1));
        //confirm the size of this table
        //add stops and check if it they exist in this table
        assertTrue(table.traverseNetwork().contains(stop));
        table.addNeighbour(stop1);
        table.addNeighbour(stop2);
        table.addNeighbour(stop3);
        assertTrue(table.traverseNetwork().contains(stop1));
        //create a list of stops, andd stops and check if they are there
        List<Stop> traversal = new LinkedList<>();
        traversal.add(stop1);
        traversal.add(stop2);
        traversal.add(stop3);
        assertTrue(table.traverseNetwork().containsAll(traversal));
        //test if a null value is in the list after transverse
        assertFalse(table.traverseNetwork().contains(null));



    }
}