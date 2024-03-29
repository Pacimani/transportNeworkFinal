package stops;

/**
 *  Represents an entry in a routing table.
 * Each entry contains information for how to route passengers to a specific
 * destination. This information includes the best intermediate stop to route
 * passengers to in order to get them to that destination, as well as the cost
 * (in Manhattan distance) required to reach the destination when going via that
 * intermediate stop.
 *
 * Constructor Summary
 */
public class RoutingEntry  {
    //the cost and the next stop in the entry
    private int cost;
    private Stop next;

    /**
     * Creates a new default RoutingEntry object. The next stop should be
     * stored as null, and the cost should be stored as Integer.MAX_VALUE.
     * These default values are used to represent a destination in a routing
     * table to which there is currently no known path.
     */
    public RoutingEntry(){
        this.next = null;
        this.cost = Integer.MAX_VALUE;
    }

    /**
     * Creates a new RoutingEntry object with the given next stop and cost for
     * the given destination.
     * If the given stop is null, or if the given cost is negative, then the
     * default values should be used for BOTH next stop and cost instead
     * (that is, null for next stop and Integer.MAX_VALUE for cost).
     * @param next The next intermediate stop to go to in order to reach the
     *             destination.
     * @param cost The cost to get to the destination.
     */
    public RoutingEntry(Stop next, int cost) {
        //Calling the default construct if there is null stop or
        //cost is less than 0
        this();

        //Override the it if cost > 0 and stop is not null
        if(next != null && cost >= 0){
            this.next = next;
            this.cost = cost;
        }
    }

    /**
     *Returns the cost to get to the destination associated with this entry in
     *  the routing table.
     * @return The cost associated with this entry.
     */
    public int getCost() {
        return this.cost;
    }

    /**
     * Returns the next stop to be visited in order to reach the destination,
     * or null if there is no known route to reach the destination associated
     * with this entry in the routing table.
     * @return The next stop, or null if there is no known route to the
     * destination.
     */
    public Stop getNext(){
        return this.next;
    }

}

