package org.hkfree.topoagent.interfaces;

/**
 *
 * @author Filip Valenta
 */
public abstract class Stateful {

    public static final int STATE_INITIAL = 0;

    protected int state = STATE_INITIAL;

    public abstract void setState(int state);

    public boolean isInState(int state) {
        return state == this.state;
    }

}
