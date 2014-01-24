/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.uhk.thesis.model;

import cz.uhk.thesis.core.Core;

/**
 *
 * @author Filip Valenta
 */
public class LltdQdDiscoveryPacket extends LltdPacket {
    
    public LltdQdDiscoveryPacket(Core c) {
        super(
            c, 
            LltdHeader.HEADER_VERSION_1, 
            LltdHeader.HEADER_SERVICE_QUICK_DISCOVERY,
            LltdHeader.HEADER_RESERVED,
            LltdHeader.HEADER_FUNCTION_DISCOVER
        );
        
        // TODO: setXid((short)0x6989);
    }
    
}
