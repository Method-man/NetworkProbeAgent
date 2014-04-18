/*
 * 
 */
package org.hkfree.topoagent.domain;

import org.hkfree.topoagent.core.Core;

/**
 *
 * @author Filip Valenta
 */
public class LltdQdResetPacket extends LltdPacket {

    public LltdQdResetPacket(Core c) {
        super(
                c,
                LltdHeader.HEADER_VERSION_1,
                LltdHeader.HEADER_SERVICE_QUICK_DISCOVERY , // kolega ma TD nikoli QD
                LltdHeader.HEADER_RESERVED,
                LltdHeader.HEADER_FUNCTION_RESET,
                60
        );
    }

}
