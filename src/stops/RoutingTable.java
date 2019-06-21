package stops;

import java.util.*;

/**
 * The class should map stopRoutingTable stops to RoutingEntry objects.
 *
 * The table is able to redirect passengers from their current stop to the
 * next intermediate stop which they should go to in order to reach their final
 * stopRoutingTable.
 */
public class RoutingTable {
    //initial stop of this table
    private Stop initialStop;
    //the table that handles destination and their entries
    private Map<Stop, RoutingEntry> stopRoutingTable = new HashMap<>();

    /**
     * Creates a new RoutingTable for the given stop.
     * The routing table should be created with an entry for its initial stop
     * (i.e. a mapping from the stop to a RoutingEntry.RoutingEntry() for that
     * stop with a cost of zero (0).
     * @param initialStop The stop for which this table will handle routing.
     */
    public RoutingTable(Stop initialStop){
        this.stopRoutingTable.put(initialStop, new RoutingEntry(initialStop,
                0));
        this.initialStop = initialStop;
    }

    /**
     * Adds the given stop as a neighbour of the stop stored in this table.
     * A neighbouring stop should be added as a destination in this table, with
     * the cost to reach that destination simply being the Manhattan distance
     * between this table's stop and the given neighbour stop.
     *
     * If the given neighbour already exists in the table, it should be updated
     * (as defined in addOrUpdateEntry(Stop, int, Stop)).
     *
     * The 'intermediate'/'next' stop between this table's stop and the new
     * neighbour stop should simply be the neighbour stop itself.
     *
     * Once the new neighbour has been added as an entry, this table should be
     * synchronised with the rest of the network using the synchronise() method.
     * @param neighbour The stop to be added as a neighbour.
     */
    public void addNeighbour(Stop neighbour){
        //cheking if this table contains the given neighbour
        if (!(this.stopRoutingTable.containsKey(neighbour))) {
            //now it's not there, we add it and synchronise the table
            if (this.addOrUpdateEntry(neighbour,
                    this.getStop().distanceTo(neighbour),
                    this.getStop())) {
                this.synchronise();
            }
            //If there the neighbour exist, we just update it based on its cost
        } else if ((this.addOrUpdateEntry(neighbour,
                this.costTo(neighbour), neighbour))) {
            return;
        }
    }

    /**
     * If there is currently no entry for the destination in the table, a new
     * entry for the given destination should be added, with a RoutingEntry for
     * the given cost and next (intermediate) stop.
     * If there is already an entry for the given destination, and the newCost
     * is lower than the current cost associated with the destination, then the
     * entry should be updated to have the given newCost and next (intermediate)
     * stop.
     *
     * If there is already an entry for the given destination, but the newCost
     * is greater than or equal to the current cost associated with the
     * destination, then the entry should remain unchanged.
     * @param destination The destination stop to add/update the entry.
     * @param newCost The new cost to associate with the new/updated entry
     * @param intermediate The new intermediate/next stop to associate with the
     *                     new/updated entry
     * @return True if a new entry was added, or an existing one was updated,
     * or false if the table remained unchanged.
     */
    public boolean addOrUpdateEntry(Stop destination, int newCost,
                                    Stop intermediate) {
        //checking if the destination is there, otherwise we add it to the tab..
        if (this.stopRoutingTable.containsKey(destination)) {
            if (newCost >= this.costTo(destination)) {
                return false;
            }
            //replace the old value with the new one.
            return (this.stopRoutingTable.replace(destination,
                     this.stopRoutingTable.get(destination),new
                    RoutingEntry(intermediate, newCost)));
        }
        this.stopRoutingTable.put(destination,new RoutingEntry(intermediate,
                newCost));
        return true;
    }

    /**
     * Returns the cost associated with getting to the given stop.
     * @param stop The stop to get the cost.
     * @return The cost to the given stop, or Integer.MAX_VALUE if the stop is
     * not currently in this routing table.
     */
    public int costTo(Stop stop) {
        if (stopRoutingTable.containsKey(stop)) {
            return this.stopRoutingTable.get(stop).getCost();
        }
        return Integer.MAX_VALUE;
    }

    /**
     * Maps each destination stop in this table to the cost associated with
     * getting to that destination.
     * @return A mapping from destination stops to the costs associated with
     * getting to those stops.
     */
    public Map<Stop, Integer> getCosts() {
        Map<Stop,Integer> stopCost = new HashMap<>();
        for (Stop stop: stopRoutingTable.keySet()) {
            stopCost.put(stop, this.costTo(stop));
        }
        return new HashMap<>(stopCost);
    }

    /**
     * Return the stop for which this table will handle routing.
     * @return this table's stop
     */
    public Stop getStop() {
        return this.initialStop;
    }

    /**
     * Returns the next intermediate stop which passengers should be routed to
     * in order to reach the given destination. If the given stop is null or not
     * in the table, then return null
     * @param destination The destination which the passengers are being routed.
     * @return The best stop to route the passengers to in order to reach the
     * given destination.
     */
    public Stop nextStop(Stop destination) {
        if (destination != null && getCosts().containsKey(destination)) {
            return this.stopRoutingTable.get(destination).getNext();
        }
        return null;
    }

    /**
     * Synchronises this routing table with the other tables in the network.
     * In each iteration, every stop in the network which is reachable by this
     * table's stop (as returned by traverseNetwork()) must be considered. For
     * each stop x in the network, each of its neighbours must be visited, and
     * the entries from x must be transferred to each neighbour (using the
     * transferEntries(Stop) method).
     *
     * If any of these transfers results in a change to the table that the
     * entries are being transferred, then the entire process must be repeated
     * again. These iterations should continue happening until no changes occur
     * to any of the tables in the network.
     *
     * This process is designed to handle changes which need to be propagated
     * throughout the entire network, which could take more than one iteration.
     */
    public void synchronise() {
        //two boolean variables which control the iteration.
        boolean valid = false;
        boolean isValid;

        // a new integer tester, to keep track of synchronization
        int validTest = 0;
        do {
            for (Stop stopHere: this.traverseNetwork()){
                for (Stop stop: stopHere.getNeighbours()) {
                    isValid = stopHere.getRoutingTable().transferEntries(stop);
                    if (isValid) {
                        validTest += 1;
                        valid = true;
                    }
                }
            }
            //check if no change occured. If so, the synchronization is
            // completed.
            if(validTest == 0){
                valid = false;
            }
            //we reset the tester at every loop
            validTest = 0;
        } while (valid);
    }

    /**
     * Updates the entries in the routing table of the given other stop, with
     * the entries from this routing table.
     * If this routing table has entries which the other stop's table doesn't,
     * then the entries should be added to the other table (as defined in
     * addOrUpdateEntry(Stop, int, Stop)) with the cost being updated to include
     * the distance.
     *
     * If this routing table has entries which the other stop's table does have,
     * and the new cost would be lower than that associated with its existing
     * entry, then its entry should be updated (as defined in
     * addOrUpdateEntry(Stop, int, Stop)).
     *
     * If this routing table has entries which the other stop's table does
     * have, but the new cost would be greater than or equal to that associated
     * with its existing entry, then its entry should remain unchanged.
     *
     * @require this.getStop().getNeighbours().contains(other) == true
     * @param other The stop whose routing table this table's entries should be
     *        transferred.
     * @return True if any new entries were added to the other stop's table, or
     * if any of its existing entries were updated, or false if the other stop's
     * table remains unchanged.
     */
    public boolean transferEntries(Stop other) {
        boolean validOrInvalid = false;

        if (this.getStop().getNeighbours().contains(other)) {
            boolean isValid;
            //iterate over this table's stops
            for (Stop stop : this.stopRoutingTable.keySet()) {
                int fare = this.costTo(stop) + this.getStop().distanceTo(
                        other);
                isValid = other.getRoutingTable().addOrUpdateEntry(
                        stop, fare, this.getStop());
                //make sure that we return true if a sop is successfully added.
                if (isValid) {
                    validOrInvalid = true;
                }
            }
            return validOrInvalid;
        }
        return validOrInvalid;
    }

    /**
     * Performs a traversal of all the stops in the network, and returns a
     * list of every stop which is reachable from the stop stored in this table.
     * Firstly create an empty list of Stops and an empty Stack of Stops.
     * Push the RoutingTable's Stop on to the stack.
     * While the stack is not empty,
     * pop the top Stop (current) from the stack.
     * For each of that stop's neighbours,
     * if they are not in the list, add them to the stack.
     * Then add the current Stop to the list.
     * Return the list of seen stops.
     * @return All of the stops in the network which are reachable by the stop
     * stored in this table.
     */
    public List<Stop> traverseNetwork() {
        Stack<Stop> stopStack = new Stack<>();
        List<Stop> stops = new ArrayList<>();
        stopStack.push(getStop());

        while(!stopStack.isEmpty()){
            stopStack.pop();
            for(Stop stop: this.getCosts().keySet()){
                if(!stops.contains(stop)){
                    stopStack.push(stop);
                }
                stops.add(stop);
            }
        }
        return new ArrayList<>(stops);
    }
}
