
package org.hkfree.topoagent.interfaces;

/**
 *
 * @author Filip Valenta
 */
public abstract class Stateful {
    
    public static final int STATE_INITIAL = 0;
    
    protected int state = STATE_INITIAL;
    
    public abstract void SetState(int state);
    
    public boolean IsInState(int state) {
        return state == this.state;
    }
    
}
