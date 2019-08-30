package com.branwilliams.bundi.voxel.components;

import com.branwilliams.bundi.engine.core.Keycode;

/**
 * @author Brandon
 * @since August 15, 2019
 */
public class PlayerControls {

    private Keycode forward;
    private Keycode left;
    private Keycode backward;
    private Keycode right;
    private Keycode ascend;
    private Keycode descend;
    private Keycode pause;
    private Keycode updateSun;
    private Keycode noclip;

    public PlayerControls(Keycode forward, Keycode left, Keycode backward, Keycode right, Keycode ascend,
                          Keycode descend, Keycode pause, Keycode updateSun, Keycode noclip) {
        this.forward = forward;
        this.left = left;
        this.backward = backward;
        this.right = right;
        this.ascend = ascend;
        this.descend = descend;
        this.pause = pause;
        this.updateSun = updateSun;
        this.noclip = noclip;
    }

    public Keycode getForward() {
        return forward;
    }

    public void setForward(Keycode forward) {
        this.forward = forward;
    }

    public Keycode getLeft() {
        return left;
    }

    public void setLeft(Keycode left) {
        this.left = left;
    }

    public Keycode getBackward() {
        return backward;
    }

    public void setBackward(Keycode backward) {
        this.backward = backward;
    }

    public Keycode getRight() {
        return right;
    }

    public void setRight(Keycode right) {
        this.right = right;
    }

    public Keycode getAscend() {
        return ascend;
    }

    public void setAscend(Keycode ascend) {
        this.ascend = ascend;
    }

    public Keycode getDescend() {
        return descend;
    }

    public void setDescend(Keycode descend) {
        this.descend = descend;
    }

    public Keycode getPause() {
        return pause;
    }

    public void setPause(Keycode pause) {
        this.pause = pause;
    }

    public Keycode getUpdateSun() {
        return updateSun;
    }

    public void setUpdateSun(Keycode updateSun) {
        this.updateSun = updateSun;
    }

    public Keycode getNoclip() {
        return noclip;
    }

    public void setNoclip(Keycode noclip) {
        this.noclip = noclip;
    }
}
