/*
 * 
 */

package cz.uhk.thesis.model;

import cz.uhk.thesis.core.Core;

/**
 *
 * @author Filip Valenta
 */
public class LltdQdResetPacket extends LltdPacket {
    
    public LltdQdResetPacket(Core c) {
        super(
            c, 
            LltdHeader.HEADER_VERSION_1, 
            LltdHeader.HEADER_SERVICE_TOPOLOGY_DISCOVERY, // kolega ma TD nikoli QD
            LltdHeader.HEADER_RESERVED,
            LltdHeader.HEADER_FUNCTION_RESET
        );
    }
    
}
