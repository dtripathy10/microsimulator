package edu.iitb.civil.tse.network;

import edu.iitb.civil.tse.vehicle.TurningMovement;

/**
 *
 * @author Debabrata Tripathy
 */
class LinkConnectivity {

    public Link inLink;
    public Link outLink;
    public TurningMovement turningMovement;
    public String id;

    public LinkConnectivity(Link inLink, Link outLink, TurningMovement turningMovement) {
        this.inLink = inLink;
        this.outLink = outLink;
        this.turningMovement = turningMovement;
    }
    
}
